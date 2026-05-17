"use client";

import useSWR from "swr";
import { fetcher } from "@/lib/axios";

import { ApiResponseT, CartProductSummary } from "@/types";
import CartSmallCard from "./CartSmallCard";

function NavCartUI() {
  const { isLoading, data, error } = useSWR<ApiResponseT<CartProductSummary[]>>(
    process.env.NEXT_PUBLIC_BASE_CART_URL,
    fetcher,
  );

  // Handle loading, error, and empty states
  // Make sure the message are UI friendly and are in the middle of whole container.
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
        Error loading wishlist
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
        Your wishlist is empty
      </div>
    );
  }

  console.log("Rendering normal list");
  return (
    <div className="flex flex-col gap-2">
      {data.data.map((cartItem) => (
        <CartSmallCard key={cartItem.productId} item={cartItem} />
      ))}
    </div>
  );
}

export default NavCartUI;
