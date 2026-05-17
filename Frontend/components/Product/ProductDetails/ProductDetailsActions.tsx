"use client";

import IconButton from "@/components/ui/IconButton";
import { addToWishlist, removeFromWishlist } from "@/lib/api/wishlist";
import { ApiEndpoint } from "@/lib/ApiEndpoint";
import { fetcher } from "@/lib/axios";
import { WishListResponse } from "@/types";
import { useMemo } from "react";
import { BsPatchPlus } from "react-icons/bs";
import { FaHeart, FaRegHeart } from "react-icons/fa";
import useSWR from "swr";

type ProductDetailsActionsProps = {
  productId: string;
};

function ProductDetailsActions({ productId }: ProductDetailsActionsProps) {
  const { data } = useSWR<WishListResponse>(ApiEndpoint.GET_WISHLIST, fetcher);

  const wishlistedIds = useMemo(
    () => new Set(data?.data.map((item) => item.product.id)),
    [data],
  );

  const isWishlisted = wishlistedIds.has(productId);

  async function handleAddToWishlist() {
    await addToWishlist({ productId: productId });
  }

  async function handleRemoveFromWishlist() {
    await removeFromWishlist(productId);
  }

  return (
    <div className="flex gap-4 items-center">
      {isWishlisted ? (
        <div onClick={async () => await handleRemoveFromWishlist()}>
          <IconButton icon={<FaHeart className="w-12 h-12 text-red-500" />} />
        </div>
      ) : (
        <div onClick={async () => await handleAddToWishlist()}>
          <IconButton icon={<FaRegHeart className="w-12 h-12" />} />
        </div>
      )}
      <button className="bg-text flex gap-4 items-center rounded-4xl border border-text px-4 py-3 text-white cursor-pointer">
        <BsPatchPlus className="w-8 h-8" />
        <p>Add to Cart</p>
      </button>
    </div>
  );
}

export default ProductDetailsActions;
