"use client";
import { useEffect } from "react";
import { getToken } from "../lib/auth";

export default function Home() {//The homepage sends people to the right place
  useEffect(() => {
    if (getToken()) {
      window.location.href = "/dashboard";//Logged in, go to the dashboard
    } else {
      window.location.href = "/login";//Not logged in, go to the login page
    }
  }, []);
  return null;
}