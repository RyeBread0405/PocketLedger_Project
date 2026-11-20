"use client";
import { Plus, X } from "lucide-react";
import React, { useEffect, useState } from "react";
import {mockTransactions, mockCategories, mockAccounts, mockBudget} from "../../../mocks";
import {apiFetch, ApiRequestError, checkBackendConnection} from "../../../lib/api";
import type { Account, AccountType, MonthlyBudget } from "../../../types/api";
const USE_MOCK = false; 

interface BackendAccount {
  id: number;
  name: string;
  type: AccountType;
  currency?: string;
}
function accountFromBackend(a: BackendAccount): Account {
  //The backend does not send a currency yet, so use USD by default
  let currency = "USD";
  if (a.currency) {
    currency = a.currency;
  }
  return { id: a.id, name: a.name, type: a.type, currency: currency };
}

//Shared style for the form inputs
const inputClass =
  "mt-1 w-full rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-[#3F4A45] focus:outline-none focus:ring-2 focus:ring-[#A9C9B0]";

const categories = mockCategories;
const budgetableCategories = categories.filter((category) => category.name !== "Income");
const categoryById = new Map(categories.map((c) => [c.id, c]));


export default function BudgetsPage() { 
  //Store loading, error, and form states
  const [loading, setLoading] = useState(!USE_MOCK);
  const [loadError, setLoadError] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [backendStatus, setBackendStatus] = useState("");

  const [monthSelected, setMonthSelected] = useState("2026-11");
  const [accounts, setAccounts] = useState<Account[]>(USE_MOCK ? mockAccounts : []);
  const [budget, setBudget] = useState<MonthlyBudget>();
  //Find accounts by their ID
  const accountById = new Map<number, Account>();
  for (const account of accounts) {
    accountById.set(account.id, account);
  }
  const allocatedCategories =
    budget?.categories.filter((category) => Number(category.planned) > 0) ?? [];

  //Load the user's accounts and budget data
  useEffect(() => {
    if (USE_MOCK) return;

    async function load() {
      try {
        //Get the user's accounts from the backend
        const accountData = await apiFetch<BackendAccount[] | { accounts: BackendAccount[] }>("/api/accounts");
        //The backend sends a list. 
        let accountList: BackendAccount[] = [];
        if (Array.isArray(accountData)) {
          accountList = accountData;
        } else {
          accountList = accountData.accounts;
        }
        //Change each account into the format used on this page
        const loadedAccounts: Account[] = [];
        for (const account of accountList) {
          loadedAccounts.push(accountFromBackend(account));
        }
        setAccounts(loadedAccounts);

        //Get user's budget info for this month.
        const budgetData = await apiFetch<MonthlyBudget | { budget: MonthlyBudget}>(`/api/budgets?userID=0&month=${monthSelected}`); //userID=8&month=
        //`/api/transactions?accountId=${account.id}`
        const normalizedBudget = "budget" in budgetData ? budgetData.budget : budgetData;
        setBudget(normalizedBudget);
        setLoadError("");
      } catch (e) {
        setLoadError(e instanceof ApiRequestError ? e.message : "Could not load data.");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  //update budget whenever monthSelected changes
  useEffect (() => {
    async function load() {
      try {
        //Get user's budget info for this month.
        const budgetData = await apiFetch<MonthlyBudget | { budget: MonthlyBudget}>(`/api/budgets?userID=0&month=${monthSelected}`); 
        const normalizedBudget = "budget" in budgetData ? budgetData.budget : budgetData;
        setBudget(normalizedBudget);
        setLoadError("");
      } catch (e) {
        setLoadError(e instanceof ApiRequestError ? e.message : "Could not load budget.");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [monthSelected])


  return (
    <main className="relative min-h-screen bg-linear-to-b from-[#E4EFE3] via-[#E3EDF1] via-40% to-[#FBF8EF] to-75% px-4 py-8 md:px-10">
      {/* Background grid */}
      <div className="pointer-events-none absolute inset-0 opacity-20 [background-image:linear-gradient(#9BB5A3_1px,transparent_1px),linear-gradient(90deg,#9BB5A3_1px,transparent_1px)] [background-size:32px_32px]" />
      
      
      

      {/* Main Content */}
      <div className="relative mx-auto max-w-5xl">
        
        {/*  */}
        
        {/* Page title and Month Selection */}
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <h1 className="text-3xl font-semibold text-[#3F5747]">Budgets</h1>
            <p className="mt-1 text-sm text-[#7A8580]">
              {USE_MOCK ? "Showing sample data." : "Your monthly budget breakdown"}
            </p>
          </div>

          {/* Choose which account to show */}
          <div className="flex items-center gap-3">
            
            <input
              id="month-select"
              type="month"
              value={monthSelected}
              onClick={(e) => e.currentTarget.showPicker()}
              onChange={(e) => setMonthSelected(e.target.value)}
              className="cursor-pointer rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-sm text-[#3F4A45]"
            />
             {/*<p className="rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-sm text-[#3F4A45]">Selected: {monthSelected}</p>*/}
          </div>

          {/* Open the add transaction form (needs at least one account) */}
            <button
              onClick={() => setShowForm(true)}
              disabled={accounts.length === 0}
              className="flex items-center gap-2 rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] px-4 py-2 font-medium text-[#3F5747] transition hover:-translate-y-0.5 hover:bg-[#E4EFE3] hover:shadow-sm disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:translate-y-0"
            >
              <Plus className="h-4 w-4" /> Create Budget
            </button>
        </div>

        
        {/* Total Planned & Total Spent */}
        <div className="mt-6 grid grid-cols-2 gap-4">
          <div className="rounded-xl border border-white/70 bg-white/60 p-4">
            <p className="text-sm text-[#7A8580]">Total Planned</p>
            <p className="mt-1 text-xl font-semibold text-[#3E8E5A]">${budget?.totalPlanned}</p>
          </div>
          
          <div className="rounded-xl border border-white/70 bg-white/60 p-4">
            <p className="text-sm text-[#7A8580]">Total Spent</p>
            <p className="mt-1 text-xl font-semibold text-[#B5574B]">${budget?.totalSpent}</p>
          </div>
        </div>

        <section className="mt-6 rounded-xl border border-white/70 bg-white/60 p-4">
          <h2 className="text-lg font-semibold text-[#3F5747]">Category budgets</h2>
          {allocatedCategories.length > 0 ? (
            <div className="mt-2 divide-y divide-[#E4EFE3]">
              {allocatedCategories.map((category) => (
                <article
                  key={category.categoryId}
                  className="flex flex-col gap-3 py-4 sm:flex-row sm:items-center sm:justify-between"
                >
                  <div>
                    <h3 className="font-medium text-[#3F4A45]">{category.categoryName}</h3>
                    <p className="text-sm text-[#7A8580]">
                      Planned: ${Number(category.planned).toFixed(2)}
                    </p>
                  </div>
                  <div className="grid grid-cols-2 gap-6 text-sm">
                    <div>
                      <p className="text-[#7A8580]">Spent</p>
                      <p className="font-medium text-[#B5574B]">
                        ${Number(category.spent).toFixed(2)}
                      </p>
                    </div>
                    <div>
                      <p className="text-[#7A8580]">Remaining</p>
                      <p className="font-medium text-[#3E8E5A]">
                        ${Number(category.remaining).toFixed(2)}
                      </p>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          ) : (
            <p className="mt-2 text-sm text-[#7A8580]">
              No category budgets allocated for this month.
            </p>
          )}
        </section>
      </div>
      

      {/* Show the form when Add transaction is clicked */}
      {showForm && budget && (
        <CreateBudgetForm
          budget={budget}
          month={monthSelected}
          onClose={() => setShowForm(false)}
          onCreated={(updatedBudget) => {
            setBudget(updatedBudget);
            setShowForm(false);
          }}
        />
      )}

    </main>
  )
}

//Information the form gets from the page
interface CreateBudgetFormProps {
  budget: MonthlyBudget; 
  month: string;
  onClose: () => void; 
  onCreated: (budget: MonthlyBudget) => void; 
}

function CreateBudgetForm({ budget, month, onClose, onCreated }: CreateBudgetFormProps) {
  //Store the information entered in the form
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  
  const [amounts, setAmounts] = useState<Record<number, string>>(() =>
    budgetableCategories.reduce<Record<number, string>>((values, category) => {
      values[category.id] = String(
        budget.categories.find((item) => item.categoryId === category.id)?.planned ?? "",
      );
      return values;
    }, {}),
  );

  //Save the budget when the form is submitted
  async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
    e.preventDefault(); //Stop the form from reloading the page
    setError("");

    const budgets = budgetableCategories.flatMap((category) => {
      const amount = amounts[category.id]?.trim();
      return amount ? [{ categoryId: category.id, amount }] : [];
    });

    if (budgets.length === 0 || budgets.some(({ amount }) => !Number.isFinite(Number(amount)) || Number(amount) < 0)) {
      setError("Enter a valid amount for at least one category.");
      return;
    }

    

    
    const body = { month, budgets };

    //Keep mock mode from sending data to the backend
    if (USE_MOCK) {
      return;
    }

    //Replace the monthly budget with the submitted category amounts
    setSaving(true);
    try {
      const created = await apiFetch<MonthlyBudget>("/api/budgets", {
        method: "PUT",
        body: JSON.stringify(body),
      });
      onCreated(created);
    } catch (e) {
      //Show an error if the budget cannot be saved
      setError(e instanceof ApiRequestError ? e.message : "Could not save the budget.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-10 flex items-center justify-center bg-black/20 px-4" onClick={onClose}>
      {/* Create budget form */}
      <form
        onSubmit={handleSubmit}
        onClick={(e) => e.stopPropagation()}
        className="max-h-[85vh] w-full max-w-md space-y-4 overflow-y-auto rounded-2xl border border-[#EDE6D3] bg-[#FFFDF7] p-6 shadow-lg"
      >
        {/* Form title and close button */}
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-semibold text-[#3F4A45]">Create monthly budget</h2>
          <button type="button" onClick={onClose} aria-label="Close" className="text-[#7A8580]">
            <X className="h-5 w-5" />
          </button>
        </div>
        <p className="text-sm text-[#7A8580]">
          Enter a planned amount for each category. Leave categories without a budget blank.
        </p>

        <div className="space-y-3">
          {budgetableCategories.map((category) => (
            <div key={category.id}>
              <label htmlFor={`budget-${category.id}`} className="text-sm text-[#4F6657]">
                {category.name}
              </label>
              <input
                id={`budget-${category.id}`}
                type="number"
                min="0"
                step="0.01"
                inputMode="decimal"
                placeholder="0.00"
                value={amounts[category.id] ?? ""}
                onChange={(event) =>
                  setAmounts((current) => ({ ...current, [category.id]: event.target.value }))
                }
                className={inputClass}
              />
            </div>
          ))}
        </div>
        

        {/* Show an error if saving fails */}
        {error && <p className="text-sm text-[#B5574B]">{error}</p>}

        {/* Save budget button */}
        <button
          type="submit"
          disabled={saving}
          className="w-full rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] px-4 py-3 font-medium text-[#3F5747] transition hover:bg-[#E4EFE3] disabled:opacity-60"
        >
          {saving ? "Saving…" : "Save budget"}
        </button>
      </form>
    </div>
  );
}