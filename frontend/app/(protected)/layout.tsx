"use client";

import { useUser } from "@/Context/UserProvider";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

function Layout({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const isAuthenticated = useUser().isAuthenticated();

  useEffect(() => {
    if (!isAuthenticated) {
      console.log("User is not authenticated. Redirecting to login page.");
      router.push("/auth/login");
    }
  }, [isAuthenticated, router]);

  if (!isAuthenticated) return null;
  return <div className="w-full h-full px-8 py-8">{children}</div>;
}

export default Layout;
