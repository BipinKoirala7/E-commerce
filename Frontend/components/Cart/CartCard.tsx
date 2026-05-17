"use client";

import Image from "next/image";
import { TiMinus } from "react-icons/ti";

import { CartProductSummary } from "@/types";
import IconButton from "@/components/ui/IconButton";
import { useSelectedCartItemPriceInfoStore } from "@/store/zustand";

type CartSmallCardProps = {
  item: CartProductSummary;
};

function CartCard({ item }: CartSmallCardProps) {
  const selectedCartItems = useSelectedCartItemPriceInfoStore();
  return (
    <div
      key={item.id}
      className="p-2 rounded-sm flex items-center space-x-4 overflow-hidden cursor-pointer hover:bg-primary smooth-transition"
    >
      <input
        type="checkbox"
        onChange={() =>
          selectedCartItems.changeSelectedCartItem(
            item.id,
            item.product.price,
            item.quantity,
          )
        }
        className="w-5 aspect-square border border-primary cursor-pointer"
      />
      <div className="w-full flex items-center gap-4">
        <Image
          src={item.product.image.imageUrl}
          alt={item.product.name}
          width={400}
          height={400}
          className="w-32 aspect-4/3 rounded object-cover"
        />
        <div className="w-full flex justify-between">
          <div className="relative flex flex-col gap-1 overflow-hidden">
            <p className="text-md font-medium single-line">
              {item.product.name}
            </p>
            <p className="text-sm text-muted-foreground">
              ${item.product.price.toFixed(2)}
            </p>
          </div>
          <div
            className={`h-full flex gap-2 p-1 items-center smooth-transition`}
          >
            <IconButton
              icon={<TiMinus className="w-5 h-5" />}
              className="hover:bg-secondary"
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default CartCard;
