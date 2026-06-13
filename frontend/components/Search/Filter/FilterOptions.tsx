import { capitalize } from "@/lib/lib";
import { Category, SortDirection } from "@/types";
import { ReadonlyURLSearchParams } from "next/navigation";
import toast from "react-hot-toast";

type FilterOptionsProps = {
  currentParams: ReadonlyURLSearchParams;
  onUpdate: (key: string, value: string) => void;
};

function FilterOptions({ currentParams, onUpdate }: FilterOptionsProps) {
  return (
    <div className="flex flex-col gap-2">
      <p className="text-2xl header-font">Filter</p>
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
                    checked={currentParams.get("category") === category}
                    radioGroup="categories"
                    onChange={() => onUpdate("category", category)}
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
          <div className="flex gap-3">
            <div className="flex flex-col gap-2">
              <label htmlFor="">Min Price: </label>
              <input
                type="text"
                name="minPrice"
                id="minPrice"
                min={currentParams.get("minPrice") || ""}
                value={currentParams.get("minPrice") || ""}
                onChange={(e) => {
                  console.log(e.currentTarget.value);
                  console.log(currentParams.get("maxPrice"));
                  if (
                    Number(e.currentTarget.value) >=
                    Number(currentParams.get("maxPrice"))
                  ) {
                    toast.error("Max Price cannot be less than Min Price");
                    return;
                  }
                  onUpdate("minPrice", e.currentTarget.value);
                }}
                className="outline-none bg-p px-2 py-1"
              />
            </div>
            <div className="flex flex-col gap-2">
              <label htmlFor="">Max Price: </label>
              <input
                type="text"
                name="maxPrice"
                id="maxPrice"
                value={currentParams.get("maxPrice") || ""}
                onChange={(e) => onUpdate("maxPrice", e.currentTarget.value)}
                className="outline-none bg-p px-2 py-1"
              />
            </div>
          </div>
        </div>
        <div className="flex flex-col gap-2">
          <p className="text-2xl header-font">Sort</p>
          <div className="flex flex-col gap-3">
            {Object.entries(SortDirection).map(
              ([directionLabel, direction]) => {
                return (
                  <div key={direction} className="flex gap-2 items-center">
                    <input
                      type="radio"
                      name="sort"
                      id={direction}
                      checked={currentParams.get("direction") === direction}
                      onChange={() => onUpdate("direction", direction)}
                      value={direction}
                    />
                    <label htmlFor={direction} className=" cursor-pointer">
                      {capitalize(directionLabel)}
                    </label>
                  </div>
                );
              },
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default FilterOptions;
