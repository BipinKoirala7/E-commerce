import { addToWishlist, removeFromWishlist } from "@/lib/api/wishlist";
import { ApiEndpoint } from "@/lib/ApiEndpoint";
import { fetcher } from "@/lib/axios";
import { WishListResponse } from "@/types";
import { useMemo } from "react";
import useSWR from "swr";

export function useWishlist() {
  const { data, mutate } = useSWR<WishListResponse>(
    ApiEndpoint.GET_WISHLIST,
    fetcher,
  );

  console.log("Wishlist data:", data);
  const wishlistedIds = useMemo(
    () => new Set(data?.data.map((item) => item.product.id) ?? []),
    [data],
  );

  const toggle = async (productId: string) => {
    const isWishlisted = wishlistedIds.has(productId);

    mutate();

    try {
      if (isWishlisted) addToWishlist({ productId });
      else removeFromWishlist(productId);

      mutate(); // revalidate after success
    } catch {
      mutate(); // rollback by revalidating on failure
    }
  };

  return {
    wishlistedIds,
    isWishlisted: (productId: string) => wishlistedIds.has(productId),
    toggle,
  };
}
