type ProductDetailsInfoProps = {
  name: string;
  price: number;
  brand: string;
};

function ProductDetailsInfo({ name, price, brand }: ProductDetailsInfoProps) {
  return (
    <div className="flex flex-col gap-2">
      <p className="flex text-7xl header-font">{brand}</p>
      <p className="text-4xl">{name}</p>
      <p id="logo" className="text-2xl font-bold logo-font">
        ${price}
      </p>
    </div>
  );
}

export default ProductDetailsInfo;
