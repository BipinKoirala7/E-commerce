import { Order, OrderStatus } from "@/types";

type Props = {
  params: {
    orderId: string;
  };
};

function page({ params }: Props) {
  const order: Order = {
    id: "ORD_001",
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
  };

  console.log(params);

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
    <div className="flex flex-col items-center justify-center">
      <div className="w-1/2 bg-foreground flex flex-col gap-4 p-4 rounded-sm">
        <p className="text-2xl header-font">Order Info</p>
        <div className="flex flex-col gap-2">
          <div className="flex flex-col gap-1">
            <p className="text-[1.2rem] ">Order Id: {order.id}</p>
            <p className="text-sm">Order Details</p>
          </div>
          <div className="flex flex-col gap-1 border-y-2 border-primary">
            <p className="text-1xl ">Items</p>
            <div className="flex flex-col gap-1">
              {order.orderItems.map((item) => (
                <p key={item.id}>{item.productId}</p>
              ))}
            </div>
          </div>
          <p className="text-1xl ">
            Created At: {order.createdAt.toUTCString()}
          </p>
          <p className="text-1xl ">Status: {order.orderStatus}</p>
          <hr className="border-t-2 border-primary" />
          <p className="text-1xl ">Customer Name: Bipin Koirala</p>
          <p className="text-1xl ">Email: {order.email}</p>
          <p className="text-1xl ">Phone: {order.phone}</p>
          <p className="text-1xl ">Shipping Address: {order.shippingAddress}</p>
          <hr className="border-t-2 border-primary" />

          <p className="text-1xl ">Payment</p>
          <p className="text-1xl ">Payment Method: Payment Method</p>
          <p className="text-1xl ">Billing Address: {order.billingAddress}</p>
          <p className="text-1xl ">Total Price: {order.totalPrice}</p>
        </div>
      </div>
    </div>
  );
}

export default page;
