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
import { fetcher } from "@/lib/axios";
import useSWR from "swr";

function OrderList() {
  // const orders: Order[] = [
  //   {
  //     id: "ord_001",
  //     billingAddress: "123 Main St, New York, NY 10001",
  //     shippingAddress: "456 Elm Ave, Brooklyn, NY 11201",
  //     email: "jane.doe@example.com",
  //     phone: "+1-212-555-0198",
  //     orderItems: [
  //       { id: "item_001", productId: "prod_abc123", quantity: 2 },
  //       { id: "item_002", productId: "prod_xyz789", quantity: 1 },
  //     ],
  //     totalPrice: 149.97,
  //     orderStatus: OrderStatus.DELIVERED,
  //     createdAt: new Date("2026-02-28T10:23:00.000Z"),
  //     updatedAt: new Date("2026-02-28T10:45:00.000Z"),
  //   },
  //   {
  //     id: "ord_002",
  //     billingAddress: "789 Oak Blvd, Austin, TX 73301",
  //     shippingAddress: "789 Oak Blvd, Austin, TX 73301",
  //     email: "john.smith@example.com",
  //     phone: "+1-512-555-0342",
  //     orderItems: [{ id: "item_003", productId: "prod_def456", quantity: 3 }],
  //     totalPrice: 89.99,
  //     orderStatus: OrderStatus.PENDING,
  //     createdAt: new Date("2026-03-01T14:30:00.000Z"),
  //     updatedAt: new Date("2026-03-01T14:45:00.000Z"),
  //   },
  // ];

  const { isLoading, data, error } = useSWR<OrderListResponse>(
    process.env.NEXT_PUBLIC_BASE_ORDER_URL,
    fetcher,
  );

  if (isLoading) return <div className="flex flex-col gap-2">Loading...</div>;

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
          <TableHead className="w-30">Order ID</TableHead>
          <TableHead>Billing Address</TableHead>
          <TableHead className="w-27.5">Total</TableHead>
          <TableHead className="w-32.5">Status</TableHead>
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
