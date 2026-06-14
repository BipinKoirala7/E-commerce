// store/useCartSelectionStore.ts
import { create } from "zustand";

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
