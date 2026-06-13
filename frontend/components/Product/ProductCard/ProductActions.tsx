"use client";

import { CirclePlus } from "lucide-react";
import { MotionValue, useSpring, useTransform } from "framer-motion";
import * as motion from "motion/react-client";

import { Button } from "@/components/ui/button";
import { ProductSummary } from "@/types";

type ProductInfoProps = {
  show: MotionValue<number>;
  product: ProductSummary;
};

function ProductActions({ product, show }: ProductInfoProps) {
  const springValue = useSpring(show, { stiffness: 300, damping: 30 });
  const rightPos = useTransform(springValue, [0, 1], ["-25%", "2.5%"]);
  const opacity = useTransform(springValue, [0, 1], [0, 1]);

  return (
    <>
      <motion.div
        style={{ opacity, right: rightPos }}
        className="absolute top-[2.08%] flex flex-col gap-2"
      >
        <Button size="icon" className="h-8 w-8 [&_svg]:size-10">
          <CirclePlus />
        </Button>
      </motion.div>
    </>
  );
}

export default ProductActions;
