"use client";

import { useState } from "react";
import { Dialog } from "@/components/ui/dialog";
import SortByOptions from "./SortByOptions";
import { Button } from "@/components/ui/button";

function SortBy() {
  const [show, setShow] = useState(false);
  return (
    <div className="relative">
      <Button
        // icon={<TbSortAscending className="text-2xl aspect-square" />}
        name="Sort By"
        onClick={() => setShow(!show)}
      />
      <Dialog open={show} onOpenChange={() => setShow(!show)}>
        <SortByOptions />
      </Dialog>
    </div>
  );
}

export default SortBy;
