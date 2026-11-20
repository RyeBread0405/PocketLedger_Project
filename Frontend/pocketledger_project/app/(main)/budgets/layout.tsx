import type { Metadata } from "next";

//Title shown on the browser tab: "Budgets | PocketLedger"
export const metadata: Metadata = {
  title: "Budgets",
};

//This layout does not change the page, it only shows the page inside it
export default function BudgetsLayout({ children }: { children: React.ReactNode }) {
  return children;
}
