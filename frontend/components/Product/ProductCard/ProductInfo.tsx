"use client";

import { ProductSummary } from "@/types";
import * as motion from "motion/react-client";
import Link from "next/link";

type ProductInfoProps = {
  product: ProductSummary;
};

function ProductInfo({ product }: ProductInfoProps) {
  const formattedPrice = new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 0,
  }).format(product.price);

  return (
    <motion.div
      whileHover={{ y: -4 }}
      className="w-full flex-1 flex flex-col bg-secondary hover:bg-green3 hover:text-f transition-colors duration-300 border border-transparent hover:shadow-md"
    >
      <Link
        href={`/product/${product.id}`}
        className="flex flex-col flex-1 gap-1.5 p-4 text-text hover:text-f"
      >
        <h3 className="text-xl font-semibold tracking-tight header-font">
          {product.brand.toWellFormed()}
        </h3>
        <p className="text-sm opacity-80 truncate standard-font">
          {product.name.toWellFormed()}
        </p>
        <p className="mt-auto pt-2 text-base font-bold">{formattedPrice}</p>
      </Link>
    </motion.div>
  );
}

export default ProductInfo;
