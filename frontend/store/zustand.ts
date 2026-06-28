import { create } from "zustand";

// Cart Selection Store

type CartSelectionStore = {
  checkedIds: Set<string>;
  toggle: (id: string) => void;
  clear: () => void;
};

export const useCartSelectionStore = create<CartSelectionStore>((set) => ({
  checkedIds: new Set(),
  toggle: (id) =>
    set((state) => {
      const next = new Set(state.checkedIds);
      next.has(id) ? next.delete(id) : next.add(id);
      return { checkedIds: next };
    }),
  clear: () => set({ checkedIds: new Set() }),
}));

// Server Status Store

export type ServerStatus = "PENDING" | "CHECKING" | "UP" | "FAILED";

export type BaseServerStatusT = {
  status: ServerStatus;
  attempts: number;
};

export type ServerKey =
  | "configServer"
  | "eurekaServer"
  | "apiGateway"
  | "userService"
  | "productService"
  | "orderService";

export type AllServerStatusT = {
  servers: Record<ServerKey, BaseServerStatusT>;
  currentIndex: number;
  setStatus: (key: ServerKey, status: ServerStatus) => void;
  incrementAttempts: (key: ServerKey) => void;
  advance: () => void;
  reset: () => void;
};

const initialServers: Record<ServerKey, BaseServerStatusT> = {
  configServer: { status: "PENDING", attempts: 0 },
  eurekaServer: { status: "PENDING", attempts: 0 },
  apiGateway: { status: "PENDING", attempts: 0 },
  userService: { status: "PENDING", attempts: 0 },
  orderService: { status: "PENDING", attempts: 0 },
  productService: { status: "PENDING", attempts: 0 },
};

export const useServerStatusStore = create<AllServerStatusT>((set) => ({
  servers: initialServers,
  currentIndex: 0,
  setStatus: (key, status) =>
    set((state) => ({
      servers: {
        ...state.servers,
        [key]: { ...state.servers[key], status },
      },
    })),
  incrementAttempts: (key) =>
    set((state) => ({
      servers: {
        ...state.servers,
        [key]: {
          ...state.servers[key],
          attempts: state.servers[key].attempts + 1,
        },
      },
    })),
  advance: () => set((state) => ({ currentIndex: state.currentIndex + 1 })),
  reset: () => set({ servers: initialServers, currentIndex: 0 }),
}));
