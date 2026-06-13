import Image from "next/image";

type ProductDetailsMainImageProps = {
  productImage: string;
};

function ProductDetailsMainImage({
  productImage,
}: ProductDetailsMainImageProps) {
  return (
    <div className="w-full flex items-center justify-center p-2">
      <Image
        src={productImage}
        alt={`an image`}
        width={600}
        height={600}
        className="w-auto h-auto object-contain"
        loading="eager"
      />
    </div>
  );
}

export default ProductDetailsMainImage;
