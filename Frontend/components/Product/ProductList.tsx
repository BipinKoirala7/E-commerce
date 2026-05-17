"use client";

import * as motion from "motion/react-client";
import { Variants } from "motion";

import ProductCard from "@/components/Product/ProductCard/ProductCard";
import { ProductSummary } from "@/types";
import { useWishlist } from "@/hooks/useWishlist";

type ProductListProps = {
  products: ProductSummary[];
};

function ProductList({ products }: ProductListProps) {
  const variants: Variants = {
    hidden: {},
    visible: {
      transition: {
        staggerChildren: 0.25,
        delayChildren: 0.5,
      },
    },
  };

  const { wishlistedIds } = useWishlist();

  const isWishlisted = (productId: string) => wishlistedIds.has(productId);
  return (
    <motion.div
      // [repeat(auto-fit,minmax(250px,1fr))]
      className="grow w-full h-full grid grid-cols-4 gap-4"
      variants={variants}
      initial="hidden"
      animate="visible"
    >
      {products.map((product) => (
        <div key={product.id} className="flex flex-col gap-2">
          <ProductCard
            product={product}
            isWishlisted={isWishlisted(product.id)}
          />
        </div>
      ))}
    </motion.div>
  );
}

export default ProductList;
