"use client";

import React, { useEffect, useRef, useState } from "react";

import { apiFetch, ApiRequestError } from "../../../lib/api";
import type { AccountType, MonthlyBudget } from "../../../types/api";
import type p5 from "p5";

interface BackendAccount {
  id: number;
  name: string;
  type: AccountType;
  currency?: string;
}
interface BackendTransaction {
  date: string;
}

function drawBudgetChart(p5: p5, budgets: MonthlyBudget[]) {
  p5.background("#fffdf7");
  p5.textSize(12);
  p5.textAlign(p5.CENTER, p5.CENTER);

  if (budgets.length === 0) return;

  const left = 58;
  const right = p5.width - 24;
  const top = 36;
  const bottom = p5.height - 54;
  const highestPlanned = Math.max(...budgets.map((budget) => Number(budget.totalPlanned)));
  const highestSpent = Math.max(...budgets.map((budget) => Number(budget.totalSpent)));
  const maxValue = Math.max(1, (highestPlanned || highestSpent) * 2);
  const groupWidth = (right - left) / budgets.length;
  const barWidth = Math.max(2, Math.min(18, groupWidth * 0.3));
  const barGap = Math.max(2, barWidth * 0.2);
  const xFor = (index: number) => left + groupWidth * (index + 0.5);
  const yFor = (value: string) =>
    bottom - (Math.min(Number(value), maxValue) / maxValue) * (bottom - top);

  p5.stroke("#cfe0cf");
  p5.strokeWeight(1);
  p5.line(left, top, left, bottom);
  p5.line(left, bottom, right, bottom);
  p5.noStroke();
  p5.fill("#7a8580");
  p5.textAlign(p5.RIGHT, p5.CENTER);
  p5.text(`$${maxValue.toFixed(0)}`, left - 8, top);
  p5.text("$0", left - 8, bottom);

  p5.noStroke();
  budgets.forEach((budget, index) => {
    const center = xFor(index);
    const plannedY = yFor(budget.totalPlanned);
    const spentY = yFor(budget.totalSpent);

    p5.fill("#3e8e5a");
    p5.rect(center - barWidth - barGap / 2, plannedY, barWidth, bottom - plannedY, 3);
    p5.fill("#b5574b");
    p5.rect(center + barGap / 2, spentY, barWidth, bottom - spentY, 3);
  });

  p5.noStroke();
  p5.fill("#3f4a45");
  p5.textAlign(p5.LEFT, p5.CENTER);
  p5.text("Planned", left, 16);
  p5.fill("#3e8e5a");
  p5.rect(left - 14, 12, 10, 8, 2);
  p5.fill("#3f4a45");
  p5.text("Spent", left + 90, 16);
  p5.fill("#b5574b");
  p5.rect(left + 76, 12, 10, 8, 2);

  p5.fill("#7a8580");
  p5.textAlign(p5.CENTER, p5.CENTER);
  budgets.forEach((budget, index) => {
    p5.text(budget.month, xFor(index), bottom + 22);
  });
  p5.text("Bars cap at 2× the highest planned total", p5.width / 2, p5.height - 10);
}

function BudgetChart({ budgets }: { budgets: MonthlyBudget[] }) {
  const containerRef = useRef<HTMLDivElement>(null);
  const budgetsRef = useRef(budgets);
  budgetsRef.current = budgets;

  useEffect(() => {
    let active = true;
    let instance: p5 | undefined;

    void import("p5").then(({ default: P5 }) => {
      if (!active || !containerRef.current) return;

      instance = new P5((sketch) => {
        sketch.setup = () => {
          sketch.createCanvas(600, 400);
        };
        sketch.draw = () => drawBudgetChart(sketch, budgetsRef.current);
      }, containerRef.current);
    });

    return () => {
      active = false;
      instance?.remove();
      containerRef.current?.replaceChildren();
    };
  }, []);

  return (
    <div
      ref={containerRef}
      className="canvas-container flex min-h-[400px] items-center justify-center"
    />
  );
}

//  main  //
export default function DashboardPage() {
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [monthlyBudgets, setMonthlyBudgets] = useState<MonthlyBudget[]>([]);
  const [monthRange, setMonthRange] = useState<{ start: string; end: string } | null>(null);

  //Load transactions first so the budgets can cover the full transaction month range.
  useEffect(() => {
    async function load() {
      try {
        const accountData = await apiFetch<BackendAccount[] | { accounts: BackendAccount[] }>("/api/accounts");
        const accountList = Array.isArray(accountData) ? accountData : accountData.accounts;
        const transactionResponses = await Promise.all(
          accountList.map((account) =>
            apiFetch<BackendTransaction[] | { transactions: BackendTransaction[] }>(
              `/api/transactions?accountId=${account.id}`,
            ),
          ),
        );
        const transactions = transactionResponses.flatMap((response) =>
          Array.isArray(response) ? response : response.transactions,
        );
        const transactionMonths = transactions.map((transaction) => {
          const month = transaction.date.slice(0, 7);
          if (!/^\d{4}-(0[1-9]|1[0-2])$/.test(month)) {
            throw new Error("A transaction returned an invalid date.");
          }
          return month;
        });

        if (transactionMonths.length === 0) {
          setMonthlyBudgets([]);
          setMonthRange(null);
          setLoadError("");
          return;
        }

        const firstMonth = transactionMonths.reduce((first, month) => (month < first ? month : first));
        const lastMonth = transactionMonths.reduce((last, month) => (month > last ? month : last));
        const firstIndex = Number(firstMonth.slice(0, 4)) * 12 + Number(firstMonth.slice(5, 7)) - 1;
        const lastIndex = Number(lastMonth.slice(0, 4)) * 12 + Number(lastMonth.slice(5, 7)) - 1;
        const months = Array.from({ length: lastIndex - firstIndex + 1 }, (_, offset) => {
          const monthIndex = firstIndex + offset;
          const year = Math.floor(monthIndex / 12);
          const month = (monthIndex % 12) + 1;
          return `${year}-${String(month).padStart(2, "0")}`;
        });

        const budgetResponses = await Promise.all(
          months.map((month) =>
            apiFetch<MonthlyBudget | { budget: MonthlyBudget }>(
              `/api/budgets?month=${encodeURIComponent(month)}`,
            ),
          ),
        );
        const budgets = budgetResponses.map((response) =>
          "budget" in response ? response.budget : response,
        );

        setMonthlyBudgets(budgets);
        setMonthRange({ start: firstMonth, end: lastMonth });
        setLoadError("");
      } catch (e) {
        setLoadError(e instanceof ApiRequestError ? e.message : "Could not load dashboard data.");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  return (
    <main className="relative min-h-screen bg-linear-to-b from-[#E4EFE3] via-[#E3EDF1] via-40% to-[#FBF8EF] to-75% px-4 py-8 md:px-10">
      <div className="pointer-events-none absolute inset-0 opacity-20 [background-image:linear-gradient(#9BB5A3_1px,transparent_1px),linear-gradient(90deg,#9BB5A3_1px,transparent_1px)] [background-size:32px_32px]" />
      <div className="relative mx-auto max-w-6xl">
        <h1 className="text-3xl font-semibold text-[#3F5747]">Dashboard</h1>
        <p className="mt-1 text-sm text-[#7A8580]">
          Monthly planned and spent totals across your accounts.
        </p>

        <div className="mt-6 grid gap-6 lg:grid-cols-6">
          <section className="rounded-xl border border-white/70 bg-white/60 p-5 lg:col-span-2">
            <h2 className="text-lg font-semibold text-[#3F5747]">Transaction range</h2>
            {monthRange ? (
              <>
                <p className="mt-3 text-sm text-[#7A8580]">From</p>
                <p className="font-medium text-[#3F4A45]">{monthRange.start}</p>
                <p className="mt-3 text-sm text-[#7A8580]">Through</p>
                <p className="font-medium text-[#3F4A45]">{monthRange.end}</p>
                <p className="mt-4 text-sm text-[#7A8580]">
                  {monthlyBudgets.length} months
                </p>
              </>
            ) : (
              <p className="mt-3 text-sm text-[#7A8580]">
                {loading ? "Loading transaction data…" : "No transactions found."}
              </p>
            )}
          </section>

          <section className="min-w-0 rounded-xl border border-white/70 bg-white/60 p-4 lg:col-span-4">
            <h2 className="mb-2 text-lg font-semibold text-[#3F5747]">Monthly budget trend</h2>
            <BudgetChart budgets={monthlyBudgets} />
          </section>
        </div>
        {loadError && <p className="mt-4 text-sm text-[#B5574B]">{loadError}</p>}
      </div>
    </main>
  );
}