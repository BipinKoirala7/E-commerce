import { capitalize } from "@/lib/lib";
import { useSearchParamsStore } from "@/store/zustand";
import { Category } from "@/types";
import toast from "react-hot-toast";

function FilterOptions() {
  const searchParamsStore = useSearchParamsStore();

  return (
    <div className="flex flex-col gap-2">
      <p className="text-3xl header-font">Filter</p>
      <div className="flex flex-col gap-3">
        <div className="flex flex-col gap-2">
          <p className="text-xl">Category</p>
          <div className="flex flex-col gap-2">
            {Object.values(Category).map((category) => {
              return (
                <div key={category} className="flex gap-2 items-center">
                  <input
                    type="radio"
                    name="category"
                    id={category}
                    value={category}
                    checked={searchParamsStore.category === category}
                    radioGroup="categories"
                    onChange={() => searchParamsStore.setCategory(category)}
                  />
                  <label htmlFor={category} className=" cursor-pointer">
                    {capitalize(category)}
                  </label>
                </div>
              );
            })}
          </div>
        </div>
        <div className="flex flex-col gap-2">
          <p className="text-xl">Price Range</p>
          <div className="flex flex-col gap-3">
            <div className="flex flex-col gap-2">
              <label htmlFor="">Min Price: </label>
              <input
                type="text"
                name="minPrice"
                id="minPrice"
                min={searchParamsStore.maxPrice}
                value={searchParamsStore.minPrice}
                onChange={(e) => {
                  if (e.currentTarget.value >= searchParamsStore.maxPrice) {
                    toast.error("Max Price cannot be less than Min Price");
                    return;
                  }
                  searchParamsStore.setPriceRange(
                    e.currentTarget.value,
                    searchParamsStore.maxPrice,
                  );
                }}
                className="outline-none bg-primary px-2 py-1"
              />
            </div>
            <div className="flex flex-col gap-2">
              <label htmlFor="">Max Price: </label>
              <input
                type="text"
                name="maxPrice"
                id="maxPrice"
                value={searchParamsStore.maxPrice}
                onChange={(e) =>
                  searchParamsStore.setPriceRange(
                    searchParamsStore.minPrice,
                    e.currentTarget.value,
                  )
                }
                className="outline-none bg-primary px-2 py-1"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default FilterOptions;
