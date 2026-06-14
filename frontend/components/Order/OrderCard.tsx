import { OrderList, OrderStatus } from "@/types";
import Link from "next/link";
import { TableCell, TableRow } from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";

type OrderCardProps = {
  item: OrderList;
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

function OrderCard({ item }: OrderCardProps) {
  return (
    <TableRow className="cursor-pointer hover:bg-muted/50 smooth-transition">
      <TableCell>
        <Link href={`/orders/${item.id}`} className="block w-full h-full">
          <span className="font-mono text-xs font-medium">
            {item.orderNumber}
          </span>
        </Link>
      </TableCell>
      <TableCell>
        <Link href={`/orders/${item.id}`} className="block w-full h-full">
          <span className="text-sm text-muted-foreground truncate max-w-xs block">
            {item.billingAddress}
          </span>
        </Link>
      </TableCell>
      <TableCell>
        <Link href={`/orders/${item.id}`} className="block w-full h-full">
          <span className="text-sm font-medium">
            ${item.totalPrice.toFixed(2)}
          </span>
        </Link>
      </TableCell>
      <TableCell>
        <Link href={`/orders/${item.id}`} className="block w-full h-full">
          <Badge variant={statusVariant[item.orderStatus]}>
            {item.orderStatus}
          </Badge>
        </Link>
      </TableCell>
    </TableRow>
  );
}

export default OrderCard;
