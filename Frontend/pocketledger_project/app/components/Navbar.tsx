"use client";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { clearToken } from "../../lib/auth";
const Pages = {
    dashboard: "/dashboard",
    accounts: "/accounts",
    budgets: "/budgets",
    import: "/import",
    transactions: "/transactions",
    settings: "/settings",
    login: "/login",
};

//Style for each link, light green background when the mouse is over it
const linkClass = "rounded-lg px-2 py-1 hover:bg-[#EEF5EC]";

export default function Navbar() {
    const router = useRouter();

    function handleLogout() {
        clearToken();
        router.push("/login");
    }

    return (
        <div className="flex flex-col md:flex-row items-center
        gap-4 border-b border-[#EDE6D3] bg-[#FFFDF7] px-4 py-3
        text-[#3F5747]">
            {/* Title Box. Also links to dashboard */}
            <span className="basis-1/4 p-2 text-center text-lg font-semibold">
                <Link href={Pages.dashboard}>PocketLedger</Link>
            </span>
            {/* Navigation Box */}
            <nav className="w-full grow grid grid-cols-3 sm:grid-cols-4 lg:grid-cols-7 gap-2 text-center text-sm">
                <Link href={Pages.dashboard} className={linkClass}>Dashboard</Link>
                <Link href={Pages.accounts} className={linkClass}>Accounts</Link>
                <Link href={Pages.budgets} className={linkClass}>Budgets</Link>
                <Link href={Pages.import} className={linkClass}>Import</Link>
                <Link href={Pages.transactions} className={linkClass}>Transactions</Link>
                <Link href={Pages.settings} className={linkClass}>Settings</Link>
                <button className={linkClass} onClick={handleLogout}>
                    Logout
                </button>
            </nav>
        </div>
    );
}