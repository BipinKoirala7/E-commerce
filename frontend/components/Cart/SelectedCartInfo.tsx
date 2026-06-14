"use client";

import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";

type SelectedCartInfoPropsT = {
  totalPrice: number;
};

function SelectedCartInfo({ totalPrice }: SelectedCartInfoPropsT) {
  return (
    <div className="flex flex-col gap-3 pt-2">
      <div className="flex items-center justify-between text-sm">
        <span className="text-muted-foreground">Subtotal</span>
        <span className="font-semibold">${totalPrice.toFixed(2)}</span>
      </div>

      <Button className="w-full">Place order</Button>
    </div>
  );
}

export default SelectedCartInfo;
