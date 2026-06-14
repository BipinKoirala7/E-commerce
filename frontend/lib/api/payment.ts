import { CreatePaymentResponse } from "@/types";
import { ApiEndpoint } from "../ApiEndpoint";
import { api } from "../axios";
import { mutate } from "swr";
import toast from "react-hot-toast";

const paymentUrl = ApiEndpoint.PAYMENT;

export async function pay(orderId: string) {
  const response = await api.post<CreatePaymentResponse>(
    paymentUrl + "/" + orderId + "/pay",
    {
      paymentMethod: "STRIPE",
    },
  );

  if (response.data.success) {
    mutate(paymentUrl);
    window.location.href = response.data.data.sessionUrl;
  } else {
    toast.error("Failed to initiate Payment");
  }
}
