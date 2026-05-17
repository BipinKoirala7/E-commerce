"use client";

import useSWR from "swr";
import { fetcher } from "@/lib/axios";

import { WishListResponse } from "@/types";
import WishlistSmallCard from "@/components/Wishlist/NavWishList/WishlistSmallCard";
import { ApiEndpoint } from "@/lib/ApiEndpoint";

function NavWishListUI() {
  const { isLoading, data, error } = useSWR<WishListResponse>(
    ApiEndpoint.GET_WISHLIST,
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

  console.log(data);
  return (
    <div className="flex flex-col gap-2">
      {data.data.map((item) => (
        <WishlistSmallCard key={item.id} item={item} />
      ))}
    </div>
  );
}

export default NavWishListUI;
