"use client";

import { useState } from "react";
import FilterOptions from "@/components/Search/Filter/FilterOptions";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { IconButton } from "@/components/ui/IconButton";
import { SlidersHorizontal } from "lucide-react";
import { useRouter, useSearchParams } from "next/navigation";
import { usePathname } from "next/navigation";

function Filter() {
  const router = useRouter();
  const pathname = usePathname();
  const currentParams = useSearchParams();
  const [show, setShow] = useState(false);

  const updateQueryParam = (key: string, value: string) => {
    const params = new URLSearchParams(currentParams.toString());

    if (value) {
      params.set(key, value);
    } else {
      params.delete(key);
    }

    params.set("page", "1");
    router.push(`${pathname}?${params.toString()}`);
  };

  return (
    <Popover open={show} onOpenChange={setShow}>
      <PopoverTrigger onClick={() => setShow(!show)}>
        <SlidersHorizontal className="text-2xl aspect-square" />
      </PopoverTrigger>
      <PopoverContent
        className="w-full min-w-60 max-w-120"
        align="start"
        side="left"
      >
        <FilterOptions
          currentParams={currentParams}
          onUpdate={updateQueryParam}
        />
      </PopoverContent>
    </Popover>
  );
}

export default Filter;
