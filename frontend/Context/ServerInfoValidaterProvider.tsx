"use client";

import { useBootSequence } from "@/hooks/useBootSequence";
import { BOOT_SEQUENCE } from "@/lib/boot-sequence";
import { X, RotateCw } from "lucide-react";
import { createContext } from "react";
import { useServerStatusStore } from "@/store/zustand";
import { StatusIcon } from "@/components/StatusIcon";

const serverInfoValidatorProvider = createContext(null);

function ServerInfoValidatorProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const { done, failed, failedStep, servers } = useBootSequence();
  const reset = useServerStatusStore((s) => s.reset);

  if (failed && failedStep) {
    return (
      <div className="w-dvw h-dvh flex items-center justify-center">
        <div className="flex flex-col gap-4 px-8 py-6 border rounded-xl shadow-sm max-w-sm text-center">
          <X className="text-red-500 mx-auto" size={32} />
          <p className="font-semibold">{failedStep.label} failed to start</p>
          <p className="text-sm text-muted-foreground">
            We tried multiple times but couldn&apos;t reach this service. It may
            still be starting up on Render — you can try again.
          </p>
          <button
            onClick={reset}
            className="inline-flex items-center justify-center gap-2 rounded-md border px-4 py-2 text-sm hover:bg-muted"
          >
            <RotateCw size={16} /> Retry
          </button>
        </div>
      </div>
    );
  }

  if (!done) {
    return (
      <div className="w-dvw h-dvh flex items-center justify-center">
        <div className="flex flex-col gap-4 px-8 py-6 border rounded-xl shadow-sm min-w-[320px]">
          <div className="flex flex-col gap-3">
            {BOOT_SEQUENCE.map(({ key, label }) => {
              const status = servers[key].status;
              return (
                <div key={key} className="flex items-center gap-3">
                  <StatusIcon status={status} />
                  <span className="flex-1">{label}</span>
                  <span
                    className={`text-sm font-medium transition-colors duration-300 ${
                      status === "UP"
                        ? "text-green-500"
                        : status === "FAILED"
                          ? "text-red-500"
                          : status === "CHECKING"
                            ? "text-yellow-500"
                            : "text-muted-foreground"
                    }`}
                  >
                    {status}
                  </span>
                </div>
              );
            })}
          </div>
          <p className="text-sm text-muted-foreground text-center mt-2">
            Waking up services one at a time — Render&apos;s free tier sleeps
            inactive instances and only runs one at a time, so each service
            needs to fully boot before the next can start.
          </p>
        </div>
      </div>
    );
  }

  return (
    <serverInfoValidatorProvider.Provider value={null}>
      {children}
    </serverInfoValidatorProvider.Provider>
  );
}

export default ServerInfoValidatorProvider;
