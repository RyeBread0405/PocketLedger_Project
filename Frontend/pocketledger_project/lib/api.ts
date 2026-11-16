import { getToken, clearToken } from "./auth";

//Backend URL
export const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL ?? "http://localhost:8080";

//Login and register do not need a token
const PUBLIC_PATHS = ["/api/auth/login", "/api/auth/register"];

//Store the error status, code, and message
export class ApiRequestError extends Error {
  status: number;
  code: string;

  constructor(status: number, code: string, message: string) {
    super(message);
    this.status = status;
    this.code = code;
  }
}

//Option to turn off automatic redirect when getting 401
export interface ApiOptions extends RequestInit {
  redirectOn401?: boolean;
}

//Show error message based on the status code
function defaultMessage(status: number): string {
  if (status === 400) return "The request is not valid.";
  if (status === 401) return "Your session has expired. Please log in again.";
  if (status === 403) return "You don't have access to this.";
  if (status === 404) return "Not found.";
  if (status === 409) return "This conflicts with existing data.";
  if (status >= 500) return "Server error. Please try again.";
  return `Request failed (${status}).`;
}

//Send requests to the backend and handle the response
export async function apiFetch<T = unknown>(path: string, options: ApiOptions = {}): Promise<T> {
  const { redirectOn401 = true, ...init } = options;
  const isPublic = PUBLIC_PATHS.some((p) => path.startsWith(p));

  //Set the request headers
  const headers = new Headers(init.headers);
  if (init.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  //Get the saved token and add it to requests that need login
  const token = getToken();
  if (token && !isPublic) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  //Try to connect to the backend
  let res: Response;
  try {
    res = await fetch(`${BACKEND_URL}${path}`, { ...init, headers });
  } catch {
    //Show an error if the backend cannot be reached
    throw new ApiRequestError(0, "NETWORK_ERROR", "Could not reach the server.");
  }

  //If the token is invalid or expired, remove it and go to login
  if (res.status === 401 && !isPublic) {
    clearToken();
    if (redirectOn401 && window.location.pathname !== "/login") {
      //Reload the page to clear the old login state
      // eslint-disable-next-line @next/next/no-location-assign-relative-destination
      window.location.href = "/login";
    }
    throw new ApiRequestError(401, "UNAUTHORIZED", defaultMessage(401));
  }

  //Read the response, 204 means there is no data
  const text = res.status === 204 ? "" : await res.text();
  let data: unknown = undefined;

  //Try to turn the response into JSON
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  //Check if the backend returned an error
  if (!res.ok) {
    //Use the backend error message if it is available
    const err = (data as { error?: { code?: string; message?: string } } | undefined)?.error;
    if (err && typeof err === "object") {
      throw new ApiRequestError(res.status, err.code ?? "ERROR", err.message ?? defaultMessage(res.status));
    }
    throw new ApiRequestError(res.status, "HTTP_" + res.status, defaultMessage(res.status));
  }

  //Return the data if the request was successful
  return data as T;
}