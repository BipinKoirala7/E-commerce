"use client";

import Image from "next/image";
import { useState } from "react";
import useSWR from "swr";
import { MapPin, Phone, Receipt, ShoppingBag } from "lucide-react";

import { ApiResponse, CartProductSummary, CreateOrderItem } from "@/types";
import { fetcher } from "@/lib/axios";
import { ApiEndpoint } from "@/lib/ApiEndpoint";
import { useCartSelectionStore } from "@/store/zustand";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
  CardFooter,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { createOrder } from "@/lib/api/order";
import { useRouter } from "next/navigation";

function CreateOrderForm() {
  const router = useRouter();
  const [billingAddress, setBillingAddress] = useState("");
  const [shippingAddress, setShippingAddress] = useState("");
  const [sameAsBilling, setSameAsBilling] = useState(false);
  const [phone, setPhone] = useState("");

  const checkedIds = useCartSelectionStore((s) => s.checkedIds);

  const { data, isLoading } = useSWR<ApiResponse<CartProductSummary[]>>(
    ApiEndpoint.CART,
    fetcher,
  );

  const selectedItems =
    data?.data.filter((item) => checkedIds.has(item.id)) ?? [];

  const totalPrice = selectedItems.reduce(
    (sum, item) => sum + item.product.price * item.quantity,
    0,
  );

  const handleSameAsBilling = (checked: boolean) => {
    setSameAsBilling(checked);
    if (checked) setShippingAddress(billingAddress);
  };

  const handleSubmit = async () => {
    const items: CreateOrderItem[] = selectedItems.map((item) => ({
      productId: item.product.id,
      quantity: item.quantity,
    }));
    const result = await createOrder({
      billingAddress,
      shippingAddress,
      phone,
      orderItems: items,
    });

    if (result) {
      router.push("/order");
    }
  };

  return (
    <div className="flex flex-col gap-5">
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-base flex items-center gap-2">
            <MapPin className="w-4 h-4 text-muted-foreground" />
            Delivery details
          </CardTitle>
          <CardDescription>Where should we send your order?</CardDescription>
        </CardHeader>

        <CardContent className="flex flex-col gap-4">
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="billing">Billing address</Label>
            <Input
              id="billing"
              placeholder="123 Main St, New York, NY 10001"
              value={billingAddress}
              onChange={(e) => {
                setBillingAddress(e.target.value);
                if (sameAsBilling) setShippingAddress(e.target.value);
              }}
            />
          </div>

          <div className="flex flex-col gap-1.5">
            <div className="flex items-center justify-between">
              <Label htmlFor="shipping">Shipping address</Label>
              <label className="flex items-center gap-1.5 text-xs text-muted-foreground cursor-pointer select-none">
                <input
                  type="checkbox"
                  className="accent-primary"
                  checked={sameAsBilling}
                  onChange={(e) => handleSameAsBilling(e.target.checked)}
                />
                Same as billing
              </label>
            </div>
            <Input
              id="shipping"
              placeholder="456 Elm Ave, Brooklyn, NY 11201"
              value={shippingAddress}
              disabled={sameAsBilling}
              onChange={(e) => setShippingAddress(e.target.value)}
            />
          </div>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="phone" className="flex items-center gap-1.5">
              <Phone className="w-3.5 h-3.5 text-muted-foreground" />
              Phone number
            </Label>
            <Input
              id="phone"
              type="tel"
              placeholder="+1-212-555-0198"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
            />
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-base flex items-center gap-2">
            <ShoppingBag className="w-4 h-4 text-muted-foreground" />
            Order summary
          </CardTitle>
          <CardDescription>
            {selectedItems.length === 0
              ? "No items selected — go back to your cart and check items to include."
              : `${selectedItems.length} item${selectedItems.length > 1 ? "s" : ""} selected`}
          </CardDescription>
        </CardHeader>

        <CardContent className="flex flex-col gap-3">
          {isLoading ? (
            <>
              <Skeleton className="h-16 w-full rounded-md" />
              <Skeleton className="h-16 w-full rounded-md" />
            </>
          ) : selectedItems.length === 0 ? (
            <div className="flex items-center justify-center min-h-24 text-sm text-muted-foreground">
              No items selected.
            </div>
          ) : (
            selectedItems.map((item) => (
              <div
                key={item.id}
                className="flex items-center gap-3 p-2 rounded-md bg-muted/40"
              >
                <Image
                  src={item.product.imageUrl}
                  alt={item.product.name}
                  width={200}
                  height={200}
                  className="w-14 aspect-square rounded-md object-cover shrink-0"
                />
                <div className="flex flex-col gap-0.5 flex-1 min-w-0 overflow-hidden">
                  <p className="text-sm font-medium truncate">
                    {item.product.name}
                  </p>
                  <Badge
                    variant="outline"
                    className="w-fit text-xs font-normal"
                  >
                    {item.product.brand}
                  </Badge>
                </div>
                <div className="flex flex-col items-end gap-0.5 shrink-0">
                  <p className="text-sm font-semibold">
                    ${(item.product.price * item.quantity).toFixed(2)}
                  </p>
                  <p className="text-xs text-muted-foreground">
                    Qty {item.quantity}
                  </p>
                </div>
              </div>
            ))
          )}
        </CardContent>

        {selectedItems.length > 0 && (
          <>
            <Separator />
            <CardFooter className="flex flex-col gap-3 pt-4">
              <div className="flex items-center justify-between w-full text-sm">
                <span className="text-muted-foreground flex items-center gap-1.5">
                  <Receipt className="w-3.5 h-3.5" />
                  Total
                </span>
                <span className="font-semibold text-base">
                  ${totalPrice.toFixed(2)}
                </span>
              </div>
              <Button
                className="w-full"
                disabled={
                  !billingAddress ||
                  !shippingAddress ||
                  !phone ||
                  selectedItems.length === 0
                }
                onClick={handleSubmit}
              >
                Place order
              </Button>
            </CardFooter>
          </>
        )}
      </Card>
    </div>
  );
}

export default CreateOrderForm;
