"use client";

import { ApiEndpoint } from "@/lib/ApiEndpoint";
import { fetcher } from "@/lib/axios";
import { User, UserResponse } from "@/types";
import { createContext, useContext, useMemo } from "react";
import useSWR, { preload, SWRConfig } from "swr";

type UserProviderT = {
  data: User | null;
  isLoading: boolean;
  error: Error | null;
  isAuthenticated: () => boolean;
};

preload(ApiEndpoint.GET_USER, fetcher);

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
  );

  const info = useMemo(() => {
    return { data: data ? data.data : null, isLoading, error };
  }, [data, isLoading, error]);

  if (isLoading)
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Loading...
      </div>
    );

  const value = {
    isAuthenticated: () => !!data,
    ...info,
  };
  return (
    <SWRConfig
      value={{
        fetcher,
        onErrorRetry: (error, key, config, revalidate, { retryCount }) => {
          if (error?.status === 401 || error?.response?.status === 401) return;
          if (retryCount >= 3) return;
          revalidate({ retryCount });
        },
      }}
    >
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
