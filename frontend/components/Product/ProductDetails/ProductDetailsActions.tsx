"use client";

import { addToCart } from "@/lib/api/cart";
import { BadgePlus } from "lucide-react";

type ProductDetailsActionsProps = {
  productId: string;
};

function ProductDetailsActions({ productId }: ProductDetailsActionsProps) {
  async function handleAddToCart() {
    await addToCart({ productId });
  }

  return (
    <div className="flex gap-4 items-center">
      <button
        className="bg-green3 flex gap-4 items-center rounded-4xl px-4 py-3 text-white cursor-pointer"
        onClick={handleAddToCart}
      >
        <BadgePlus className="w-8 h-8" />
        <p>Add to Cart</p>
      </button>
    </div>
  );
}

export default ProductDetailsActions;
