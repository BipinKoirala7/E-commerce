"use client";

import Image from "next/image";
import useSWR from "swr";
import {
  MapPin,
  Phone,
  Mail,
  Package,
  CalendarDays,
  RefreshCw,
  CreditCard,
  CheckCircle2,
  Clock,
  XCircle,
  Hash,
} from "lucide-react";

import { ApiResponse, OrderDetails, OrderStatus, PaymentStatus } from "@/types";
import { fetcher } from "@/lib/axios";
import { ApiEndpoint } from "@/lib/ApiEndpoint";

import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
  CardFooter,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { pay } from "@/lib/api/payment";

type Props = {
  orderNumber: string;
};

const statusVariant: Record<
  OrderStatus,
  "default" | "secondary" | "destructive" | "outline"
> = {
  [OrderStatus.PENDING]: "outline",
  [OrderStatus.CONFIRMED]: "secondary",
  [OrderStatus.PROCESSING]: "secondary",
  [OrderStatus.DELIVERED]: "default",
  [OrderStatus.CANCELLED]: "destructive",
  [OrderStatus.RETURNED]: "destructive",
};

function InfoRow({
  icon,
  label,
  value,
}: {
  icon: React.ReactNode;
  label: string;
  value: string;
}) {
  return (
    <div className="flex items-start gap-3">
      <span className="mt-0.5 text-muted-foreground shrink-0">{icon}</span>
      <div className="flex flex-col gap-0.5 min-w-0">
        <p className="text-xs text-muted-foreground">{label}</p>
        <p className="text-sm wrap-break-words">{value}</p>
      </div>
    </div>
  );
}

function OrderDetail({ orderNumber }: Props) {
  const { data, isLoading, error } = useSWR<ApiResponse<OrderDetails>>(
    `${ApiEndpoint.ORDER}/${orderNumber}`,
    fetcher,
  );

  if (isLoading)
    return (
      <div className="flex flex-col gap-4">
        <Skeleton className="h-40 w-full rounded-xl" />
        <Skeleton className="h-56 w-full rounded-xl" />
        <Skeleton className="h-24 w-full rounded-xl" />
        <Skeleton className="h-24 w-full rounded-xl" />
      </div>
    );

  if (error || data == null)
    return (
      <div className="min-h-60 flex items-center justify-center text-sm text-muted-foreground">
        {error
          ? "Failed to load order. Try again later."
          : "Something went wrong."}
      </div>
    );

  const order = data.data;
  const payment = order.payment;
  const isPaid = payment?.paymentStatus == PaymentStatus.COMPLETED;
  const isFailed = payment?.paymentStatus == PaymentStatus.FAILED;

  console.log(payment);
  console.log(isPaid);

  const formattedDate = (iso: string) =>
    new Date(iso).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });

  return (
    <div className="flex flex-col gap-4">
      {/* ── Status header ───────────────────────────────── */}
      <Card>
        <CardHeader className="pb-3">
          <div className="flex items-center justify-between gap-2 flex-wrap">
            <div>
              <CardTitle className="text-base font-mono">
                {order.orderNumber}
              </CardTitle>
              <CardDescription className="mt-0.5">
                {order.orderItems.length} item
                {order.orderItems.length !== 1 ? "s" : ""}
                {" · "}
                <span className="font-medium text-foreground">
                  ${order.totalPrice.toFixed(2)}
                </span>
              </CardDescription>
            </div>
            <Badge variant={statusVariant[order.orderStatus]} className="h-fit">
              {order.orderStatus}
            </Badge>
          </div>
        </CardHeader>

        <Separator />

        <CardContent className="pt-4 grid grid-cols-1 sm:grid-cols-2 gap-4">
          <InfoRow
            icon={<CalendarDays className="w-4 h-4" />}
            label="Placed on"
            value={formattedDate(order.createdAt)}
          />
          <InfoRow
            icon={<RefreshCw className="w-4 h-4" />}
            label="Last updated"
            value={formattedDate(order.updatedAt)}
          />
          <InfoRow
            icon={<Mail className="w-4 h-4" />}
            label="Email"
            value={order.email}
          />
          <InfoRow
            icon={<Phone className="w-4 h-4" />}
            label="Phone"
            value={order.phone}
          />
          <InfoRow
            icon={<MapPin className="w-4 h-4" />}
            label="Billing address"
            value={order.billingAddress}
          />
          <InfoRow
            icon={<MapPin className="w-4 h-4" />}
            label="Shipping address"
            value={order.shippingAddress}
          />
        </CardContent>
      </Card>

      {/* ── Order items ─────────────────────────────────── */}
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-base flex items-center gap-2">
            <Package className="w-4 h-4 text-muted-foreground" />
            Items
          </CardTitle>
        </CardHeader>

        <CardContent className="flex flex-col gap-3">
          {order.orderItems.map((orderItem, idx) => (
            <div key={orderItem.id}>
              <div className="flex items-center gap-3">
                <Image
                  src={orderItem.product.imageUrl}
                  alt={orderItem.product.name}
                  width={200}
                  height={200}
                  className="w-16 aspect-square rounded-md object-cover shrink-0"
                />
                <div className="flex items-center justify-between w-full gap-2 overflow-hidden">
                  <div className="flex flex-col gap-0.5 overflow-hidden">
                    <p className="text-sm font-medium truncate">
                      {orderItem.product.name}
                    </p>
                    <Badge
                      variant="outline"
                      className="w-fit text-xs font-normal"
                    >
                      {orderItem.product.brand}
                    </Badge>
                  </div>
                  <div className="flex flex-col items-end gap-0.5 shrink-0">
                    <p className="text-sm font-semibold">
                      $
                      {(orderItem.product.price * orderItem.quantity).toFixed(
                        2,
                      )}
                    </p>
                    <p className="text-xs text-muted-foreground">
                      Qty {orderItem.quantity} · $
                      {orderItem.product.price.toFixed(2)} each
                    </p>
                  </div>
                </div>
              </div>
              {idx < order.orderItems.length - 1 && (
                <Separator className="mt-3" />
              )}
            </div>
          ))}
        </CardContent>

        <Separator />

        <CardFooter className="pt-4 flex items-center justify-between">
          <p className="text-sm text-muted-foreground">Order total</p>
          <p className="text-lg font-semibold">
            ${order.totalPrice.toFixed(2)}
          </p>
        </CardFooter>
      </Card>

      {/* ── Payment ─────────────────────────────────────── */}
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-base flex items-center gap-2">
            <CreditCard className="w-4 h-4 text-muted-foreground" />
            Payment
          </CardTitle>
        </CardHeader>

        <Separator />

        <CardContent className="pt-4">
          {isPaid ? (
            <div className="flex flex-col gap-4">
              <div className="flex items-center gap-2 text-sm text-green-600 dark:text-green-400 bg-green-50 dark:bg-green-950/40 px-3 py-2 rounded-md">
                <CheckCircle2 className="w-4 h-4 shrink-0" />
                <span className="font-medium">Payment confirmed</span>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="flex items-start gap-3">
                  <span className="mt-0.5 text-muted-foreground shrink-0">
                    <CreditCard className="w-4 h-4" />
                  </span>
                  <div className="flex flex-col gap-0.5">
                    <p className="text-xs text-muted-foreground">
                      Payment method
                    </p>
                    <Badge className="w-fit bg-[#635BFF] hover:bg-[#635BFF] text-white text-xs font-medium">
                      Stripe
                    </Badge>
                  </div>
                </div>

                <InfoRow
                  icon={<Hash className="w-4 h-4" />}
                  label="Payment reference"
                  value={payment!.paymentNumber}
                />
                <InfoRow
                  icon={<CalendarDays className="w-4 h-4" />}
                  label="Paid on"
                  value={formattedDate(payment!.updatedAt)}
                />
                <InfoRow
                  icon={<CreditCard className="w-4 h-4" />}
                  label="Amount paid"
                  value={`$${payment!.totalAmount.toFixed(2)}`}
                />
              </div>
            </div>
          ) : isFailed ? (
            <div className="flex flex-col gap-4">
              <div className="flex items-center gap-2 text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-950/40 px-3 py-2 rounded-md">
                <XCircle className="w-4 h-4 shrink-0" />
                <span>Payment failed. Please try again.</span>
              </div>
              <div className="flex items-center justify-between">
                <div className="flex flex-col gap-0.5">
                  <p className="text-sm text-muted-foreground">Amount due</p>
                  <p className="text-lg font-semibold">
                    ${(payment?.totalAmount ?? order.totalPrice).toFixed(2)}
                  </p>
                </div>
                <Button
                  variant="destructive"
                  onClick={() => {
                    pay(order.id);
                  }}
                >
                  Retry payment
                </Button>
              </div>
            </div>
          ) : (
            // payment is null — not yet initiated
            <div className="flex flex-col gap-4">
              <div className="flex items-center gap-2 text-sm text-yellow-700 dark:text-yellow-400 bg-yellow-50 dark:bg-yellow-950/40 px-3 py-2 rounded-md">
                <Clock className="w-4 h-4 shrink-0" />
                <span>Payment not yet completed for this order.</span>
              </div>
              <div className="flex items-center justify-between">
                <div className="flex flex-col gap-0.5">
                  <p className="text-sm text-muted-foreground">Amount due</p>
                  <p className="text-lg font-semibold">
                    ${order.totalPrice.toFixed(2)}
                  </p>
                </div>
                <Button onClick={() => pay(order.id)}>Complete payment</Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

export default OrderDetail;
