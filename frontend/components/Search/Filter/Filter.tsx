"use client";

import { useState } from "react";
import FilterOptions from "@/components/Search/Filter/FilterOptions";
import { Button } from "@/components/ui/button";
import { Dialog } from "@/components/ui/dialog";

function Filter() {
  const [show, setShow] = useState(false);
  return (
    <div className="relative">
      <Button
        // icon={<IoFilter className="text-2xl aspect-square" />}
        name="Filter"
        onClick={() => setShow(!show)}
      />
      {/* <Dialog
        open={show}
        onOpenChange={() => setShow(!show)}
        //  className="w-full min-w-40 max-w-80"
      >
        <FilterOptions />
      </Dialog> */}
    </div>
  );
}

export default Filter;
