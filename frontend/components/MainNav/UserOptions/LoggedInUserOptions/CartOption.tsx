"use client";

import { useState } from "react";
import { ShoppingCart } from "lucide-react";

import NavCartList from "@/components/Cart/NavCartList/NavCartList";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";

function CartOption() {
  const [show, setShow] = useState(false);

  return (
    <Popover open={show} onOpenChange={setShow}>
      <PopoverTrigger onClick={() => setShow(!show)}>
        <ShoppingCart className="w-5 h-5" />
      </PopoverTrigger>
      <PopoverContent
        className="w-full min-w-60 max-w-120"
        align="start"
        side="left"
      >
        <NavCartList close={() => setShow((prev) => !prev)} />
      </PopoverContent>
    </Popover>
  );
}

export default CartOption;
