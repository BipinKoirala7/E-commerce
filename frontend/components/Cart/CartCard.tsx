"use client";

import { useState } from "react";
import Image from "next/image";
import { Loader2, Minus, Plus, Trash2 } from "lucide-react";
import { CartProductSummary } from "@/types";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Badge } from "@/components/ui/badge";
import { addToCart, deleteFromCart, removeFromCart } from "@/lib/api/cart";
import Link from "next/link";
import { useCartSelectionStore } from "@/store/zustand";

type CartCardProps = {
  item: CartProductSummary;
};

type LoadingAction = "decrease" | "increase" | "delete" | null;

function CartCard({ item }: CartCardProps) {
  const { checkedIds, toggle } = useCartSelectionStore();
  const isChecked = checkedIds.has(item.id);
  const [loadingAction, setLoadingAction] = useState<LoadingAction>(null);

  const isLoading = loadingAction !== null;

  const handleDecrease = async () => {
    if (isLoading) return;
    setLoadingAction("decrease");
    try {
      await removeFromCart(item.product.id, item.quantity - 1);
    } finally {
      setLoadingAction(null);
    }
  };

  const handleIncrease = async () => {
    if (isLoading) return;
    setLoadingAction("increase");
    try {
      await addToCart({ productId: item.product.id });
    } finally {
      setLoadingAction(null);
    }
  };

  const handleDelete = async () => {
    if (isLoading) return;
    setLoadingAction("delete");
    try {
      await deleteFromCart(item.product.id);
    } finally {
      setLoadingAction(null);
    }
  };

  return (
    <div className="flex items-center gap-4 p-3 rounded-md hover:bg-muted/50 smooth-transition">
      <Checkbox
        id={`cart-item-${item.id}`}
        checked={isChecked}
        onCheckedChange={() => toggle(item.id)}
      />

      <div className="flex items-center gap-4 w-full overflow-hidden">
        <Image
          src={item.product.imageUrl}
          alt={item.product.name}
          loading="eager"
          width={400}
          height={400}
          className="w-24 aspect-4/3 rounded-md object-cover shrink-0"
        />

        <Link
          href={`/product/${item.product.id}`}
          className="flex flex-col gap-1 overflow-hidden flex-1 min-w-0"
        >
          <p className="text-sm font-medium truncate">{item.product.name}</p>
          <Badge variant="outline" className="w-fit text-xs font-normal">
            {item.product.brand}
          </Badge>
          <p className="text-sm text-muted-foreground">
            ${item.product.price.toFixed(2)}
          </p>
        </Link>

        <div className="shrink-0 flex items-center gap-1.5 px-2.5 py-1 rounded-md border text-sm text-muted-foreground">
          <span className="text-xs text-muted-foreground/60">Qty</span>
          <span className="font-medium text-foreground">{item.quantity}</span>
        </div>

        <Button
          variant="ghost"
          size="icon"
          className="shrink-0 text-muted-foreground hover:text-destructive hover:bg-destructive/10"
          onClick={handleDecrease}
          disabled={item.quantity <= 1 || isLoading}
        >
          {loadingAction === "decrease" ? (
            <Loader2 className="w-4 h-4 animate-spin" />
          ) : (
            <Minus className="w-4 h-4" />
          )}
          <span className="sr-only">Remove item</span>
        </Button>
        <Button
          size="icon"
          className="bg-transparent shrink-0 text-t hover:bg-green3/25"
          onClick={handleIncrease}
          disabled={isLoading}
        >
          {loadingAction === "increase" ? (
            <Loader2 className="w-4 h-4 animate-spin" />
          ) : (
            <Plus className="w-4 h-4" />
          )}
          <span className="sr-only">Remove item</span>
        </Button>
        <Button
          variant="ghost"
          size="icon"
          className="shrink-0 text-destructive hover:text-f hover:bg-destructive"
          onClick={handleDelete}
          disabled={isLoading}
        >
          {loadingAction === "delete" ? (
            <Loader2 className="w-4 h-4 animate-spin" />
          ) : (
            <Trash2 className="w-4 h-4" />
          )}
          <span className="sr-only">Remove item</span>
        </Button>
      </div>
    </div>
  );
}

export default CartCard;
