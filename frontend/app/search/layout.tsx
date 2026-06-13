import SearchFilters from "@/components/Search/SearchFilters";
import { Suspense } from "react";

function layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="w-full min-h-full flex flex-col gap-4 px-8 pt-2 pb-16">
      <SearchFilters />
      <Suspense fallback={<div className="text-center py-10">Loading...</div>}>
        <div className="w-full h-full">{children}</div>
      </Suspense>
    </div>
  );
}

export default layout;
