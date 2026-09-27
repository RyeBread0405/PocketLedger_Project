import type { Category } from "../types/api";
export const mockCategories: Category[] = [//Default categories used by PocketLedger
  { id: 1, name: "Rent", color: "#6C5CE7" },
  { id: 2, name: "Utilities", color: "#F1C40F" },
  { id: 3, name: "Bills", color: "#00ACC1" },
  { id: 4, name: "Groceries", color: "#E8833A" },
  { id: 5, name: "Dining", color: "#E74C3C" },
  { id: 6, name: "Entertainment", color: "#E84393" },
  { id: 7, name: "Transportation", color: "#4A90D9" },
  { id: 8, name: "Health and Wellness", color: "#1ABC9C" },
  { id: 9, name: "Shopping", color: "#8D6E63" },
  { id: 10, name: "Education", color: "#9B59B6" },
  { id: 11, name: "Income", color: "#2ECC71" },
  { id: 12, name: "Other", color: "#95A5A6" },
];
export const mockCategoriesResponse = {//Matches the GET /api/categories response
  categories: mockCategories,
};