"use client";

import { productCategorySearchUrl } from "@/lib/lib";
import ProductList from "@/components/Product/ProductList";
import { fetcher } from "@/lib/axios";
import useSWR from "swr";

function WomenCollectionShowCase() {
  const { isLoading, data, error } = useSWR(
    productCategorySearchUrl("women"),
    fetcher,
  );

  if (isLoading)
    return (
      <div className="w-full opacity-50 text-1xl flex items-center justify-center">
        Loading...
      </div>
    );
  if (error)
    return (
      <div className="w-full opacity-50 text-1xl flex items-center justify-center">
        Error loading Products
      </div>
    );
  if (data == null)
    return (
      <div className="w-full opacity-50 text-1xl flex items-center justify-center">
        Something went wrong
      </div>
    );

  if (data.data.content.length === 0 && data.data.totalElements === 0) {
    return (
      <div className="w-full opacity-50 text-1xl flex items-center justify-center">
        No Products found
      </div>
    );
  }

  return <ProductList products={data.data.content} />;
}

export default WomenCollectionShowCase;
