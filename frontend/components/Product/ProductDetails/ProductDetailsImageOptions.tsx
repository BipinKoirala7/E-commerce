import Image from "next/image";

type ProductImageOptionsProps = {
  productImage: string;
};

function ProductDetailsImageOptions({
  productImage,
}: ProductImageOptionsProps) {
  return (
    <div className="flex flex-col gap-2">
      <div
        // key={img.id}
        className="w-fit bg-foreground flex items-center justify-center p-2 cursor-pointer hover:bg-secondary smooth-transition"
      >
        <Image
          src={productImage}
          alt={`a image`}
          width={100}
          height={100}
          className="w-fit h-32 object-contain max-h-100 bg-primary"
          loading="eager"
        />
      </div>
    </div>
  );
}

export default ProductDetailsImageOptions;
