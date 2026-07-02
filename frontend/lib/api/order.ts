import {
  CreateOrderResponse,
  DeleteOrderResponse,
  CreateOrder,
  UpdateOrderResponse,
  UpdateOrder,
} from "@/types";
import { ApiEndpoint } from "../ApiEndpoint";
import { api } from "@/lib/axios";
import toast from "react-hot-toast";
import { mutate } from "swr";

const orderUrl = ApiEndpoint.ORDER;

export async function createOrder(order: CreateOrder): Promise<boolean> {
  const response = await api.post<CreateOrderResponse>(orderUrl, order);

  if (response.data.success) {
    toast.success("Order Created successfully");
    mutate(orderUrl);
    return true;
  } else {
    toast.error("Failed to create order");
    return false;
  }
}

export async function updateOrder(
  orderId: string,
  updateOrder: UpdateOrder,
): Promise<boolean> {
  const response = await api.put<UpdateOrderResponse>(
    orderUrl + "/" + orderId,
    updateOrder,
  );

  if (response.data.success) {
    toast.success("Order updated successfully");
    mutate(orderUrl);
    return true;
  } else {
    toast.error("Failed to update order");
    return false;
  }
}

export async function deleteOrder(orderId: string): Promise<boolean> {
  const response = await api.post<DeleteOrderResponse>(orderUrl, orderId);

  if (response.data.success) {
    toast.success("Order deleted successfully");
    mutate(orderUrl);
    return true;
  } else {
    toast.error("Failed to delete order");
    return false;
  }
}
