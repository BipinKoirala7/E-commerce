"use client";

import { ApiEndpoint } from "@/lib/ApiEndpoint";
import { fetcher } from "@/lib/axios";
import { User, UserResponse } from "@/types";
import { createContext, useContext, useMemo } from "react";
import useSWR, { SWRConfig } from "swr";

type UserProviderT = {
  data: User | null;
  isAuthenticated: () => boolean;
};

const swrConfig = {
  fetcher,
  shouldRetryOnError: false,
  revalidateOnFocus: false,
};

const UserProviderContext = createContext<UserProviderT>({
  data: null,
  isAuthenticated: () => false,
});

function UserProvider({ children }: { children: React.ReactNode }) {
  const { isLoading, data } = useSWR<UserResponse>(ApiEndpoint.USER, fetcher, {
    shouldRetryOnError: false,
    revalidateOnFocus: false,
  });

  const value = useMemo(
    () => ({
      data: data?.data ?? null,
      isAuthenticated: () => !!data?.data,
    }),
    [data],
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
