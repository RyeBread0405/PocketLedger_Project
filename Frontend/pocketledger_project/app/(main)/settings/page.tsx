"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { LogOut, Coins } from "lucide-react";
import { apiFetch, ApiRequestError } from "../../../lib/api";
import { clearToken } from "../../../lib/auth";

//Settings data returned by GET /api/settings and PUT /api/settings
interface SettingsResponse {
  currency: string;
}

//Currencies the user can pick from
const CURRENCIES = [
  { code: "USD", name: "US Dollar" },
  { code: "EUR", name: "Euro" },
  { code: "GBP", name: "British Pound" },
  { code: "CAD", name: "Canadian Dollar" },
  { code: "MXN", name: "Mexican Peso" },
  { code: "JPY", name: "Japanese Yen" },
  { code: "CNY", name: "Chinese Yuan" },
  { code: "INR", name: "Indian Rupee" },
];

//Get the error message to show the user
function getErrorMessage(e: unknown, defaultText: string) {
  if (e instanceof ApiRequestError) {
    return e.message;
  }
  return defaultText;
}

//Shared style for the cards on this page
const cardClass = "rounded-2xl border border-[#EDE6D3] bg-[#FFFDF7] p-6 shadow-sm";

//Shared style for the buttons on this page
const buttonClass =
  "flex items-center gap-2 rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] px-4 py-2 font-medium text-[#3F5747] transition hover:bg-[#E4EFE3] disabled:cursor-not-allowed disabled:opacity-50";

export default function SettingsPage() {
  const router = useRouter();

  //The currency picked in the dropdown
  const [currency, setCurrency] = useState("USD");

  //Store loading, saving, and message states
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [saveError, setSaveError] = useState("");

  //Load the user's current settings when the page opens
  useEffect(() => {
    async function loadSettings() {
      try {
        const data = await apiFetch<SettingsResponse>("/api/settings");
        setCurrency(data.currency);
        setLoadError("");
      } catch (e) {
        setLoadError(getErrorMessage(e, "Could not load your settings."));
      }
      setLoading(false);
    }
    loadSettings();
  }, []);

  //Save the picked currency to the backend
  async function handleSave() {
    setMessage("");
    setSaveError("");
    setSaving(true);
    try {
      const data = await apiFetch<SettingsResponse>("/api/settings", {
        method: "PUT",
        body: JSON.stringify({ currency: currency }),
      });
      setCurrency(data.currency);
      setMessage("Saved. Your home currency is now " + data.currency + ".");
    } catch (e) {
      setSaveError(getErrorMessage(e, "Could not save your settings."));
    }
    setSaving(false);
  }

  //Log out: remove the token and go to the login page
  function handleLogout() {
    clearToken();
    router.push("/login");
  }

  //Text on the save button
  let saveButtonText = "Save";
  if (saving) {
    saveButtonText = "Saving…";
  }

  return (
    <main className="relative min-h-screen bg-linear-to-b from-[#E4EFE3] via-[#E3EDF1] via-40% to-[#FBF8EF] to-75% px-4 py-8 md:px-10">
      {/* Background grid */}
      <div className="pointer-events-none absolute inset-0 opacity-20 [background-image:linear-gradient(#9BB5A3_1px,transparent_1px),linear-gradient(90deg,#9BB5A3_1px,transparent_1px)] [background-size:32px_32px]" />

      <div className="relative mx-auto max-w-3xl">
        {/* Page title */}
        <h1 className="text-3xl font-semibold text-[#3F5747]">Settings</h1>
        <p className="mt-1 text-sm text-[#7A8580]">Manage your preferences.</p>

        {/* Home currency card */}
        <div className={cardClass + " mt-6"}>
          <div className="flex items-center gap-2">
            <Coins className="h-5 w-5 text-[#3E8E5A]" />
            <h2 className="text-lg font-semibold text-[#3F4A45]">Home currency</h2>
          </div>
          <p className="mt-1 text-sm text-[#7A8580]">
            The currency you want to see your totals in.
          </p>

          {/* Show loading or error message */}
          {loading && <p className="mt-4 text-sm text-[#7A8580]">Loading…</p>}
          {loadError !== "" && <p className="mt-4 text-sm text-[#B5574B]">{loadError}</p>}

          {/* Currency dropdown and save button */}
          {!loading && loadError === "" && (
            <div className="mt-4 flex flex-wrap items-center gap-3">
              <label htmlFor="currency" className="sr-only">Home currency</label>
              <select
                id="currency"
                value={currency}
                onChange={(e) => {
                  setCurrency(e.target.value);
                  setMessage("");
                }}
                className="rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-[#3F4A45] focus:outline-none focus:ring-2 focus:ring-[#A9C9B0]"
              >
                {CURRENCIES.map((c) => (
                  <option key={c.code} value={c.code}>{c.code} – {c.name}</option>
                ))}
              </select>

              <button onClick={handleSave} disabled={saving} className={buttonClass}>
                {saveButtonText}
              </button>
            </div>
          )}

          {/* Show a message after saving */}
          {message !== "" && <p className="mt-3 text-sm text-[#3E8E5A]">{message}</p>}
          {saveError !== "" && <p className="mt-3 text-sm text-[#B5574B]">{saveError}</p>}

          {/* Explain what this setting does right now */}
          <p className="mt-4 text-xs text-[#9AA39E]">
            Totals on the Transactions page are converted to this currency using the exchange rate from each transaction&apos;s date.
          </p>
        </div>

        {/* Log out card */}
        <div className={cardClass + " mt-6"}>
          <h2 className="text-lg font-semibold text-[#3F4A45]">Account</h2>
          <p className="mt-1 text-sm text-[#7A8580]">Sign out of PocketLedger on this device.</p>
          <button onClick={handleLogout} className={buttonClass + " mt-4"}>
            <LogOut className="h-4 w-4" /> Log out
          </button>
        </div>
      </div>
    </main>
  );
}