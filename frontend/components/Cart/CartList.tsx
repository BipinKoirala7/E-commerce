"use client";

import { ApiResponse, CartProductSummary } from "@/types";
import CartCard from "./CartCard";
import { fetcher } from "@/lib/axios";
import useSWR from "swr";
import SelectedCartInfo from "./SelectedCartInfo";
import { ApiEndpoint } from "@/lib/ApiEndpoint";
import { Skeleton } from "@/components/ui/skeleton";
import { Separator } from "@/components/ui/separator";
import { useState } from "react";

function CartList() {
  const { isLoading, data, error } = useSWR<ApiResponse<CartProductSummary[]>>(
    ApiEndpoint.CART,
    fetcher,
  );
  const [selectedTotalPrice, setSelectedTotalPrice] = useState(0);

  const subtract = (price: number) => {
    setSelectedTotalPrice((prev) => prev - price);
  };

  const add = (price: number) => {
    setSelectedTotalPrice((prev) => prev + price);
  };

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
        <CartCard
          key={cartItem.id}
          item={cartItem}
          add={add}
          subtract={subtract}
        />
      ))}
      <Separator className="mt-4" />
      <SelectedCartInfo totalPrice={selectedTotalPrice} />
    </div>
  );
}

export default CartList;
