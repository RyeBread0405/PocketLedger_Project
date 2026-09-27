"use client";
import { Wallet, Globe, Sparkles, PieChart } from "lucide-react";
const BACKEND_URL = "http://localhost:8080";
const USE_MOCK = true; //Use mock login for now, change to false when OAuth is ready
const features = [
  { icon: Globe, text: "Track spending in any currency" },
  { icon: Sparkles, text: "AI sorts your transactions for you" },
  { icon: PieChart, text: "Set monthly budgets and stay on track" },
];
const grainTexture =
  "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='2'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E\")"; 
 
export default function LoginPage() { 
  function handleLogin() { 
    if (USE_MOCK) { //Go straight to the home page while using mock data
      window.location.href = "/"; 
      return; 
    } 
    window.location.href = `${BACKEND_URL}/oauth2/authorization/google`; //Use Google OAuth when the backend login is ready
  } 
  return ( 
    <main className="relative flex min-h-screen flex-col overflow-hidden bg-linear-to-b from-[#E4EFE3] via-[#E3EDF1] via-40% to-[#FBF8EF] to-75% md:flex-row md:bg-linear-to-r"> 
      {/* Background grid */}
      <div className="pointer-events-none absolute inset-0 opacity-20 [background-image:linear-gradient(#9BB5A3_1px,transparent_1px),linear-gradient(90deg,#9BB5A3_1px,transparent_1px)] [background-size:32px_32px]" /> 
      {/* Left side with app information */}
      <section className="relative px-8 py-12 text-[#3F5747] md:flex md:w-1/2 md:flex-col md:justify-center md:px-16"> 
        {/* Soft background shapes */}
        <div className="pointer-events-none absolute -top-24 right-0 h-80 w-80 rounded-full bg-[#FBF1C9] opacity-70 blur-3xl" /> 
        <div className="pointer-events-none absolute -left-20 bottom-0 h-80 w-80 rounded-full bg-[#C9DDEB] opacity-70 blur-3xl" /> 
        <div className="relative"> 
          {/* App name and icon */}
          <div className="flex items-center gap-3"> 
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-white/70 text-[#5E8A6B]"> 
              <Wallet className="h-6 w-6" /> 
            </div> 
            <span className="text-xl font-semibold">PocketLedger</span> 
          </div> 
          <h1 className="mt-10 text-3xl font-semibold leading-tight md:text-4xl"> 
            Welcome to PocketLedger 
          </h1> 
          {/* Main app features */}
          <ul className="mt-8 space-y-4"> 
            {features.map(({ icon: Icon, text }) => ( 
              <li key={text} className="flex items-center gap-3"> 
                <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-white/60 text-[#6B93AE]"> 
                  <Icon className="h-4 w-4" /> 
                </div> 
                <span className="text-sm text-[#4F6657]">{text}</span> 
              </li> 
            ))} 
          </ul> 
          {/* Example transaction and budget cards */}
          <div className="relative mt-12 hidden h-36 md:block"> 
            <div className="absolute left-0 top-0 w-56 -rotate-3 rounded-xl border border-white/70 bg-white/60 p-4 shadow-sm backdrop-blur-sm"> 
              <div className="flex items-center justify-between text-sm"> 
                <span className="text-[#4F6657]">ALDI</span> 
                <span className="font-medium text-[#3F5747]">-$38.17</span> 
              </div> 
              <span className="mt-2 inline-block rounded-full bg-[#DDE9F2] px-2 py-0.5 text-xs text-[#6B93AE]"> 
                AI sorted · Groceries 
              </span> 
            </div> 
            <div className="absolute left-40 top-16 w-56 rotate-2 rounded-xl border border-white/70 bg-white/60 p-4 shadow-sm backdrop-blur-sm"> 
              <div className="flex items-center justify-between text-sm"> 
                <span className="text-[#4F6657]">Dining budget</span> 
                <span className="font-medium text-[#3F5747]">36%</span> 
              </div> 
              <div className="mt-2 h-2 rounded-full bg-[#E4EFE3]"> 
                <div className="h-2 w-[36%] rounded-full bg-[#9CC3A6]" /> 
              </div> 
            </div> 
          </div> 
        </div> 
      </section> 
      {/* Right side login area */}
      <section className="relative flex flex-1 items-center justify-center px-6 py-12"> 
        <div className="w-full max-w-sm rounded-2xl border border-[#EDE6D3] bg-[#FFFDF7] p-8 shadow-sm"> 
          <h2 className="text-2xl font-semibold text-[#3F4A45]">Sign in</h2> 
          <p className="mt-2 text-sm text-[#7A8580]"> 
            Use your Google account to continue. 
          </p> 
          {/* Google login button */}
          <button 
            onClick={handleLogin} 
            className="mt-8 w-full rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] px-4 py-3 font-medium text-[#3F5747] transition hover:-translate-y-0.5 hover:bg-[#E4EFE3] hover:shadow-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#A9C9B0]" 
          > 
            Continue with Google 
          </button> 
          <p className="mt-6 text-center text-xs leading-relaxed text-[#9AA39E]"> 
            No separate sign-up needed. 
            <br /> 
            First time here? Just continue with Google. 
          </p> 
        </div> 
      </section> 
      {/* Adds a light grain texture over the full page */}
      <div 
        className="pointer-events-none absolute inset-0 opacity-[0.15] mix-blend-multiply" 
        style={{ backgroundImage: grainTexture }} 
      /> 
    </main> 
  ); 
}