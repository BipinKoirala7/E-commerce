import { OrderList, OrderStatus } from "@/types";
import Link from "next/link";
import { TableCell, TableRow } from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Button } from "../ui/button";
import { CheckCircle2, CircleX, X } from "lucide-react";
import { pay } from "@/lib/api/payment";

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
  const isPending = item.orderStatus === OrderStatus.PENDING;
  const isCancelled = item.orderStatus === OrderStatus.CANCELLED;
  const isConfirmed = item.orderStatus === OrderStatus.CONFIRMED;

  console.log({
    orderNumber: item.orderNumber,
    status: item.orderStatus,
    isPending: item.orderStatus === OrderStatus.PENDING,
  });

  function handlePayNowClick() {
    pay(item.id);
  }

  return (
    <TableRow className="cursor-pointer hover:bg-muted/50 smooth-transition">
      <TableCell>
        <Link
          href={`/order/${item.orderNumber}`}
          className="block w-full h-full"
        >
          <span className="font-mono text-xs font-medium">
            {item.orderNumber}
          </span>
        </Link>
      </TableCell>
      <TableCell>
        <Link
          href={`/order/${item.orderNumber}`}
          className="block w-full h-full"
        >
          <span className="text-sm text-muted-foreground truncate max-w-xs block">
            {item.billingAddress}
          </span>
        </Link>
      </TableCell>
      <TableCell>
        <Link
          href={`/order/${item.orderNumber}`}
          className="block w-full h-full"
        >
          <span className="text-sm font-medium">
            ${item.totalPrice.toFixed(2)}
          </span>
        </Link>
      </TableCell>
      <TableCell>
        <Link
          href={`/order/${item.orderNumber}`}
          className="block w-full h-full"
        >
          <Badge variant={statusVariant[item.orderStatus]}>
            {item.orderStatus}
          </Badge>
        </Link>
      </TableCell>
      <TableCell>
        {isPending ? (
          <Button
            size="sm"
            className="text-xs text-f h-7 bg-green3 cursor-pointer"
            onClick={() => handlePayNowClick()}
          >
            Pay now
          </Button>
        ) : isConfirmed ? (
          <CheckCircle2 className="text-green-500" />
        ) : isCancelled ? (
          <CircleX className="text-xs text-red-500">Cancelled</CircleX>
        ) : (
          <span className="text-xs text-muted-foreground">N/A</span>
        )}
      </TableCell>
    </TableRow>
  );
}

export default OrderCard;
