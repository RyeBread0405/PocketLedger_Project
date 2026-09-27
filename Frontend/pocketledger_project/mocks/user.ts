import type { User, MeResponse } from "../types/api";
export const mockUser: User = {//Mock logged-in student
  id: 1,
  email: "student@okstate.edu",
  homeCurrency: "USD",
};
export const mockMeResponse: MeResponse = {//Matches the GET /api/auth/me response
  user: mockUser,
};