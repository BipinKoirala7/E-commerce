"use client";

import { useState } from "react";
import { CirclePlus, Loader2 } from "lucide-react";
import { MotionValue, useSpring, useTransform } from "framer-motion";
import * as motion from "motion/react-client";

import { Button } from "@/components/ui/button";
import { ProductSummary } from "@/types";
import { addToCart } from "@/lib/api/cart";

type ProductInfoProps = {
  show: MotionValue<number>;
  product: ProductSummary;
};

function ProductActions({ product, show }: ProductInfoProps) {
  const [isLoading, setIsLoading] = useState(false);

  const springValue = useSpring(show, { stiffness: 300, damping: 30 });
  const rightPos = useTransform(springValue, [0, 1], ["-25%", "2.5%"]);
  const opacity = useTransform(springValue, [0, 1], [0, 1]);

  const handleClick = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      await addToCart({
        productId: product.id,
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <motion.div
        style={{ opacity, right: rightPos }}
        className="absolute top-[2.08%] flex flex-col gap-2"
      >
        <Button
          size="icon"
          className="h-8 w-8 [&_svg]:size-10"
          onClick={handleClick}
          disabled={isLoading}
        >
          {isLoading ? <Loader2 className="animate-spin" /> : <CirclePlus />}
        </Button>
      </motion.div>
    </>
  );
}

export default ProductActions;
