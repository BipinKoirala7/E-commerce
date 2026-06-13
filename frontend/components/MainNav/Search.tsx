"use client";

import { Search as SearchIcon, X } from "lucide-react";
import { useRouter } from "next/navigation";
import { useRef, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { ButtonGroup } from "@/components/ui/button-group";

function Search() {
  const inputRef = useRef<HTMLInputElement>(null);
  const [searchInput, setSearchInput] = useState<string>("");
  const router = useRouter();

  function handleSearch() {
    if (searchInput.trim()) {
      router.push(`/search?q=${encodeURIComponent(searchInput.trim())}`);
    }
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === "Enter") {
      handleSearch();
    }
    if (e.key === "Escape") {
      setSearchInput("");
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
        onChange={(e) => setSearchInput(e.target.value)}
        onKeyDown={handleKeyDown}
        value={searchInput}
      />

      <Button
        variant="ghost"
        size="icon"
        className={`h-7 w-7 rounded-l-sm shrink-0 transition-opacity ${searchInput.length > 0 ? "opacity-100" : "opacity-0 pointer-events-none"}`}
        onClick={() => {
          setSearchInput("");
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
