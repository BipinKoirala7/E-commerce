"use client";

import { ApiEndpoint } from "@/lib/ApiEndpoint";
import { fetcher } from "@/lib/axios";
import { User, UserResponse } from "@/types";
import { createContext, useContext, useMemo } from "react";
import useSWR, { SWRConfig } from "swr";

type UserProviderT = {
  data: User | null;
  isLoading: boolean;
  error: Error | null;
  isAuthenticated: () => boolean;
};

// {
//   fetcher,
//   onErrorRetry: (error, key, config, revalidate, { retryCount }) => {
//     if (error?.status === 401 || error?.response?.status === 401) return;
//     if (retryCount >= 3) return;
//     revalidate({ retryCount });
//   },
// }

const swrConfig = {
  fetcher,
  shouldRetryOnError: false,
  revalidateOnFocus: false,
};

const UserProviderContext = createContext<UserProviderT>({
  data: null,
  isLoading: false,
  error: null,
  isAuthenticated: () => false,
});

function UserProvider({ children }: { children: React.ReactNode }) {
  const { isLoading, data, error } = useSWR<UserResponse>(
    ApiEndpoint.GET_USER,
    fetcher,
    {
      shouldRetryOnError: false,
      revalidateOnFocus: false,
    },
  );

  const value = useMemo(
    () => ({
      data: data?.data ?? null,
      isLoading,
      error: error ?? null,
      isAuthenticated: () => !!data?.data,
    }),
    [data, isLoading, error],
  );
  if (isLoading)
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Loading...
      </div>
    );

  return (
    <SWRConfig value={swrConfig}>
      <UserProviderContext.Provider value={value}>
        {children}
      </UserProviderContext.Provider>
    </SWRConfig>
  );
}

export function useUser() {
  return useContext(UserProviderContext);
}

export default UserProvider;
