"use client";

import { MotionValue, useSpring, useTransform } from "framer-motion";
import { BsPatchPlus } from "react-icons/bs";
import { FaHeart, FaRegHeart } from "react-icons/fa";
import * as motion from "motion/react-client";

import IconButton from "@/components/ui/IconButton";
import { ProductSummary } from "@/types";
import { addToWishlist, removeFromWishlist } from "@/lib/api/wishlist";

type ProductInfoProps = {
  show: MotionValue<number>;
  product: ProductSummary;
  isWishlisted: boolean;
};

function ProductActions({ product, show, isWishlisted }: ProductInfoProps) {
  const springValue = useSpring(show, { stiffness: 300, damping: 30 });
  const rightPos = useTransform(springValue, [0, 1], ["-25%", "2.5%"]);
  const opacity = useTransform(springValue, [0, 1], [0, 1]);

  async function handleAddToWishlist() {
    await addToWishlist({ productId: product.id });
  }

  async function handleRemoveFromWishlist() {
    await removeFromWishlist(product.id);
  }

  return (
    <>
      <motion.div
        style={{ opacity, right: rightPos }}
        className={"absolute top-[2.08%] flex flex-col gap-2"}
      >
        <div className={""}>
          <IconButton icon={<BsPatchPlus className="w-6 h-6" />} />
        </div>
        {isWishlisted ? (
          <div onClick={async () => await handleRemoveFromWishlist()}>
            <IconButton icon={<FaHeart className="w-6 h-6 text-red-500" />} />
          </div>
        ) : (
          <div onClick={async () => await handleAddToWishlist()}>
            <IconButton icon={<FaRegHeart className="w-6 h-6" />} />
          </div>
        )}
      </motion.div>
    </>
  );
}

export default ProductActions;
