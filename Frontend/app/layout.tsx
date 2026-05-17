import type { Metadata } from "next";
import { Nunito } from "next/font/google";
import { Belleza } from "next/font/google";
import { Style_Script } from "next/font/google";

import "./globals.css";
import MainNav from "../components/MainNav/MainNav";
import Footer from "../components/Footer/Footer";
import React from "react";
import UserProvider from "@/Context/UserProvider";
import { Toaster } from "react-hot-toast";

//  Adding Nunito Font for Standard use
const nunito = Nunito({
  variable: "--font-nunito",
  subsets: ["latin"],
});

//  Adding Belleza Font for Header and Specific use
const belleza = Belleza({
  weight: "400",
  variable: "--font-belleza",
  subsets: ["latin"],
});

//  Adding Style Script for logo
const styleScript = Style_Script({
  weight: "400",
  variable: "--font-logo",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "E-commerce",
  description: "Shop What you like",
};

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`flex flex-col w-full text-text ${nunito.variable} ${belleza.variable} ${styleScript.variable} antialiased`}
      >
        <UserProvider>
          <main className="w-full min-h-screen h-full flex flex-col">
            <MainNav />
            <div className="w-full flex-1 text-text flex justify-center">
              {children}
            </div>
          </main>
          <Footer />
          <Toaster />
        </UserProvider>
      </body>
    </html>
  );
}
