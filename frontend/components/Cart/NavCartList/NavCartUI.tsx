"use client";

import useSWR from "swr";
import { fetcher } from "@/lib/axios";

import { ApiResponse, CartProductSummary } from "@/types";
import CartSmallCard from "./CartSmallCard";
import { ApiEndpoint } from "@/lib/ApiEndpoint";

function NavCartUI() {
  const { isLoading, data, error } = useSWR<ApiResponse<CartProductSummary[]>>(
    ApiEndpoint.CART,
    fetcher,
  );

  if (isLoading) {
    console.log("It is loading");
    return (
      <div className="h-full text-1xl opacity-50 flex items-center justify-center">
        Loading...
      </div>
    );
  }
  if (error) {
    console.log("Error occurred");
    return (
      <div className="h-full text-1xl opacity-50 flex items-center justify-center">
        Error loading cart
      </div>
    );
  }
  if (data == null) {
    console.log("Data is null");
    return (
      <div className="h-full text-1xl opacity-50 flex items-center justify-center">
        Something went wrong
      </div>
    );
  }

  if (data.data.length === 0) {
    console.log("Wishlist is empty");
    return (
      <div className="h-full text-1xl opacity-50 flex items-center justify-center">
        Your cart is empty
      </div>
    );
  }

  console.log("Rendering normal list");
  return (
    <div className="flex flex-col gap-2">
      {data.data.map((cartItem) => (
        <CartSmallCard key={cartItem.product.id} item={cartItem} />
      ))}
    </div>
  );
}

export default NavCartUI;
