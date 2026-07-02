import Image from "next/image";
import { useState } from "react";

import { CartProductSummary } from "@/types";
import { addToCart, deleteFromCart, removeFromCart } from "@/lib/api/cart";
import { Button } from "@/components/ui/button";
import { Loader2, Minus, Plus, Trash2 } from "lucide-react";

type CartSmallCardProps = {
  item: CartProductSummary;
};

type LoadingAction = "decrease" | "increase" | "delete" | null;

function CartSmallCard({ item }: CartSmallCardProps) {
  const [showOptions, setShowOptions] = useState(false);
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
    <div
      key={item.id}
      onMouseEnter={() => setShowOptions(true)}
      onMouseLeave={() => setShowOptions(false)}
      className="p-2 rounded-sm flex items-center space-x-4 overflow-hidden cursor-pointer hover:bg-f smooth-transition"
    >
      <Image
        src={item.product.imageUrl}
        alt={item.product.name}
        width={60}
        height={60}
        className="w-16 aspect-4/3 rounded object-cover"
      />
      <div className="w-full flex items-center justify-between gap-8">
        <div className="relative flex flex-col gap-1 overflow-hidden">
          <p className="text-md font-medium single-line">
            {item.product.brand}
          </p>
          <p className={`text-sm text-t ${showOptions ? "text-p" : ""}`}>
            ${item.product.price.toFixed(2)}
          </p>
        </div>
        <div className="flex items-center gap-2">
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
            variant="ghost"
            size="icon"
            className="shrink-0 text-muted-foreground hover:text-green3 hover:bg-green3/10"
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
    </div>
  );
}

export default CartSmallCard;
