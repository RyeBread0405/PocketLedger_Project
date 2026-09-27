import type { Transaction } from "../types/api";
export const mockTransactions: Transaction[] = [//Mock transactions for a student's monthly spending
  { id: 1, date: "2026-09-01", description: "Apartment Rent", amount: "-650.00", currency: "USD", convertedAmount: "-650.00", exchangeRate: "1.0000", exchangeRateDate: "2026-09-01", categoryId: 1, accountId: 1, categorySource: "user" },
  { id: 2, date: "2026-09-03", description: "OG&E", amount: "-48.22", currency: "USD", convertedAmount: "-48.22", exchangeRate: "1.0000", exchangeRateDate: "2026-09-03", categoryId: 2, accountId: 1, categorySource: "user" },
  { id: 3, date: "2026-09-04", description: "City of Stillwater Water", amount: "-24.70", currency: "USD", convertedAmount: "-24.70", exchangeRate: "1.0000", exchangeRateDate: "2026-09-04", categoryId: 2, accountId: 1, categorySource: "user" },
  { id: 4, date: "2026-09-05", description: "Spotify", amount: "-11.99", currency: "USD", convertedAmount: "-11.99", exchangeRate: "1.0000", exchangeRateDate: "2026-09-05", categoryId: 3, accountId: 2, categorySource: "rule" },
  { id: 5, date: "2026-09-05", description: "AT&T", amount: "-45.00", currency: "USD", convertedAmount: "-45.00", exchangeRate: "1.0000", exchangeRateDate: "2026-09-05", categoryId: 3, accountId: 2, categorySource: "user" },
  { id: 6, date: "2026-09-07", description: "Walmart", amount: "-72.46", currency: "USD", convertedAmount: "-72.46", exchangeRate: "1.0000", exchangeRateDate: "2026-09-07", categoryId: 4, accountId: 1, categorySource: "ai" },
  { id: 7, date: "2026-09-09", description: "ALDI", amount: "-38.17", currency: "USD", convertedAmount: "-38.17", exchangeRate: "1.0000", exchangeRateDate: "2026-09-09", categoryId: 4, accountId: 1, categorySource: "ai" },
  { id: 8, date: "2026-09-10", description: "Aspen Coffee", amount: "-12.84", currency: "USD", convertedAmount: "-12.84", exchangeRate: "1.0000", exchangeRateDate: "2026-09-10", categoryId: 5, accountId: 2, categorySource: "ai" },
  { id: 9, date: "2026-09-11", description: "Chipotle", amount: "-11.35", currency: "USD", convertedAmount: "-11.35", exchangeRate: "1.0000", exchangeRateDate: "2026-09-11", categoryId: 5, accountId: 2, categorySource: "ai" },
  { id: 10, date: "2026-09-12", description: "Hideaway Pizza", amount: "-18.90", currency: "USD", convertedAmount: "-18.90", exchangeRate: "1.0000", exchangeRateDate: "2026-09-12", categoryId: 5, accountId: 2, categorySource: "user" },
  { id: 11, date: "2026-09-13", description: "AMC Theatres", amount: "-15.49", currency: "USD", convertedAmount: "-15.49", exchangeRate: "1.0000", exchangeRateDate: "2026-09-13", categoryId: 6, accountId: 2, categorySource: "ai" },
  { id: 12, date: "2026-09-14", description: "Uber", amount: "-14.62", currency: "USD", convertedAmount: "-14.62", exchangeRate: "1.0000", exchangeRateDate: "2026-09-14", categoryId: 7, accountId: 2, categorySource: "ai" },
  { id: 13, date: "2026-09-15", description: "OnCue", amount: "-35.00", currency: "USD", convertedAmount: "-35.00", exchangeRate: "1.0000", exchangeRateDate: "2026-09-15", categoryId: 7, accountId: 1, categorySource: "user" },
  { id: 14, date: "2026-09-16", description: "Walgreens", amount: "-13.27", currency: "USD", convertedAmount: "-13.27", exchangeRate: "1.0000", exchangeRateDate: "2026-09-16", categoryId: 8, accountId: 2, categorySource: "ai" },
  { id: 15, date: "2026-09-17", description: "Target", amount: "-29.95", currency: "USD", convertedAmount: "-29.95", exchangeRate: "1.0000", exchangeRateDate: "2026-09-17", categoryId: 9, accountId: 2, categorySource: "ai" },
  { id: 16, date: "2026-09-18", description: "OSU Bookstore", amount: "-86.50", currency: "USD", convertedAmount: "-86.50", exchangeRate: "1.0000", exchangeRateDate: "2026-09-18", categoryId: 10, accountId: 1, categorySource: "user" },
  { id: 17, date: "2026-09-18", description: "OSU Payroll", amount: "480.00", currency: "USD", convertedAmount: "480.00", exchangeRate: "1.0000", exchangeRateDate: "2026-09-18", categoryId: 11, accountId: 1, categorySource: "rule" },
  { id: 18, date: "2026-09-19", description: "Target Refund", amount: "12.00", currency: "USD", convertedAmount: "12.00", exchangeRate: "1.0000", exchangeRateDate: "2026-09-19", categoryId: 9, accountId: 2, categorySource: "user" },
  { id: 19, date: "2026-09-20", description: "VENMO PAYMENT", amount: "-7.25", currency: "USD", convertedAmount: "-7.25", exchangeRate: "1.0000", exchangeRateDate: "2026-09-20", categoryId: 12, accountId: 2, categorySource: "default" },
  { id: 20, date: "2026-09-21", description: "Paris Grocery Store", amount: "-21.40", currency: "EUR", convertedAmount: "-23.13", exchangeRate: "1.0810", exchangeRateDate: "2026-09-21", categoryId: 4, accountId: 4, categorySource: "ai" },
];
export const mockTransactionsResponse = {//Matches the GET /api/transactions response
  transactions: mockTransactions,
};