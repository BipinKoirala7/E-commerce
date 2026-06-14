"use client";

import { OrderListResponse } from "@/types";
import OrderCard from "@/components/Order/OrderCard";
import {
  Table,
  TableBody,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Skeleton } from "@/components/ui/skeleton";
import { fetcher } from "@/lib/axios";
import useSWR from "swr";

function OrderList() {
  const { isLoading, data, error } = useSWR<OrderListResponse>(
    process.env.NEXT_PUBLIC_BASE_ORDER_URL,
    fetcher,
  );

  if (isLoading)
    return (
      <div className="flex flex-col gap-3">
        {[...Array(3)].map((_, i) => (
          <Skeleton key={i} className="h-14 w-full rounded-md" />
        ))}
      </div>
    );

  if (error || data == null)
    return (
      <div className="min-h-60 flex items-center justify-center text-sm text-muted-foreground">
        {error
          ? "Failed to load orders. Try again later."
          : "Something went wrong."}
      </div>
    );

  if (data.data.length === 0)
    return (
      <div className="min-h-60 flex items-center justify-center text-sm text-muted-foreground">
        No orders yet.
      </div>
    );

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead className="w-30">Order</TableHead>
          <TableHead>Billing Address</TableHead>
          <TableHead className="w-27.5">Total</TableHead>
          <TableHead className="w-32.5">Status</TableHead>
          <TableHead className="w-28">Payment</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {data.data.map((order) => (
          <OrderCard key={order.id} item={order} />
        ))}
      </TableBody>
    </Table>
  );
}

export default OrderList;
