"use client";

import { Check, X } from "lucide-react";
import { Spinner } from "@/components/ui/spinner";
import { ServerStatus } from "@/store/zustand";

export function StatusIcon({ status }: { status: ServerStatus }) {
  const isResolved = status === "UP" || status === "FAILED";

  return (
    <div className="relative size-5 shrink-0">
      {/* Spinner: visible while PENDING/CHECKING, fades+shrinks out on resolve */}
      <div
        className={`absolute inset-0 flex items-center justify-center transition-all duration-300 ${
          isResolved ? "opacity-0 scale-50" : "opacity-100 scale-100"
        }`}
      >
        <Spinner className="size-5 text-muted-foreground" />
      </div>

      {/* Check: scales/fades in only when UP */}
      <div
        className={`absolute inset-0 flex items-center justify-center transition-all duration-300 ${
          status === "UP" ? "opacity-100 scale-100" : "opacity-0 scale-50"
        }`}
      >
        <span className="flex items-center justify-center size-5 rounded-full bg-green-500">
          <Check className="size-3.5 text-white" strokeWidth={3} />
        </span>
      </div>

      {/* X: scales/fades in only when FAILED */}
      <div
        className={`absolute inset-0 flex items-center justify-center transition-all duration-300 ${
          status === "FAILED" ? "opacity-100 scale-100" : "opacity-0 scale-50"
        }`}
      >
        <span className="flex items-center justify-center size-5 rounded-full bg-red-500">
          <X className="size-3.5 text-white" strokeWidth={3} />
        </span>
      </div>
    </div>
  );
}
