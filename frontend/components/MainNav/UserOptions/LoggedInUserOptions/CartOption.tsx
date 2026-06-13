"use client";

import { useState } from "react";
import { ShoppingCart } from "lucide-react";

import NavCartList from "@/components/Cart/NavCartList/NavCartList";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent } from "@/components/ui/dialog";

function CartOption() {
  const [open, setOpen] = useState(false);

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <Button
        size="icon"
        onClick={() => setOpen((prev) => !prev)}
        aria-expanded={open}
        aria-label="Toggle cart"
      >
        <ShoppingCart className="w-5 h-5" />
      </Button>

      <DialogContent
        showCloseButton={false}
        className="w-full min-w-100 max-w-100 min-h-30 h-full"
      >
        <NavCartList />
      </DialogContent>
    </Dialog>
  );
}

export default CartOption;
