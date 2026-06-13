import { capitalize } from "@/lib/lib";
import { useSearchParamsStore } from "@/store/zustand";
import { SortDirection } from "@/types";

function SortByOptions() {
  const searchParamsStore = useSearchParamsStore();

  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-col gap-2">
        <p className="text-2xl header-font">Sort</p>
        <div className="flex flex-col gap-3">
          {Object.entries(SortDirection).map(([directionLabel, direction]) => {
            return (
              <div key={direction} className="flex gap-2 items-center">
                <input
                  type="radio"
                  name="sort"
                  id={direction}
                  checked={searchParamsStore.direction === direction}
                  onChange={() => searchParamsStore.setDirection(direction)}
                  value={direction}
                />
                <label htmlFor={direction} className=" cursor-pointer">
                  {capitalize(directionLabel)}
                </label>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

export default SortByOptions;
