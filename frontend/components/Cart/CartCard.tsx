"use client";

import Image from "next/image";
import { Minus } from "lucide-react";
import { CartProductSummary } from "@/types";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Badge } from "@/components/ui/badge";
import { removeFromCart } from "@/lib/api/cart";
import Link from "next/link";

type CartCardProps = {
  item: CartProductSummary;
  add: (price: number) => void;
  subtract: (price: number) => void;
};

function CartCard({ item, add, subtract }: CartCardProps) {
  return (
    <div className="flex items-center gap-4 p-3 rounded-md hover:bg-muted/50 smooth-transition">
      <Checkbox
        id={`cart-item-${item.id}`}
        onCheckedChange={(checked) => {
          if (checked) {
            add(item.product.price * item.quantity);
          } else {
            subtract(item.product.price * item.quantity);
          }
        }}
      />

      <div className="flex items-center gap-4 w-full overflow-hidden">
        <Image
          src={item.product.imageUrl}
          alt={item.product.name}
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
          onClick={() => {
            removeFromCart(item.product.id);
            subtract(item.product.price * item.quantity);
          }}
        >
          <Minus className="w-4 h-4" />
          <span className="sr-only">Remove item</span>
        </Button>
      </div>
    </div>
  );
}

export default CartCard;
