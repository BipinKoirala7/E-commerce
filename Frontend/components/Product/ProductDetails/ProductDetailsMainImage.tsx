import { type ProductImage } from "@/types";
import Image from "next/image";

type ProductDetailsMainImageProps = {
  productImage: ProductImage;
};

function ProductDetailsMainImage({
  productImage,
}: ProductDetailsMainImageProps) {
  return (
    <div className="w-full flex items-center justify-center p-2">
      <Image
        src={productImage.imageUrl}
        alt={productImage.id}
        width={600}
        height={600}
        className="w-auto h-auto object-contain"
        loading="eager"
      />
    </div>
  );
}

export default ProductDetailsMainImage;
