import CategoryProductList from "@/components/Category/CategoryProductList";

type CategoryParams = {
  params: Promise<{ category: string }>;
};

export default async function Page({ params }: CategoryParams) {
  const { category } = await params;
  return (
    <div className="w-full min-h-full h-full flex items-center justify-center px-8 py-4">
      <CategoryProductList category={category} />
    </div>
  );
}
