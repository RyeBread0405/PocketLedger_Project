"use client";
import {useEffect, useMemo, useState} from "react";
import Link from "next/link";
import { Plus, X } from "lucide-react";
import {mockTransactions, mockCategories, mockAccounts} from "../../../mocks";
import {apiFetch, ApiRequestError} from "../../../lib/api";
import type { Account, AccountType, Category } from "../../../types/api";

const USE_MOCK = false;

//Transaction data used on this page
interface Row {
  id: number;
  date: string;
  label: string;
  description: string;
  amount: number; //Negative for expense, positive for income
  categoryId: number;
  accountId: number;
}

//Transaction data returned by the backend
interface BackendTransaction {
  id: number;
  amount: number | string;
  label: string | null;
  description: string | null;
  date: string;
  categoryId: number;
  accountId: number;
}

//Account data returned by GET /api/accounts
interface BackendAccount {
  id: number;
  name: string;
  type: AccountType;
  currency?: string;
}

//Category data returned by GET /api/categories
interface BackendCategory {
  id: number;
  categoryLabel: string;
}

//Change backend transaction data into the format used on this page
function fromBackend(t: BackendTransaction): Row {
  return {
    id: t.id,
    date: t.date,
    label: t.label ?? "",
    description: t.description ?? "",
    amount: Number(t.amount),
    categoryId: t.categoryId,
    accountId: t.accountId,
  };
}

//Change backend account data into the format used on this page
function accountFromBackend(a: BackendAccount): Account {
  //The backend does not send a currency yet, so use USD by default
  let currency = "USD";
  if (a.currency) {
    currency = a.currency;
  }

  return { id: a.id, name: a.name, type: a.type, currency: currency };
}

//Change mock transactions into the same format
const mockRows: Row[] = mockTransactions.map((t) => ({
  id: t.id,
  date: t.date,
  label: t.description,
  description: "",
  amount: Number(t.amount),
  categoryId: t.categoryId,
  accountId: t.accountId,
}));

//The backend does not store colors, so colors only live in the frontend.
//The default categories (IDs 1-12) use the colors from mocks/categories.ts
const defaultColors = new Map<number, string>();
for (const c of mockCategories) {
  defaultColors.set(c.id, c.color);
}

//Colors for categories the user creates
const extraColors = ["#1E3A8A", "#A0401A", "#1E6B3A", "#6B2D5C", "#8C6D1F", "#0B5D63", "#8B1E3F", "#7A8B1F"];

//Pick a color for a category
function colorForCategory(id: number) {
  const color = defaultColors.get(id);
  if (color) {
    return color;
  }
  //User categories get a color from the list, based on their ID
  return extraColors[id % extraColors.length];
}

//Change backend category data into the format used on this page
function categoryFromBackend(c: BackendCategory): Category {
  return { id: c.id, name: c.categoryLabel, color: colorForCategory(c.id) };
}

//Color used when a category can't be found
const unknownColor = "#95A5A6";

//Show money with the correct currency symbol
function formatMoney(amount: number, currency: string) {
  return new Intl.NumberFormat("en-US", { style: "currency", currency }).format(amount);
}

//Get today's date for the transaction form
function today() {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

//Data returned by GET /api/settings
interface SettingsResponse {
  currency: string;
}

//Data returned by GET /api/fx/rate
interface FxRateResponse {
  from: string;
  to: string;
  date: string;
  rate: number | string;
}

//Exchange rates only exist for past days, so use today for future dates
function rateDate(date: string) {
  const now = today();
  if (date > now) {
    return now;
  }
  return date;
}

//Key for saving a rate
function rateKey(currency: string, date: string) {
  return currency + "|" + rateDate(date);
}

//Shared style for the form inputs
const inputClass =
  "mt-1 w-full rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-[#3F4A45] focus:outline-none focus:ring-2 focus:ring-[#A9C9B0]";

export default function TransactionsPage() {
  //Store accounts, transactions, and selected account
  const [accounts, setAccounts] = useState<Account[]>(USE_MOCK ? mockAccounts : []);
  const [rows, setRows] = useState<Row[]>(USE_MOCK ? mockRows : []);
  const [accountFilter, setAccountFilter] = useState<number | "all">("all");
  const [categories, setCategories] = useState<Category[]>(USE_MOCK ? mockCategories : []);

  //Store the home currency and the exchange rates we already got
  const [homeCurrency, setHomeCurrency] = useState("USD");
  const [rates, setRates] = useState<Record<string, number>>({});
  const [fxError, setFxError] = useState("");

  //Store loading, error, and form states
  const [loading, setLoading] = useState(!USE_MOCK);
  const [loadError, setLoadError] = useState("");
  const [showForm, setShowForm] = useState(false);

  //Find accounts by their ID
  const accountById = new Map<number, Account>();
  for (const account of accounts) {
    accountById.set(account.id, account);
  }

  //Find categories by their ID
  const categoryById = new Map<number, Category>();
  for (const category of categories) {
    categoryById.set(category.id, category);
  }

  //Add a category the user just created to the list
  function addCategory(category: Category) {
    setCategories((prev) => [...prev, category]);
  }

  //Get the currency of the account a transaction belongs to
  function currencyOf(row: Row) {
    const account = accountById.get(row.accountId);
    if (account) {
      return account.currency;
    }
    return "USD";
  }

  //Change a transaction amount into the home currency.
  //Returns null if we don't have the exchange rate yet
  function toHomeCurrency(row: Row) {
    const currency = currencyOf(row);
    if (currency === homeCurrency) {
      return row.amount;
    }
    const rate = rates[rateKey(currency, row.date)];
    if (rate === undefined) {
      return null;
    }
    return row.amount * rate;
  }

  //Load the user's accounts, then the transactions for every account
  useEffect(() => {
    if (USE_MOCK) return;

    async function load() {
      try {
        //Get the default categories and the user's own categories
        const categoryData = await apiFetch<BackendCategory[]>("/api/categories");
        const loadedCategories: Category[] = [];
        for (const category of categoryData) {
          loadedCategories.push(categoryFromBackend(category));
        }
        setCategories(loadedCategories);

        //Get the home currency from Settings (keep USD if this fails)
        try {
          const settings = await apiFetch<SettingsResponse>("/api/settings");
          if (settings.currency) {
            setHomeCurrency(settings.currency);
          }
        } catch {
          //The page still works in USD
        }

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

        //Get the transactions for each account
        const allRows: Row[] = [];
        for (const account of loadedAccounts) {
          const transactionData = await apiFetch<BackendTransaction[] | { transactions: BackendTransaction[] }>(
            `/api/transactions?accountId=${account.id}`
          );

          //The backend sends a list. 
          let transactionList: BackendTransaction[] = [];
          if (Array.isArray(transactionData)) {
            transactionList = transactionData;
          } else {
            transactionList = transactionData.transactions;
          }

          for (const t of transactionList) {
            allRows.push(fromBackend(t));
          }
        }
        setRows(allRows);
        setLoadError("");
      } catch (e) {
        setLoadError(e instanceof ApiRequestError ? e.message : "Could not load transactions.");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  //Get the exchange rates we still need to show totals in the home currency.
  //This runs again when transactions are added or the rates change
  useEffect(() => {
    if (USE_MOCK || loading || fxError !== "") return;

    //Make a list of the rates we don't have yet
    const needed: { currency: string; date: string; key: string }[] = [];
    for (const row of rows) {
      //Find the currency of this transaction's account
      let currency = "USD";
      for (const account of accounts) {
        if (account.id === row.accountId) {
          currency = account.currency;
        }
      }

      //No rate needed if it is already in the home currency
      if (currency === homeCurrency) {
        continue;
      }

      //Skip rates we already have or already put in the list
      const key = rateKey(currency, row.date);
      let alreadyNeeded = false;
      for (const item of needed) {
        if (item.key === key) {
          alreadyNeeded = true;
        }
      }
      if (rates[key] !== undefined || alreadyNeeded) {
        continue;
      }
      needed.push({ currency: currency, date: rateDate(row.date), key: key });
    }

    if (needed.length === 0) return;

    //Ask the backend for each rate, one at a time
    async function loadRates() {
      const newRates: Record<string, number> = {};
      try {
        for (const item of needed) {
          const data = await apiFetch<FxRateResponse>(
            `/api/fx/rate?from=${item.currency}&to=${homeCurrency}&date=${item.date}`
          );
          newRates[item.key] = Number(data.rate);
        }
      } catch (e) {
        //503 when the exchange rate service is down
        if (e instanceof ApiRequestError) {
          setFxError(e.message);
        } else {
          setFxError("Exchange rates are currently unavailable.");
        }
      }
      //Save the rates we got
      setRates((prev) => ({ ...prev, ...newRates }));
    }
    loadRates();
  }, [rows, accounts, homeCurrency, rates, loading, fxError]);

  //Filter transactions by account and show the newest ones first
  const visible = useMemo(
    () =>
      rows
        .filter((r) => accountFilter === "all" || r.accountId === accountFilter)
        .sort((a, b) => b.date.localeCompare(a.date) || b.id - a.id),
    [rows, accountFilter]
  );

  //Calculate total income and spending in the home currency
  let income = 0;
  let expense = 0;
  let missingRate = false;
  for (const r of visible) {
    const amount = toHomeCurrency(r);
    if (amount === null) {
      missingRate = true;
    } else if (amount >= 0) {
      income += amount;
    } else {
      expense += amount;
    }
  }

  //If exchange rates failed, add up the original amounts and show them in USD
  let totalsCurrency = homeCurrency;
  if (fxError !== "") {
    income = 0;
    expense = 0;
    for (const r of visible) {
      if (r.amount >= 0) {
        income += r.amount;
      } else {
        expense += r.amount;
      }
    }
    totalsCurrency = "USD";
  }

  //Text shown in the total cards
  let incomeText = formatMoney(income, totalsCurrency);
  let expenseText = formatMoney(expense, totalsCurrency);
  if (missingRate && fxError === "") {
    incomeText = "Converting…";
    expenseText = "Converting…";
  }

  //Loading is done with no error, but the user has no accounts yet
  let noAccounts = false;
  if (!loading && !loadError && accounts.length === 0) {
    noAccounts = true;
  }

  return (
    <main className="relative min-h-screen bg-linear-to-b from-[#E4EFE3] via-[#E3EDF1] via-40% to-[#FBF8EF] to-75% px-4 py-8 md:px-10">
      {/* Background grid */}
      <div className="pointer-events-none absolute inset-0 opacity-20 [background-image:linear-gradient(#9BB5A3_1px,transparent_1px),linear-gradient(90deg,#9BB5A3_1px,transparent_1px)] [background-size:32px_32px]" />

      <div className="relative mx-auto max-w-5xl">
        {/* Page title and account filter */}
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <h1 className="text-3xl font-semibold text-[#3F5747]">Transactions</h1>
            <p className="mt-1 text-sm text-[#7A8580]">
              {USE_MOCK ? "Showing sample data." : "Your transactions."}
            </p>
          </div>

          <div className="flex items-center gap-3">
            {/* Choose which account to show */}
            <select
              aria-label="Filter by account"
              value={accountFilter}
              onChange={(e) => setAccountFilter(e.target.value === "all" ? "all" : Number(e.target.value))}
              className="rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-sm text-[#3F4A45]"
            >
              <option value="all">All accounts</option>
              {accounts.map((a) => (
                <option key={a.id} value={a.id}>{a.name}</option>
              ))}
            </select>

            {/* Open the add transaction form (needs at least one account) */}
            <button
              onClick={() => setShowForm(true)}
              disabled={accounts.length === 0}
              className="flex items-center gap-2 rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] px-4 py-2 font-medium text-[#3F5747] transition hover:-translate-y-0.5 hover:bg-[#E4EFE3] hover:shadow-sm disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:translate-y-0"
            >
              <Plus className="h-4 w-4" /> Add transaction
            </button>
          </div>
        </div>

        {/* Total income and spending cards */}
        <div className="mt-6 grid grid-cols-2 gap-4">
          <div className="rounded-xl border border-white/70 bg-white/60 p-4">
            <p className="text-sm text-[#7A8580]">Income</p>
            <p className="mt-1 text-xl font-semibold text-[#3E8E5A]">{incomeText}</p>
          </div>
          <div className="rounded-xl border border-white/70 bg-white/60 p-4">
            <p className="text-sm text-[#7A8580]">Spending</p>
            <p className="mt-1 text-xl font-semibold text-[#B5574B]">{expenseText}</p>
          </div>
        </div>

        {/* Tell the user which currency the totals are in */}
        {fxError === "" && homeCurrency !== "USD" && (
          <p className="mt-2 text-xs text-[#7A8580]">
            Totals are shown in {homeCurrency}, using the exchange rate from each transaction&apos;s date.
          </p>
        )}
        {fxError !== "" && (
          <p className="mt-2 text-xs text-[#B5574B]">
            {fxError} Totals are shown in USD.
          </p>
        )}

        {/* Transaction list */}
        <div className="mt-6 overflow-hidden rounded-2xl border border-[#EDE6D3] bg-[#FFFDF7] shadow-sm">
          {/* Show loading, error, or empty message */}
          {loading && <p className="p-6 text-sm text-[#7A8580]">Loading…</p>}
          {loadError && <p className="p-6 text-sm text-[#B5574B]">{loadError}</p>}
          {noAccounts && (
            <p className="p-6 text-sm text-[#7A8580]">
              You don&apos;t have any accounts yet.{" "}
              {/* Link to the Accounts page */}
              <Link href="/accounts" className="font-medium text-[#3F5747] underline hover:text-[#3E8E5A]">
                Create an account first
              </Link>{" "}
              to add transactions.
            </p>
          )}
          {!loading && !loadError && !noAccounts && visible.length === 0 && (
            <p className="p-6 text-sm text-[#7A8580]">No transactions yet.</p>
          )}

          {/* Show transactions when there is data */}
          {!loading && !loadError && visible.length > 0 && (
            <ul className="divide-y divide-[#EDE6D3]">
              {visible.map((r) => {
                const acct = accountById.get(r.accountId);

                //Find the category name and color for this transaction
                const cat = categoryById.get(r.categoryId);
                let categoryName = "Unknown";
                let categoryColor = unknownColor;
                if (cat) {
                  categoryName = cat.name;
                  categoryColor = cat.color;
                }

                //Amount in the home currency, only if it is a different currency
                let convertedText = "";
                const converted = toHomeCurrency(r);
                if (currencyOf(r) !== homeCurrency && converted !== null) {
                  convertedText = "≈ " + formatMoney(converted, homeCurrency);
                }

                return (
                  <li key={r.id} className="flex items-center gap-4 px-5 py-3">
                    {/* Transaction date */}
                    <span className="w-24 shrink-0 text-sm tabular-nums text-[#7A8580]">{r.date}</span>

                    {/* Transaction name, account, and description */}
                    <div className="min-w-0 flex-1">
                      <p className="truncate font-medium text-[#3F4A45]">{r.label || "Unlabeled"}</p>
                      <p className="truncate text-xs text-[#7A8580]">
                        {acct?.name ?? `Account ${r.accountId}`}
                        {r.description && ` · ${r.description}`}
                      </p>
                    </div>

                    {/* Transaction category */}
                    <span
                      className="hidden shrink-0 items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs sm:inline-flex"
                      style={{ backgroundColor: categoryColor + "22", color: categoryColor }}
                    >
                      <span className="h-2 w-2 rounded-full" style={{ backgroundColor: categoryColor }} />
                      {categoryName}
                    </span>

                    {/* Transaction amount, green for income and red for expense */}
                    <div className="w-28 shrink-0 text-right">
                      <span
                        className={`font-medium tabular-nums ${
                          r.amount >= 0 ? "text-[#3E8E5A]" : "text-[#B5574B]"
                        }`}
                      >
                        {r.amount >= 0 ? "+" : ""}
                        {formatMoney(r.amount, acct?.currency ?? "USD")}
                      </span>
                      {/* Also show the amount in the home currency if it is different */}
                      {convertedText !== "" && (
                        <p className="text-xs tabular-nums text-[#7A8580]">{convertedText}</p>
                      )}
                    </div>
                  </li>
                );
              })}
            </ul>
          )}
        </div>
      </div>

      {/* Show the form when Add transaction is clicked */}
      {showForm && accounts.length > 0 && (
        <AddTransactionForm
          accounts={accounts}
          categories={categories}
          onCategoryCreated={addCategory}
          onClose={() => setShowForm(false)}
          onCreated={(row) => {
            setRows((prev) => [row, ...prev]);
            setShowForm(false);
          }}
        />
      )}
    </main>
  );
}

//Information the form gets from the page
interface AddTransactionFormProps {
  accounts: Account[];
  categories: Category[];
  onCategoryCreated: (category: Category) => void;
  onClose: () => void;
  onCreated: (row: Row) => void; 
}

//Form for adding a new transaction
function AddTransactionForm({ accounts, categories, onCategoryCreated, onClose, onCreated }: AddTransactionFormProps) {
  //Pick "Other" as the starting category, or the first one if "Other" is missing
  let startCategoryId = 0;
  for (const c of categories) {
    if (c.name === "Other") {
      startCategoryId = c.id;
    }
  }
  if (startCategoryId === 0 && categories.length > 0) {
    startCategoryId = categories[0].id;
  }

  //Store the information entered in the form
  const [date, setDate] = useState(today());
  const [kind, setKind] = useState<"expense" | "income">("expense");
  const [amount, setAmount] = useState("");
  const [label, setLabel] = useState("");
  const [description, setDescription] = useState("");
  const [categoryId, setCategoryId] = useState(startCategoryId);
  const [accountId, setAccountId] = useState(accounts[0].id);
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  //Store the "new category" box
  const [showNewCategory, setShowNewCategory] = useState(false);
  const [newCategoryName, setNewCategoryName] = useState("");
  const [categoryError, setCategoryError] = useState("");
  const [creatingCategory, setCreatingCategory] = useState(false);

  //Create a new category and select it
  async function handleCreateCategory() {
    setCategoryError("");
    const name = newCategoryName.trim();

    //Check that the name is not empty
    if (name === "") {
      setCategoryError("Enter a category name.");
      return;
    }

    //Check that the category does not already exist 
    for (const c of categories) {
      if (c.name.toLowerCase() === name.toLowerCase()) {
        setCategoryError("That category already exists.");
        return;
      }
    }

    //Send the new category to the backend
    setCreatingCategory(true);
    try {
      const created = await apiFetch<BackendCategory>("/api/categories", {
        method: "POST",
        body: JSON.stringify({ categoryLabel: name }),
      });
      const category = categoryFromBackend(created);
      onCategoryCreated(category);
      setCategoryId(category.id); //Select the new category in the dropdown
      setNewCategoryName("");
      setShowNewCategory(false);
    } catch (e) {
      //Show an error if the category cannot be saved
      if (e instanceof ApiRequestError) {
        setCategoryError(e.message);
      } else {
        setCategoryError("Could not create the category.");
      }
    } finally {
      setCreatingCategory(false);
    }
  }

  //Save the transaction when the form is submitted
  async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
    e.preventDefault(); //Stop the form from reloading the page
    setError("");

    //Check if the amount is valid
    const value = Number(amount);
    if (!Number.isFinite(value) || value <= 0) {
      setError("Enter an amount greater than 0.");
      return;
    }

    //Check that a category is selected
    if (categoryId === 0) {
      setError("Choose a category.");
      return;
    }

    //Use negative amount for expense and positive for income
    const signed = (kind === "expense" ? -value : value).toFixed(2);

    //Create the transaction data to send
    const body = { date, amount: signed, label, description, categoryId, accountId };

    //Add a transaction using mock data
    if (USE_MOCK) {
      onCreated({ id: Date.now(), ...body, amount: Number(signed) });
      return;
    }

    //Send the new transaction to the backend
    setSaving(true);
    try {
      const created = await apiFetch<BackendTransaction>("/api/transactions", {
        method: "POST",
        body: JSON.stringify(body),
      });
      onCreated(fromBackend(created));
    } catch (e) {
      //Show an error if the transaction cannot be saved
      setError(e instanceof ApiRequestError ? e.message : "Could not save the transaction.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-10 flex items-center justify-center bg-black/20 px-4" onClick={onClose}>
      {/* Add transaction form */}
      <form
        onSubmit={handleSubmit}
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-md space-y-4 rounded-2xl border border-[#EDE6D3] bg-[#FFFDF7] p-6 shadow-lg"
      >
        {/* Form title and close button */}
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-semibold text-[#3F4A45]">Add transaction</h2>
          <button type="button" onClick={onClose} aria-label="Close" className="text-[#7A8580]">
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Choose expense or income */}
        <div className="grid grid-cols-2 gap-2 rounded-lg bg-[#EEF5EC] p-1 text-sm">
          {(["expense", "income"] as const).map((k) => (
            <button
              key={k}
              type="button"
              onClick={() => setKind(k)}
              className={`rounded-md py-1.5 capitalize ${kind === k ? "bg-white font-medium text-[#3F5747] shadow-sm" : "text-[#7A8580]"}`}
            >
              {k}
            </button>
          ))}
        </div>

        {/* Date and amount inputs */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label htmlFor="date" className="text-sm text-[#4F6657]">Date</label>
            <input
              id="date"
              type="date"
              required
              value={date}
              onChange={(e)=>setDate(e.target.value)}
              className={inputClass}
            />
          </div>
          <div>
            <label htmlFor="amount" className="text-sm text-[#4F6657]">Amount</label>
            <input
              id="amount"
              type="number"
              step="0.01"
              min="0.01"
              required
              placeholder="0.00"
              value={amount}
              onChange={(e)=>setAmount(e.target.value)}
              className={inputClass}
            />
          </div>
        </div>

        {/* Transaction label */}
        <div>
          <label htmlFor="label" className="text-sm text-[#4F6657]">Label</label>
          <input
            id="label"
            maxLength={255}
            placeholder="e.g. Walmart"
            value={label}
            onChange={(e)=>setLabel(e.target.value)}
            className={inputClass}
          />
        </div>

        {/* Optional transaction description */}
        <div>
          <label htmlFor="description" className="text-sm text-[#4F6657]">Description (optional)</label>
          <textarea
            id="description"
            maxLength={4095}
            rows={2}
            value={description}
            onChange={(e)=>setDescription(e.target.value)}
            className={inputClass}
          />
        </div>

        {/* Choose category and account */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <div className="flex h-5 items-center justify-between">
              <label htmlFor="category" className="text-sm text-[#4F6657]">Category</label>
              {/* Open the box for making a new category */}
              <button
                type="button"
                onClick={() => setShowNewCategory(true)}
                className="text-xs font-medium text-[#5E8A6B] hover:underline"
              >
                + New
              </button>
            </div>
            <select
              id="category"
              value={categoryId}
              onChange={(e)=>setCategoryId(Number(e.target.value))}
              className={inputClass}
            >
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>
          <div>
            <div className="flex h-5 items-center">
              <label htmlFor="account" className="text-sm text-[#4F6657]">Account</label>
            </div>
            <select
              id="account"
              value={accountId}
              onChange={(e)=>setAccountId(Number(e.target.value))}
              className={inputClass}
            >
              {accounts.map((a) => (
                <option key={a.id} value={a.id}>{a.name}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Box for making a new category */}
        {showNewCategory && (
          <div className="rounded-lg border border-[#CFE0CF] bg-[#F6FAF5] p-3">
            <label htmlFor="newCategory" className="text-sm text-[#4F6657]">New category name</label>
            <div className="mt-1 flex gap-2">
              <input
                id="newCategory"
                maxLength={255}
                placeholder="e.g. Pets"
                value={newCategoryName}
                onChange={(e)=>setNewCategoryName(e.target.value)}
                onKeyDown={(e) => {
                  //Pressing Enter here should add the category, not save the transaction
                  if (e.key === "Enter") {
                    e.preventDefault();
                    handleCreateCategory();
                  }
                }}
                className="w-full rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-[#3F4A45] focus:outline-none focus:ring-2 focus:ring-[#A9C9B0]"
              />
              <button
                type="button"
                onClick={handleCreateCategory}
                disabled={creatingCategory}
                className="shrink-0 rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] px-3 py-2 text-sm font-medium text-[#3F5747] hover:bg-[#E4EFE3] disabled:opacity-60"
              >
                Add
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowNewCategory(false);
                  setNewCategoryName("");
                  setCategoryError("");
                }}
                className="shrink-0 px-1 text-sm text-[#7A8580]"
              >
                Cancel
              </button>
            </div>
            {categoryError && <p className="mt-2 text-sm text-[#B5574B]">{categoryError}</p>}
          </div>
        )}

        {/* Show an error if saving fails */}
        {error && <p className="text-sm text-[#B5574B]">{error}</p>}

        {/* Save transaction button */}
        <button
          type="submit"
          disabled={saving}
          className="w-full rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] px-4 py-3 font-medium text-[#3F5747] transition hover:bg-[#E4EFE3] disabled:opacity-60"
        >
          {saving ? "Saving…" : "Save transaction"}
        </button>
      </form>
    </div>
  );
}