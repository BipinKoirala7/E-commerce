"use client";

import { ApiResponse, CartProductSummary } from "@/types";
import CartCard from "./CartCard";
import { fetcher } from "@/lib/axios";
import useSWR from "swr";
import SelectedCartInfo from "./SelectedCartInfo";
import { ApiEndpoint } from "@/lib/ApiEndpoint";

function CartList() {
  const { isLoading, data, error } = useSWR<ApiResponse<CartProductSummary[]>>(
    ApiEndpoint.GET_CART,
    fetcher,
  );

  // Handle loading, error, and empty states
  if (isLoading)
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Loading...
      </div>
    );
  if (error)
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Error loading Cart Items
      </div>
    );
  if (data == null)
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Something went wrong
      </div>
    );

  if (data.data.length === 0) {
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Your Cart is empty
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-2">
      {data.data.map((cartItem) => (
        <div key={cartItem.id} className="flex flex-col gap-2">
          <CartCard item={cartItem} />
        </div>
      ))}
      <div>
        <hr className="border-t-2 border-primary mt-3" />
        <SelectedCartInfo />
      </div>
    </div>
  );
}

export default CartList;
