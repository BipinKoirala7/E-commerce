import { ProductImage } from "@/types";
import Image from "next/image";

type ProductImageOptionsProps = {
  productImages: ProductImage[];
};

function ProductDetailsImageOptions({
  productImages,
}: ProductImageOptionsProps) {
  return (
    <div className="flex flex-col gap-2">
      {productImages.map((img) => (
        <div
          key={img.id}
          className="w-fit bg-foreground flex items-center justify-center p-2 cursor-pointer hover:bg-secondary smooth-transition"
        >
          <Image
            src={img.imageUrl}
            alt={img.id}
            width={100}
            height={100}
            className="w-fit h-32 object-contain max-h-100 bg-primary"
            loading="eager"
          />
        </div>
      ))}
    </div>
  );
}

export default ProductDetailsImageOptions;
