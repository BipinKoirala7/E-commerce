"use client";

import { BadgePlus } from "lucide-react";

type ProductDetailsActionsProps = {
  productId: string;
};

function ProductDetailsActions({ productId }: ProductDetailsActionsProps) {
  return (
    <div className="flex gap-4 items-center">
      <button className="bg-text flex gap-4 items-center rounded-4xl border border-text px-4 py-3 text-white cursor-pointer">
        <BadgePlus className="w-8 h-8" />
        <p>Add to Cart</p>
      </button>
    </div>
  );
}

export default ProductDetailsActions;
