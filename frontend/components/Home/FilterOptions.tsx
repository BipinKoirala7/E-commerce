import Filter from "../Search/Filter/Filter";
import SortBy from "../Search/SortBy/SortBy";

export default function FilterOptions() {
  return (
    <div className="flex gap-4 items-center">
      <Filter />
      <SortBy />
    </div>
  );
}
