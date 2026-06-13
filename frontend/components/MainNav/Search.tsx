"use client";

import { X } from "lucide-react";
import { useRouter } from "next/navigation";
import { useEffect, useRef, useState } from "react";

function Search() {
  const inputRef = useRef<HTMLInputElement>(null);
  const [searchInput, setSearchInput] = useState<string>("");
  const router = useRouter();

  // function handleSearch() {
  //   searchParamsStore.setQuery(searchInput);
  //   router.push("/search");
  // }

  // useEffect(() => {
  //   function handleKeyDown(e: KeyboardEvent) {
  //     if (e.key === "Enter") {
  //       // searchParamsStore.setQuery(searchInput);
  //       router.push("/search");
  //       return;
  //     }
  //     if (e.key === "Escape") {
  //       setSearchInput("");
  //       return;
  //     }
  //   }
  //   if (inputRef.current?.focus) {
  //     window.addEventListener("keydown", handleKeyDown);
  //   }
  // }, [inputRef, searchInput, router, searchParamsStore]);

  return (
    <div className="flex gap-2 items-center rounded-sm bg-foreground pl-2 pr-4 py-2">
      <input
        ref={inputRef}
        type="text"
        className="h-full w-full border-none outline-none rounded-sm "
        placeholder="Search Products..."
        onChange={(e) => setSearchInput(e.target.value)}
        value={searchInput}
      />
      <X
        className="h-full text-[1.5rem] cursor-pointer "
        onClick={() => setSearchInput("")}
        opacity={searchInput.length > 0 ? 0.5 : 0}
      />
      <Search
      // className="h-full text-[1.5rem] cursor-pointer"
      // opacity={searchInput.length > 0 ? 1 : 0}
      // onClick={handleSearch}
      />
    </div>
  );
}

export default Search;
