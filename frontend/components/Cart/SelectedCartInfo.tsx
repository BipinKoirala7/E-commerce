"use client";

import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

type SelectedCartInfoPropsT = {
  totalPrice: number;
};

function SelectedCartInfo({ totalPrice }: SelectedCartInfoPropsT) {
  const router = useRouter();
  return (
    <div className="flex flex-col gap-3 pt-2">
      <div className="flex items-center justify-between text-sm">
        <span className="text-muted-foreground">Subtotal</span>
        <span className="font-semibold">${totalPrice.toFixed(2)}</span>
      </div>

      <Button
        disabled={totalPrice <= 0}
        className="w-full"
        onClick={() => router.push("/order/create")}
      >
        Place order
      </Button>
    </div>
  );
}

export default SelectedCartInfo;
