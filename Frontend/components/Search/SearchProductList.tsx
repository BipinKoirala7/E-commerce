import { fetcher } from "@/lib/axios";
import { productSearchUrl } from "@/lib/lib";
import { useSearchParamsStore } from "@/store/zustand";
import { ProductSearchParams, ProductSearchResponse } from "@/types";
import useSWR from "swr";
import ProductList from "@/components/Product/ProductList";
import Pagination from "@/components/Search/Pagination/Pagination";

function SearchProductList() {
  const store = useSearchParamsStore();
  const params: ProductSearchParams = {
    query: store.query,
    category: store.category,
    minPrice: store.minPrice,
    maxPrice: store.maxPrice,
    sort: store.sort,
    page: store.page,
    size: store.size,
    direction: store.direction,
  };

  const query = productSearchUrl(params);
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
