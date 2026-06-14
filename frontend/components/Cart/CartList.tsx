"use client";

import { ApiResponse, CartProductSummary } from "@/types";
import CartCard from "./CartCard";
import { fetcher } from "@/lib/axios";
import useSWR from "swr";
import SelectedCartInfo from "./SelectedCartInfo";
import { ApiEndpoint } from "@/lib/ApiEndpoint";
import { Skeleton } from "@/components/ui/skeleton";
import { Separator } from "@/components/ui/separator";
import { useMemo } from "react";
import { useCartSelectionStore } from "@/store/zustand";

function CartList() {
  const { isLoading, data, error } = useSWR<ApiResponse<CartProductSummary[]>>(
    ApiEndpoint.CART,
    fetcher,
  );
  const { checkedIds } = useCartSelectionStore();

  const selectedTotalPrice = useMemo(() => {
    if (!data?.data) return 0;
    return data.data
      .filter((item) => checkedIds.has(item.id))
      .reduce((sum, item) => sum + item.product.price * item.quantity, 0);
  }, [data, checkedIds]);

  if (isLoading)
    return (
      <div className="flex flex-col gap-3">
        {[...Array(3)].map((_, i) => (
          <Skeleton key={i} className="h-24 w-full rounded-md" />
        ))}
      </div>
    );

  if (error)
    return (
      <div className="min-h-60 flex items-center justify-center text-sm text-muted-foreground">
        Failed to load cart. Try again later.
      </div>
    );

  if (data == null)
    return (
      <div className="min-h-60 flex items-center justify-center text-sm text-muted-foreground">
        Something went wrong.
      </div>
    );

  if (data.data.length === 0)
    return (
      <div className="min-h-60 flex items-center justify-center text-sm text-muted-foreground">
        Your cart is empty.
      </div>
    );

  return (
    <div className="flex flex-col gap-2">
      {data.data.map((cartItem) => (
        <CartCard key={cartItem.id} item={cartItem} />
      ))}
      <Separator className="mt-4" />
      <SelectedCartInfo totalPrice={selectedTotalPrice} />
    </div>
  );
}

export default CartList;
