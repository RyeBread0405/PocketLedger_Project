"use client";
import {useState} from "react";
import Link from "next/link";
import { Wallet } from "lucide-react";
import { mockRegister } from "../../mocks";
import {saveToken} from "../../lib/auth";
const BACKEND_URL = "http://localhost:8080";
const USE_MOCK = true; //Use mock login for now, change to false when the backend login is ready.
const grainTexture =
  "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='2'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E\")"; 

  export default function RegisterPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  async function handleRegister(e: React.SubmitEvent<HTMLFormElement>) {
    e.preventDefault(); //Stop the form from reloading the page
    setError("");
    if (password.length < 8) { //Backend needs at least 8 characters
      setError("Password must be at least 8 characters.");
      return;
    }
    if (password.length > 72) { //Backend allows at most 72 characters
      setError("Password must be 72 characters or less.");
      return;
    }
    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }
    if (USE_MOCK) { //student@okstate.edu is already taken in the mock data
      try {
        const data = await mockRegister({ name, email, password });
        saveToken(data.token);
        window.location.href = "/";
      } catch {
        setError("This email is already registered. Try signing in.");
      }
      return;
    }
    const res = await fetch(`${BACKEND_URL}/api/auth/register`, { //Check this path with the backend team
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, email, password }),
    });
    if (!res.ok) {
      setError("Could not create your account. This email may already be registered.");
      return;
    }
    const data = await res.json();
    saveToken(data.token); //Log the user in right after signing up
    window.location.href = "/";
  }
  return (
    <main className="relative flex min-h-screen items-center justify-center overflow-hidden bg-linear-to-b from-[#E4EFE3] via-[#E3EDF1] via-40% to-[#FBF8EF] to-75% px-6 py-12">
      {/* Background grid, same as the login page */}
      <div className="pointer-events-none absolute inset-0 opacity-20 [background-image:linear-gradient(#9BB5A3_1px,transparent_1px),linear-gradient(90deg,#9BB5A3_1px,transparent_1px)] [background-size:32px_32px]" />
      <div className="relative w-full max-w-sm">
        {/* App name and icon */}
        <div className="mb-6 flex items-center justify-center gap-3 text-[#3F5747]">
          <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-white/70 text-[#5E8A6B]">
            <Wallet className="h-6 w-6" />
          </div>
          <span className="text-xl font-semibold">PocketLedger</span>
        </div>
        {/* Sign up card */}
        <div className="rounded-2xl border border-[#EDE6D3] bg-[#FFFDF7] p-8 shadow-sm">
          <h1 className="text-2xl font-semibold text-[#3F4A45]">Create an account</h1>
          <p className="mt-2 text-sm text-[#7A8580]">
            Your home currency starts as USD. You can change it later in Settings.
          </p>
          <form onSubmit={handleRegister} className="mt-8 space-y-4">
            <div>
              <label htmlFor="name" className="text-sm text-[#4F6657]">Name</label>
              <input
                id="name"
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="mt-1 w-full rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-[#3F4A45] focus:outline-none focus:ring-2 focus:ring-[#A9C9B0]"
              />
            </div>
            <div>
              <label htmlFor="email" className="text-sm text-[#4F6657]">Email</label>
              <input
                id="email"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="mt-1 w-full rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-[#3F4A45] focus:outline-none focus:ring-2 focus:ring-[#A9C9B0]"
              />
            </div>
            <div>
              <label htmlFor="password" className="text-sm text-[#4F6657]">Password</label>
              <input
                id="password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="mt-1 w-full rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-[#3F4A45] focus:outline-none focus:ring-2 focus:ring-[#A9C9B0]"
              />
              <p className="mt-1 text-xs text-[#9AA39E]">At least 8 characters.</p>
            </div>
            <div>
              <label htmlFor="confirmPassword" className="text-sm text-[#4F6657]">Confirm password</label>
              <input
                id="confirmPassword"
                type="password"
                required
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="mt-1 w-full rounded-lg border border-[#CFE0CF] bg-white px-3 py-2 text-[#3F4A45] focus:outline-none focus:ring-2 focus:ring-[#A9C9B0]"
              />
            </div>
            {/* Only shows when something is wrong */}
            {error && <p className="text-sm text-[#B5574B]">{error}</p>}
            <button
              type="submit"
              className="w-full rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] px-4 py-3 font-medium text-[#3F5747] transition hover:-translate-y-0.5 hover:bg-[#E4EFE3] hover:shadow-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#A9C9B0]"
            >
              Create account
            </button>
          </form>
          <p className="mt-6 text-center text-sm text-[#7A8580]">
            Already have an account?{" "}
            <Link href="/login" className="font-medium text-[#5E8A6B] hover:underline">
              Sign in
            </Link>
          </p>
        </div>
      </div>
      {/* Adds a light grain texture over the full page */}
      <div
        className="pointer-events-none absolute inset-0 opacity-[0.15] mix-blend-multiply"
        style={{ backgroundImage: grainTexture }}
      />
    </main>
  );
}