import {
  AddToCartResponse,
  AddCartItem,
  RemoveFromCartResponse,
  DeleteFromCartResponse,
} from "@/types";
import { ApiEndpoint } from "@/lib/ApiEndpoint";
import toast from "react-hot-toast";
import { api } from "../axios";
import { mutate } from "swr";

const cartUrl = ApiEndpoint.CART;

export async function addToCart(cartItem: AddCartItem): Promise<boolean> {
  const response = await api.post<AddToCartResponse>(
    cartUrl + "/" + cartItem.productId,
  );

  if (response.data.success) {
    await mutate(cartUrl);
    toast.success("Added to cart successfully");
    return true;
  } else {
    toast.error("Failed to add to cart");
    return false;
  }
}

export async function removeFromCart(
  productId: string,
  quantity: number,
): Promise<boolean> {
  const response = await api.patch<RemoveFromCartResponse>(
    cartUrl + "/" + productId,
    {
      quantity,
    },
  );

  if (response.data.success) {
    await mutate(cartUrl);
    toast.success("Removed from cart successfully");
    return true;
  } else {
    toast.error("Failed to remove from cart");
    return false;
  }
}

export async function deleteFromCart(productId: string): Promise<boolean> {
  const response = await api.delete<DeleteFromCartResponse>(
    cartUrl + "/" + productId,
  );

  if (response.data.success) {
    await mutate(cartUrl);
    toast.success("Deleted from cart successfully");
    return true;
  } else {
    toast.error("Failed to delete from cart");
    return false;
  }
}
