"use client";

import { handleEmailLogin, handleGoogleAuth } from "@/lib/api/auth";
import { useState } from "react";
import { FcGoogle } from "react-icons/fc";

function Page() {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");

  console.log("Email: " + email);
  console.log("Password: " + password);
  return (
    <div className="min-w-[33%] h-full bg-foreground mt-8 mb-8 flex flex-col gap-4 items-center justify-center p-8 rounded-2xl">
      <p className="header-font text-6xl">Login</p>
      <button
        className="bg-primary w-full flex items-center justify-center cursor-pointer gap-2 px-4 py-2 rounded-lg hover:bg-secondary smooth-transition"
        onClick={() => handleGoogleAuth()}
      >
        <FcGoogle className="text-[2rem]" />
        <p>Continue with Google</p>
      </button>
      <div className="flex gap-1 items-center py-2">
        <p className="opacity-50 text-[1.25rem]">Or</p>
      </div>
      <div className="w-full flex flex-col gap-3">
        <div className="w-full flex flex-col gap-2">
          <label className="w-full text-[1.1rem]" htmlFor="email">
            Email:
          </label>
          <input
            className="w-full text-[1.1rem] border border-primary outline-none px-3 py-2 rounded-lg"
            placeholder="Enter Email"
            type="text"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div className="w-full flex flex-col gap-2">
          <label className="w-full text-[1.1rem]" htmlFor="password">
            Password:
          </label>
          <input
            className="w-full text-[1.1rem] border border-primary outline-none px-3 py-2 rounded-lg"
            placeholder="Enter Password"
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
      </div>
      <button
        className="w-full flex bg-primary py-2 justify-center hover:bg-secondary rounded-lg cursor-pointer smooth-transition"
        onClick={() => handleEmailLogin({ email, password })}
      >
        Login
      </button>
    </div>
  );
}

export default Page;
