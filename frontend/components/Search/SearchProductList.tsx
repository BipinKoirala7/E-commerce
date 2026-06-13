import { fetcher } from "@/lib/axios";
import { productSearchUrl } from "@/lib/lib";
import { ProductSearchResponse } from "@/types";
import useSWR from "swr";
import ProductList from "@/components/Product/ProductList";
import Pagination from "@/components/Search/Pagination/Pagination";
import { useSearchParams } from "next/navigation";

function SearchProductList() {
  const params = useSearchParams();

  console.log(params);

  const query = productSearchUrl({
    query: params.get("query") || "",
    category: params.get("category") || "",
    minPrice: params.get("minPrice") || "",
    maxPrice: params.get("maxPrice") || "",
    sort: params.get("sort") || "",
    page: params.get("page")
      ? (Number(params.get("page")) - 1).toString()
      : "0",
    size: params.get("size") || "",
    direction: (params.get("direction") as "ASC" | "DESC") || "",
  });
  console.log(query);

  const { isLoading, data, error } = useSWR<ProductSearchResponse>(
    query,
    fetcher,
  );

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

  console.log("Product List: ", data.data);

  return (
    <div className="min-h-full flex flex-col gap-8">
      <ProductList products={data.data.content} />;
      <Pagination
        currentPage={data.data.currentPage}
        totalPages={data.data.totalPages}
        isFirst={data.data.isFirst}
        isLast={data.data.isLast}
        numberOfElements={data.data.numberOfElements}
      />
    </div>
  );
}

export default SearchProductList;
