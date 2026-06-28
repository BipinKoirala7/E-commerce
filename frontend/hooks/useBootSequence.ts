"use client";

import useSWR from "swr";
import { healthFetcher } from "@/lib/axios";
import { useServerStatusStore } from "@/store/zustand";
import {
  BOOT_SEQUENCE,
  MAX_ATTEMPTS,
  RETRY_INTERVAL_MS,
} from "@/lib/boot-sequence";
import { useEffect } from "react";

export function useBootSequence() {
  const currentIndex = useServerStatusStore((s) => s.currentIndex);
  const servers = useServerStatusStore((s) => s.servers);
  const setStatus = useServerStatusStore((s) => s.setStatus);
  const incrementAttempts = useServerStatusStore((s) => s.incrementAttempts);
  const advance = useServerStatusStore((s) => s.advance);

  const step =
    currentIndex < BOOT_SEQUENCE.length ? BOOT_SEQUENCE[currentIndex] : null;
  const stepFailed = step ? servers[step.key].status === "FAILED" : false;
  const allDone = currentIndex >= BOOT_SEQUENCE.length;

  const shouldFetch = step && !stepFailed;

  const { data, error, isLoading } = useSWR(
    shouldFetch ? step!.url : null,
    healthFetcher,
    {
      refreshInterval: (latest) =>
        latest?.status === "UP" ? 0 : RETRY_INTERVAL_MS,
      errorRetryInterval: RETRY_INTERVAL_MS,
      shouldRetryOnError: true,
      dedupingInterval: 0,
      revalidateOnFocus: false,
    },
  );

  useEffect(() => {
    if (!step || stepFailed) return;

    if (data?.status === "UP") {
      // only act if we're not already marked UP, to avoid redundant advance() calls
      if (useServerStatusStore.getState().servers[step.key].status !== "UP") {
        setStatus(step.key, "UP");
        advance();
      }
      return;
    }

    if (error) {
      const current = useServerStatusStore.getState().servers[step.key];
      const attemptsSoFar = current.attempts + 1;
      incrementAttempts(step.key);

      if (attemptsSoFar >= MAX_ATTEMPTS) {
        setStatus(step.key, "FAILED");
      } else if (current.status !== "CHECKING") {
        setStatus(step.key, "CHECKING");
      }
      return;
    }
    // no data, no error yet (still loading first request) — leave status as-is
  }, [data, error, step, stepFailed, setStatus, incrementAttempts, advance]);

  return {
    done: allDone,
    failed: stepFailed,
    failedStep: stepFailed ? step : null,
    servers,
  };
}
