"use client";

import { useSelectedCartItemPriceInfoStore } from "@/store/zustand";
import Button from "../ui/Button";

function SelectedCartInfo() {
  const totalPrice = useSelectedCartItemPriceInfoStore((state) =>
    state.getTotalPrice(),
  );
  return (
    <div className="flex flex-col gap-2">
      <p className="text-lg font-medium">Total Price: ${totalPrice}</p>
      <Button
        name="Order"
        className="bg-secondary text-white rounded-sm px-4 py-2 hover:bg-text smooth-transition"
      />
    </div>
  );
}

export default SelectedCartInfo;
