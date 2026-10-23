export type CategorySource = "user" | "rule" | "ai" | "default";
export type AccountType =
  | "checking"
  | "savings"
  | "credit"
  | "cash";
export interface UserSettings{ //User preferences, stored as JSON in the database
  homeCurrency: string;
}
export interface User {//Basic user information returned after login
  id: number;
  email: string;
  settings: UserSettings;
}
export interface LoginRequest {//Data sent when the user logs in
  email: string;
  password: string;
} 
export interface RegisterRequest {//Data sent when the user creates an account (currency defaults to USD)
  email: string;
  password: string;
}
export interface AuthResponse {//Returned after login or register
  token: string;
  user: User;
}
export interface MeResponse {//Response used when getting or updating the current user
  user: User;
}
export interface UpdateMeRequest {//Data sent when the user changes their settings
  settings: UserSettings;
}
export interface Category {//A spending category used for transactions and budgets
  id: number;
  name: string;
  color: string;
}
export interface Account {//A place where the user's transactions come from
  id: number;
  name: string;
  type: AccountType;
  currency: string;
}
export interface Transaction {//A single transaction in the user's ledger
  id: number;
  date: string;
  description: string;
  //Money values stay as strings to avoid floating-point problems
  amount: string;
  currency: string;
  convertedAmount: string;
  exchangeRate: string;
  exchangeRateDate: string;
  categoryId: number;
  accountId: number;
  categorySource: CategorySource;
}
export interface BudgetCategory {//Budget information for one category
  categoryId: number;
  categoryName: string;
  planned: string;
  spent: string;
  remaining: string;
}
export interface MonthlyBudget {//Full budget summary for one month
  month: string;
  totalPlanned: string;
  totalSpent: string;
  categories: BudgetCategory[];
}
export interface ImportPreview {//Information returned after the user uploads a CSV file
  uploadId: string;
  columns: string[];
  sampleRows: string[][];
  rowCount: number;
}
export interface ImportResult {//Summary returned after a CSV import finishes
  imported: number;
  skipped: number;
  failed: number;
}
export interface AICategorizeResult {//Result for one transaction after AI categorization
  id: number;
  categoryId: number;
  source: CategorySource;
  //Rule-based and default results may not have a confidence score
  confidence: number | null;
}
export interface AIAskResponse {//Response from the "Ask your ledger" feature
  answer: string;
  //These are the transactions used to support the answer
  transactionIds: number[];
}
export interface ExchangeRate {//Exchange rate saved for a specific transaction date
  from: string;
  to: string;
  date: string;
  rate: string;
}
export interface ApiError {//Standard error format returned by the backend
  error: {
    code: string;
    message: string;
  };
}