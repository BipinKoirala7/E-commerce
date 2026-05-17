"use client";

import Button from "@/components/ui/Button";
import IconButton from "@/components/ui/IconButton";
import { useSearchParamsStore } from "@/store/zustand";
import { IoIosArrowBack, IoIosArrowForward } from "react-icons/io";

type PaginationProps = {
  currentPage: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
  numberOfElements: number;
};

function Pagination(props: PaginationProps) {
  const { currentPage, totalPages, isFirst, isLast } = props;
  const searchParamsStore = useSearchParamsStore();
  const pages: number[] = [];
  Array.from({ length: totalPages }).forEach(
    (_, i) => Math.abs(currentPage - i) < 5 && pages.push(i),
  );
  console.log("Current Page", currentPage);
  console.log("Pages", pages);

  return (
    <div className="flex gap-8 items-center justify-center">
      <div className="flex gap-8 items-center">
        <IconButton
          icon={<IoIosArrowBack className="w-6 h-6" />}
          disabled={isFirst}
        />
        <div className="flex gap-2">
          {pages.map((page) =>
            currentPage === page ? (
              <Button
                key={page}
                name={(page + 1).toString()}
                className="bg-text text-foreground hover:bg-text"
              />
            ) : (
              <Button
                key={page}
                name={(page + 1).toString()}
                onClick={() => searchParamsStore.setPage(page.toString())}
              />
            ),
          )}
        </div>
        <IconButton
          icon={<IoIosArrowForward className="w-6 h-6" />}
          disabled={isLast}
        />
      </div>
    </div>
  );
}

export default Pagination;
