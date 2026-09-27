import type { Account } from "../types/api";
export const mockAccounts: Account[] = [//Mock accounts used for frontend development
  { id: 1, name: "Checking", type: "checking", currency: "USD" },
  { id: 2, name: "Student Credit Card", type: "credit", currency: "USD" },
  { id: 3, name: "Cash", type: "cash", currency: "USD" },
  { id: 4, name: "Travel Card", type: "credit", currency: "EUR" },
  { id: 5, name: "Savings", type: "savings", currency: "USD" },
];
export const mockAccountsResponse = {//Matches the GET /api/accounts response
  accounts: mockAccounts,
};