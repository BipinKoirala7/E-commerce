import {
  AddToCartResponse,
  AddCartItem,
  RemoveFromCartResponse,
} from "@/types";
import { ApiEndpoint } from "@/lib/ApiEndpoint";
import toast from "react-hot-toast";
import { api } from "../axios";
import { mutate } from "swr";

const cartUrl = ApiEndpoint.GET_CART;

export async function addToCart(cartItem: AddCartItem): Promise<boolean> {
  const response = await api.post<AddToCartResponse>(cartUrl, cartItem);

  if (response.data.success) {
    toast.success("Added to cart successfully");
    mutate(ApiEndpoint.GET_CART);
    return true;
  } else {
    toast.error("Failed to add to cart");
    return false;
  }
}

export async function removeFromCart(cartItemId: string): Promise<boolean> {
  const response = await api.delete<RemoveFromCartResponse>(
    cartUrl + "/" + cartItemId,
  );

  if (response.data.success) {
    toast.success("Removed from cart successfully");
    mutate(ApiEndpoint.GET_CART);
    return true;
  } else {
    toast.error("Failed to remove from cart");
    return false;
  }
}
