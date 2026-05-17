"use client";

import { fetcher } from "@/lib/axios";
import { ProductSearchResponse } from "@/types";
import useSWR from "swr";
import ProductList from "@/components/Product/ProductList";
import { productCategorySearchUrl } from "@/lib/lib";

type CategoryProductListProps = {
  category: string;
};

function CategoryProductList({ category }: CategoryProductListProps) {
  const query = productCategorySearchUrl(category);
  console.log(query);

  const { isLoading, data, error } = useSWR<ProductSearchResponse>(
    query,
    fetcher,
  );

  console.log(data);

  if (isLoading)
    return (
      <div className="opacity-50 text-1xl flex items-center justify-center">
        Loading...
      </div>
    );
  if (error)
    return (
      <div className="opacity-50 text-1xl flex items-center justify-center">
        Error loading Products
      </div>
    );
  if (data == null)
    return (
      <div className="opacity-50 text-1xl flex items-center justify-center">
        Something went wrong
      </div>
    );

  if (data.data.content.length === 0 && data.data.totalElements === 0) {
    return (
      <div className="opacity-50 text-1xl flex items-center justify-center">
        No Products found
      </div>
    );
  }

  return <ProductList products={data.data.content} />;
}

export default CategoryProductList;
