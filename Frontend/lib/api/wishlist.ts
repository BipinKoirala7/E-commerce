import {
  AddToWishlist,
  AddToWishlistResponse,
  RemoveFromWishlistResponse,
} from "@/types";
import { api } from "../axios";
import toast from "react-hot-toast";
import { ApiEndpoint } from "@/lib/ApiEndpoint";
import { mutate } from "swr";

const wishlistUrl = ApiEndpoint.GET_WISHLIST;

export async function addToWishlist(wishlist: AddToWishlist): Promise<boolean> {
  const response = await api.post<AddToWishlistResponse>(wishlistUrl, wishlist);

  if (response.data.success) {
    toast.success("Added to wishlist successfully");
    mutate(ApiEndpoint.GET_WISHLIST);
    return true;
  } else {
    toast.error("Failed to add to wishlist");
    return false;
  }
}

export async function removeFromWishlist(wishlistId: string): Promise<boolean> {
  const response = await api.delete<RemoveFromWishlistResponse>(
    wishlistUrl + "/" + wishlistId,
  );

  if (response.data.success) {
    toast.success("Removed from wishlist successfully");
    mutate(ApiEndpoint.GET_WISHLIST);
    return true;
  } else {
    toast.error("Failed to remove from wishlist");
    return false;
  }
}
