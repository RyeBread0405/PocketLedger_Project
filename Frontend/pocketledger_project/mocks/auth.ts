import type { AuthResponse, ApiError, LoginRequest, RegisterRequest } from "../types/api";
import { mockUser } from "./user";
export const mockPassword = "password123";//Password that works with mockUser when testing login
export const mockAuthResponse: AuthResponse = {//Matches the POST /api/auth/login and /api/auth/register response
  token: "fake-token-123",
  user: mockUser,
};
export const mockLoginError: ApiError = {//Matches the 401 response when the email or password is wrong
  error: { code: "INVALID_CREDENTIALS", message: "Email or password is incorrect." },
};
export const mockEmailTakenError: ApiError = {//Matches the 409 response when the email is already registered
  error: { code: "EMAIL_TAKEN", message: "This email is already registered. Try signing in." },
};
export async function mockLogin(req: LoginRequest): Promise<AuthResponse> {//Pretends to be the login endpoint
  if (req.email === mockUser.email && req.password === mockPassword) {
    return mockAuthResponse;
  }
  throw new Error(mockLoginError.error.message);
}
export async function mockRegister(req: RegisterRequest): Promise<AuthResponse> {//Pretends to be the register endpoint
  if (req.email === mockUser.email) {
    throw new Error(mockEmailTakenError.error.message);
  }
  return {//New users default to USD
    token: mockAuthResponse.token,
    user: { id: 2, name: req.name, email: req.email, homeCurrency: "USD" },
  };
}
