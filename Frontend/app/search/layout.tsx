import SearchFilters from "@/components/Search/SearchFilters";

function layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="w-full min-h-full flex flex-col gap-4 px-8 pt-2 pb-16">
      <SearchFilters />
      <div className="w-full h-full">{children}</div>
    </div>
  );
}

export default layout;
