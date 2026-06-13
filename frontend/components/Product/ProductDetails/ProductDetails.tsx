"use client";

import { fetcher } from "@/lib/axios";
import { productDetailsUrl } from "@/lib/lib";
import { ProductDetailsResponse } from "@/types";

import useSWR from "swr";
import ProductDetailsActions from "./ProductDetailsActions";
import ProductDetailsInfo from "@/components/Product/ProductDetails/ProductDetailsInfo";
import ProductDetailsMainImage from "./ProductDetailsMainImage";
import ProductDetailsImageOptions from "./ProductDetailsImageOptions";

function ProductDetailsPage({ productId }: { productId: string }) {
  console.log(productId);

  const { isLoading, data, error } = useSWR<ProductDetailsResponse>(
    productDetailsUrl(productId),
    fetcher,
  );

  // Handle loading, error, and empty states
  // Make sure the message are UI friendly and are in the middle of whole container.
  if (isLoading)
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Loading...
      </div>
    );
  if (error)
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Error loading wishlist
      </div>
    );
  if (data == null)
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Something went wrong
      </div>
    );
  if (data.data == null)
    return (
      <div className="min-h-60 opacity-50 text-1xl flex items-center justify-center">
        Something went wrong
      </div>
    );
  return (
    <div className="w-full h-full grid grid-cols-2 gap-8">
      <ProductDetailsMainImage productImage={data.data.images[0]} />
      <div className="flex flex-col gap-4 py-20">
        <ProductDetailsInfo
          name={data.data.name}
          price={data.data.price}
          brand={data.data.brand}
        />
        <ProductDetailsImageOptions productImages={data.data.images} />
        <p>{data.data.description}</p>
        <ProductDetailsActions productId={productId} />
      </div>
    </div>
  );
}

export default ProductDetailsPage;
