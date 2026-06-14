import { ProductSearchParams } from "@/types";
import { ApiEndpoint } from "./ApiEndpoint";

export const productSearchUrl = (params: ProductSearchParams) => {
  const searchParams = new URLSearchParams(
    Object.entries(params).filter(
      ([_, value]) => value != null && value !== "",
    ),
  );

  return ApiEndpoint.PRODUCT + "?" + searchParams.toString();
};

export const productCategorySearchUrl = (category: string) => {
  return ApiEndpoint.PRODUCT + "/category/" + category;
};

export const productDetailsUrl = (productId: string) => {
  return ApiEndpoint.PRODUCT + "/" + productId;
};

export const capitalize = (str: string) =>
  str.charAt(0).toUpperCase() + str.slice(1);
