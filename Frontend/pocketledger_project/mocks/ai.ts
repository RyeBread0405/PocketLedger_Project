import type { AICategorizeResult, AIAskResponse } from "../types/api";
export const mockCategorizeResults: AICategorizeResult[] = [//Mock AI categorization results
  { id: 6, categoryId: 4, source: "ai", confidence: 0.94 },
  { id: 8, categoryId: 5, source: "ai", confidence: 0.91 },
  { id: 11, categoryId: 6, source: "ai", confidence: 0.88 },
  { id: 19, categoryId: 12, source: "default", confidence: 0.34 },
  { id: 3, categoryId: 2, source: "rule", confidence: null },
];
export const mockCategorizeResponse = {//Matches the POST /api/ai/categorize response
  results: mockCategorizeResults,
};
export const mockAskResponse: AIAskResponse = {//Mock response for the Ask your ledger feature
  answer: "You spent $43.09 on Dining in September 2026.",
  transactionIds: [8, 9, 10],
};