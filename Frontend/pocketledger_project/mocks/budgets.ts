import type { MonthlyBudget } from "../types/api";
export const mockBudget: MonthlyBudget = {//Mock monthly budget for September
  month: "2026-09",
  totalPlanned: "1650.00",
  totalSpent: "1158.84",
  categories: [
    { categoryId: 1, categoryName: "Rent", planned: "650.00", spent: "650.00", remaining: "0.00" },
    { categoryId: 2, categoryName: "Utilities", planned: "100.00", spent: "72.92", remaining: "27.08" },
    { categoryId: 3, categoryName: "Bills", planned: "80.00", spent: "56.99", remaining: "23.01" },
    { categoryId: 4, categoryName: "Groceries", planned: "220.00", spent: "133.76", remaining: "86.24" },
    { categoryId: 5, categoryName: "Dining", planned: "120.00", spent: "43.09", remaining: "76.91" },
    { categoryId: 6, categoryName: "Entertainment", planned: "60.00", spent: "15.49", remaining: "44.51" },
    { categoryId: 7, categoryName: "Transportation", planned: "100.00", spent: "49.62", remaining: "50.38" },
    { categoryId: 8, categoryName: "Health and Wellness", planned: "50.00", spent: "13.27", remaining: "36.73" },
    { categoryId: 9, categoryName: "Shopping", planned: "100.00", spent: "29.95", remaining: "70.05" },
    { categoryId: 10, categoryName: "Education", planned: "120.00", spent: "86.50", remaining: "33.50" },
    { categoryId: 12, categoryName: "Other", planned: "50.00", spent: "7.25", remaining: "42.75" },
  ],
};