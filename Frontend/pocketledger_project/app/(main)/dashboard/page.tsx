"use client";

import { NextReactP5Wrapper } from "@p5-wrapper/next";
import { type Sketch } from "@p5-wrapper/react";
import React, { useEffect, useState } from "react";

import {mockTransactions, mockCategories, mockAccounts} from "../../../mocks";
import {apiFetch, ApiRequestError, checkBackendConnection} from "../../../lib/api";
import type { Account, AccountType } from "../../../types/api";

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

type DashboardSketchProps = { rotation: Account };
const sketch: Sketch<DashboardSketchProps> = (p5) => {
  let name = "";
  let type = "";

  p5.setup = () => { p5.createCanvas(600, 400); } 

  p5.updateWithProps = (props) => {
    name = props.rotation.name;
    type = props.rotation.type;
  };

  p5.draw = () => {
    p5.background(200);
    p5.text("Name: " + String(name), 10, 20);
    p5.text("Type: " + String(type), 10, 40);
  };
}

const BACKEND_URL = "http://localhost:8080";
const USE_MOCK = false; 




//  main  //
export default function DashboardPage() { 
  //Store loading, error, and form states
  const [loading, setLoading] = useState(!USE_MOCK);
  const [loadError, setLoadError] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [backendStatus, setBackendStatus] = useState("");

  const [accounts, setAccounts] = useState<Account[]>(USE_MOCK ? mockAccounts : []);
  //Find accounts by their ID
  const accountById = new Map<number, Account>();
  for (const account of accounts) {
    accountById.set(account.id, account);
  }
  //Load the user's accounts, then the transactions for every account
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
      } catch (e) {
        setLoadError(e instanceof ApiRequestError ? e.message : "Could not load transactions.");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  // need to replace this with just a loading screen.
  const [rotations, setRotations] = useState(mockAccounts[0]);
  /*const [rotation, setRotation] = useState(11);
  useEffect(() => {
    const interval = setInterval(
      () => setRotation((rotation) => rotation + 0.1),
      100
    );
    return () => {
      clearInterval(interval);
    };
  }, []);*/

    

    return (
            <main className="grid grow grid-flow-col grid-cols-6 grid-rows-6 gap-6 p-8 
            border-2 border-double border-black-300
            relative min-h-screen bg-linear-to-b from-[#E4EFE3] via-[#E3EDF1] via-40% to-[#FBF8EF] to-75% px-4 py-8 md:px-10">
                {/* Background grid */}
                <div className="pointer-events-none absolute inset-0 opacity-20 [background-image:linear-gradient(#9BB5A3_1px,transparent_1px),linear-gradient(90deg,#9BB5A3_1px,transparent_1px)] [background-size:32px_32px]" />

                

                {/* Box 1, no need for this due to navBar. Replace with something else. */}
                <div className="col-span-2 row-span-4 border-8 border-double border-black-300 
                flex flex-col justify-center gap-8 text-black">
                    <button 
                        onClick={() => setRotations(accounts[0])} 
                        className=" w-full rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] py-4 font-medium text-[#3F5747] transition hover:-translate-y-0.5 hover:bg-[#E4EFE3] hover:shadow-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#A9C9B0]" >
                        Load Account 1
                    </button> 
                    <button 
                        onClick={() => setRotations(accounts[1])} // that'll throw an error if theres only one account.
                        className=" w-full rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] py-4 font-medium text-[#3F5747] transition hover:-translate-y-0.5 hover:bg-[#E4EFE3] hover:shadow-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#A9C9B0]" >
                        Load Account 2
                    </button> 
                </div>


                {/* Chart Canvas */}
                <div className="col-span-4 row-span-4 border-8 border-double border-black-300 text-black">
                    <div className="flex items-center justify-center w-full h-full">
                    Learning how to fetch and pass data to sketch. currently just printing account names in sketch by passing the account object. Starts with Mock.
                    <NextReactP5Wrapper sketch={sketch} rotation={rotations} />
                    </div>
                </div>
            </main>
    )


    

}