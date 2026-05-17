"use client";

import { useMotionValue } from "framer-motion";
import Image from "next/image";
import * as motion from "motion/react-client";
import { Variants } from "motion";

import ProductInfo from "@/components/Product/ProductCard/ProductInfo";
import ProductActions from "@/components/Product/ProductCard/ProductActions";
import { ProductSummary } from "@/types";

type ProductCardProps = {
  product: ProductSummary;
  isWishlisted: boolean;
};

function ProductCard({ product, isWishlisted }: ProductCardProps) {
  const show = useMotionValue(0);

  //  Product Card Variants. Don't change the name ("hidden" and "visible"). Inherited from parent.
  const variants: Variants = {
    //  Start at 10px from the bottom
    hidden: { opacity: 0, y: -10 },
    visible: { opacity: 1, y: 0 },
  };
  return (
    <motion.div
      onMouseEnter={() => show.set(1)}
      onMouseLeave={() => show.set(0)}
      variants={variants}
      className="relative bg-foreground rounded-sm overflow-hidden cursor-pointer"
    >
      <div
        className={
          "flex items-center justify-center aspect-5/7 mix-blend-multiply"
        }
      >
        <Image
          src={product.imageUrl}
          alt={product.name}
          width={500}
          height={100}
          className="object-contain max-h-100"
          loading="eager"
        />
      </div>
      <ProductActions
        show={show}
        product={product}
        isWishlisted={isWishlisted}
      />
      <ProductInfo show={show} product={product} />
    </motion.div>
  );
}

export default ProductCard;
