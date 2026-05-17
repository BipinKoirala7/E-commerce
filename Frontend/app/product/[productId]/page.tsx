import ProductDetails from "@/components/Product/ProductDetails/ProductDetails";

type ProductPageProps = {
  params: Promise<{ productId: string }>;
};

async function page({ params }: ProductPageProps) {
  const { productId } = await params;
  return (
    <div className="w-full min-h-full px-8 py-4">
      <ProductDetails productId={productId} />
    </div>
  );
}

export default page;
