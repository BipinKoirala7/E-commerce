"use client";

import { Search as SearchIcon, X } from "lucide-react";
import { useRouter, useSearchParams } from "next/navigation";
import { useRef, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

function Search() {
  const inputRef = useRef<HTMLInputElement>(null);
  const router = useRouter();
  const searchParams = useSearchParams();
  const [searchQuery, setSearchQuery] = useState(
    searchParams.get("query") || "",
  );

  function handleSearch() {
    if (searchQuery.trim()) {
      router.push(`/search?query=${encodeURIComponent(searchQuery.trim())}`);
    }
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === "Enter") {
      handleSearch();
    }
    if (e.key === "Escape") {
      router.push(`/search`);
      inputRef.current?.blur();
    }
  }

  return (
    <div className="flex items-center rounded-md bg-f px-2 py-1 gap-1">
      <Input
        ref={inputRef}
        type="text"
        className="max-w-48 w-48 h-8 text-t border-none shadow-none bg-transparent focus-visible:ring-0 placeholder:text-s"
        placeholder="Search Products..."
        onKeyDown={handleKeyDown}
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.currentTarget.value)}
      />

      <Button
        variant="ghost"
        size="icon"
        className={`h-7 w-7 rounded-l-sm shrink-0 transition-opacity ${searchParams.get("query") ? "opacity-100" : "opacity-0 pointer-events-none"}`}
        onClick={() => {
          router.push(`/search?query=`);
          inputRef.current?.focus();
        }}
      >
        <X className="h-4 w-4" />
      </Button>
      <Button size="icon" className="h-7 w-7 shrink-0" onClick={handleSearch}>
        <SearchIcon className="h-4 w-4" />
      </Button>
    </div>
  );
}

export default Search;
