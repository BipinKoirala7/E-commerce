import { Order } from "@/types";
import Link from "next/link";

type OrderCardProps = {
  item: Order;
};

function OrderCard({ item }: OrderCardProps) {
  return (
    <Link
      href={`/orders/${item.id}`}
      key={item.id}
      className="p-2 rounded-sm grid grid-cols-[1fr_2fr_1fr_1fr] gap-2 items-center overflow-hidden cursor-pointer hover:bg-primary smooth-transition"
    >
      <p className="text-md font-medium single-line">{item.id.toUpperCase()}</p>
      <p className="text-sm text-muted-foreground">{item.billingAddress}</p>
      <p className="text-sm text-muted-foreground">
        ${item.totalPrice.toFixed(2)}
      </p>
      <p className="text-sm text-muted-foreground">{item.orderStatus}</p>
    </Link>
  );
}

export default OrderCard;
