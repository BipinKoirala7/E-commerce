"use client";

import { Order, OrderStatus } from "@/types";
import OrderCard from "@/components/Order/OrderCard";

function OrderList() {
  const orders: Order[] = [
    {
      id: "ord_001",
      billingAddress: "123 Main St, New York, NY 10001",
      shippingAddress: "456 Elm Ave, Brooklyn, NY 11201",
      email: "jane.doe@example.com",
      phone: "+1-212-555-0198",
      orderItems: [
        { id: "item_001", productId: "prod_abc123", quantity: 2 },
        { id: "item_002", productId: "prod_xyz789", quantity: 1 },
      ],
      totalPrice: 149.97,
      orderStatus: OrderStatus.DELIVERED,
      createdAt: new Date("2026-02-28T10:23:00.000Z"),
      updatedAt: new Date("2026-02-28T10:45:00.000Z"),
    },
    {
      id: "ord_002",
      billingAddress: "789 Oak Blvd, Austin, TX 73301",
      shippingAddress: "789 Oak Blvd, Austin, TX 73301",
      email: "john.smith@example.com",
      phone: "+1-512-555-0342",
      orderItems: [{ id: "item_003", productId: "prod_def456", quantity: 3 }],
      totalPrice: 89.99,
      orderStatus: OrderStatus.PENDING,
      createdAt: new Date("2026-03-01T14:30:00.000Z"),
      updatedAt: new Date("2026-03-01T14:45:00.000Z"),
    },
  ];

  // const { isLoading, data, error } = useSWR<ApiResponseT<Order[]>>(
  //   process.env.NEXT_PUBLIC_BASE_CART_URL,
  //   fetcher,
  // );

  // // Handle loading, error, and empty states
  // // Make sure the message are UI friendly and are in the middle of whole container.
  // if (isLoading)
  //   return (
  //     <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
  //       Loading...
  //     </div>
  //   );
  // if (error)
  //   return (
  //     <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
  //       Error loading orders
  //     </div>
  //   );
  // if (data == null)
  //   return (
  //     <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
  //       Something went wrong
  //     </div>
  //   );

  // if (data.data.length === 0) {
  //   return (
  //     <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
  //       ou have no orders
  //     </div>
  //   );
  // }

  return (
    <div className="flex flex-col gap-2">
      <div className="grid grid-cols-[1fr_2fr_1fr_1fr] gap-2 p-2">
        <p>Order Id</p>
        <p>Billing Address</p>
        <p>Total Price</p>
        <p>Order Status</p>
      </div>
      {orders.map((order) => (
        <div key={order.id} className="flex flex-col gap-2">
          <OrderCard item={order} />
        </div>
      ))}
    </div>
  );
}

export default OrderList;
