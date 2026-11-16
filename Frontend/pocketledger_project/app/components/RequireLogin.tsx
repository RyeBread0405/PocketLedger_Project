"use client";
import { useEffect, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { getToken } from "../../lib/auth";

//We don't need to listen for token changes while the page is open
function subscribe() {
  return () => {};
}

//In the browser check if a token is saved
function hasToken() {
  return getToken() !== null;
}

//On the server there is no localStorage, so count it as not logged in yet
function hasTokenOnServer() {
  return false;
}

//Only show the page if the user is logged in, otherwise go to the login page
export default function RequireLogin({ children }: { children: React.ReactNode }) {
  const router = useRouter();

  //React's way of reading the token
  const loggedIn = useSyncExternalStore(subscribe, hasToken, hasTokenOnServer);

  useEffect(() => {
    //No token means the user is not logged in
    if (getToken() === null) {
      //replace() so the Back button doesn't come back to this page
      router.replace("/login");
    }
  }, [router]);

  //Show nothing while checking, so the page doesn't flash before the redirect
  if (!loggedIn) {
    return null;
  }

  return <>{children}</>;
}