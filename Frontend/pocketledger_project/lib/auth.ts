const TOKEN_KEY = "token";

export function saveToken(token: string) {//Save the token after login
  localStorage.setItem(TOKEN_KEY, token);
}
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}
export function clearToken() {//Remove the token when logging out
  localStorage.removeItem(TOKEN_KEY);
}
export function authHeaders(): HeadersInit {//Attach the token to a request
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
}