import Image from "next/image";
import { useState } from "react";

import { CartProductSummary } from "@/types";
import IconButton from "@/components/ui/IconButton";
import { TiMinus } from "react-icons/ti";

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
        src={item.productImageUrl}
        alt={item.productName}
        width={60}
        height={60}
        className="w-16 aspect-4/3 rounded object-cover"
      />
      <div className="relative flex flex-col gap-1 overflow-hidden">
        <p className="text-md font-medium single-line">{item.productName}</p>
        <p className="text-sm text-muted-foreground">
          ${item.productPrice.toFixed(2)}
        </p>
      </div>
      <div
        className={`h-full flex gap-2 p-1 items-center smooth-transition ${showOptions ? "flex" : "hidden"}`}
      >
        <IconButton
          icon={<TiMinus className="w-5 h-5" />}
          className="hover:bg-secondary"
        />
      </div>
    </div>
  );
}

export default CartSmallCard;
