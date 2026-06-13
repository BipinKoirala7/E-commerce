"use client";

import { ProductSummary } from "@/types";

import { MotionValue, useSpring, useTransform } from "framer-motion";
import * as motion from "motion/react-client";
import Link from "next/link";

type ProductInfoProps = {
  show: MotionValue<number>;
  product: ProductSummary;
};

function ProductInfo({ show, product }: ProductInfoProps) {
  const springValue = useSpring(show, { stiffness: 300, damping: 30 });
  const bottomPos = useTransform(springValue, [0, 1], ["-14%", "0%"]);
  return (
    <>
      <motion.div
        style={{ bottom: bottomPos }}
        className={`absolute left-1/2 -translate-x-1/2 w-full bg-secondary hover:bg-text hover:text-foreground transition-colors duration-300 text-text rounded-sm`}
      >
        <Link
          href={`/product/${product.id}`}
          className="flex flex-col gap-2 p-2"
        >
          <p className="text-2xl header-font">{product.brand.toWellFormed()}</p>
          <p className={`single-line`}>{product.name.toWellFormed()}</p>
          <p className={"font-bold"}>${product.price}</p>
        </Link>
      </motion.div>
    </>
  );
}

export default ProductInfo;
