"use client";

import { Button } from "@/components/ui/button";
import { IconButton } from "@/components/ui/IconButton";
import { ArrowLeft, ArrowRight } from "lucide-react";
import { useRouter, usePathname, useSearchParams } from "next/navigation";

type PaginationProps = {
  currentPage: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
  numberOfElements: number;
};

function Pagination(props: PaginationProps) {
  const { currentPage, totalPages, isFirst, isLast } = props;
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const pages: number[] = [];
  Array.from({ length: totalPages }).forEach(
    (_, i) => Math.abs(currentPage - i) < 5 && pages.push(i),
  );

  function goToPage(page: number) {
    const params = new URLSearchParams(searchParams.toString());
    params.set("page", (page + 1).toString());
    router.push(`${pathname}?${params.toString()}`);
  }

  return (
    <div className="flex gap-8 items-center justify-center">
      <div className="flex gap-8 items-center">
        <IconButton
          icon={<ArrowLeft className="w-6 h-6" />}
          disabled={isFirst}
          onClick={() => goToPage(currentPage - 1)}
        />
        <div className="flex gap-2">
          {pages.map((page) =>
            currentPage === page ? (
              <Button key={page} className="bg-text text-f hover:bg-text">
                {page + 1}
              </Button>
            ) : (
              <Button
                key={page}
                variant="outline"
                onClick={() => goToPage(page)}
              >
                {page + 1}
              </Button>
            ),
          )}
        </div>
        <IconButton
          icon={<ArrowRight className="w-6 h-6" />}
          disabled={isLast}
          onClick={() => goToPage(currentPage + 1)}
        />
      </div>
    </div>
  );
}

export default Pagination;
