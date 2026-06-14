import Image from "next/image";
import { useState } from "react";

import { CartProductSummary } from "@/types";
import { TiMinus } from "react-icons/ti";
import { IconButton } from "@/components/ui/IconButton";
import { removeFromCart } from "@/lib/api/cart";

type CartSmallCardProps = {
  item: CartProductSummary;
};

function CartSmallCard({ item }: CartSmallCardProps) {
  const [showOptions, setShowOptions] = useState(false);
  return (
    <div
      key={item.id}
      onMouseEnter={() => setShowOptions(true)}
      onMouseLeave={() => setShowOptions(false)}
      className="p-2 rounded-sm flex items-center space-x-4 overflow-hidden cursor-pointer hover:bg-primary smooth-transition"
    >
      <Image
        src={item.product.imageUrl}
        alt={item.product.name}
        width={60}
        height={60}
        className="w-16 aspect-4/3 rounded object-cover"
      />
      <div className="w-full flex items-center justify-between">
        <div className="relative flex flex-col gap-1 overflow-hidden">
          <p className="text-md font-medium single-line">
            {item.product.brand}
          </p>
          <p className={`text-sm text-t ${showOptions ? "text-p" : ""}`}>
            ${item.product.price.toFixed(2)}
          </p>
        </div>
        <div
          className={`h-full flex gap-2 p-1 items-center smooth-transition ${showOptions ? "opacity-100" : "opacity-0"}`}
        >
          <IconButton
            icon={<TiMinus className="w-5 h-5" />}
            className="hover:bg-secondary"
            onClick={() => removeFromCart(item.product.id)}
          />
        </div>
      </div>
    </div>
  );
}

export default CartSmallCard;
