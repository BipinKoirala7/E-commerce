"use client";

import { WishListItem } from "@/types";
import IconButton from "@/components/ui/IconButton";

import Image from "next/image";
import { useState } from "react";
import { TiMinus } from "react-icons/ti";
import { BsPatchPlus } from "react-icons/bs";
import { removeFromWishlist } from "@/lib/api/wishlist";

type WishlistSmallCardProps = {
  item: WishListItem;
};

function WishlistSmallCard({ item }: WishlistSmallCardProps) {
  const [showOptions, setShowOptions] = useState(false);
  return (
    <div
      key={item.id}
      onMouseEnter={() => setShowOptions(true)}
      onMouseLeave={() => setShowOptions(false)}
      className="relative max-w-full p-2 rounded-sm flex items-center justify-between space-x-4 cursor-pointer box-border hover:bg-primary"
    >
      <div className="flex items-center gap-4">
        <div className="p-2 flex items-center justify-center bg-background">
          <Image
            src={item.product.image.imageUrl}
            alt={item.product.name}
            width={60}
            height={60}
            className="w-16 aspect-4/3 rounded object-contain"
          />
        </div>
        <div className="relative flex flex-col gap-1 overflow-hidden">
          <p className="text-lg font-medium single-line">{item.product.name}</p>
          <p className="text-sm text-muted-foreground">
            ${item.product.price.toFixed(2)}
          </p>
        </div>
      </div>
      <div
        className={`absolute right-2 top-0 bg-primary h-full gap-2 px-3 items-center ${showOptions ? "flex" : "hidden"}`}
      >
        <IconButton
          icon={<BsPatchPlus className="w-5 h-5" />}
          className="hover:bg-secondary"
        />
        <IconButton
          icon={<TiMinus className="w-5 h-5" />}
          className="hover:bg-secondary"
          onClick={() => {
            removeFromWishlist(item.id);
          }}
        />
      </div>
    </div>
  );
}

export default WishlistSmallCard;
