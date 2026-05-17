"use client";

import IconWithNameButton from "../../ui/IconWithNameButton";
import Modal from "../../ui/Modal";

import { useState } from "react";
import { IoFilter } from "react-icons/io5";
import FilterOptions from "@/components/Search/Filter/FilterOptions";

function Filter() {
  const [show, setShow] = useState(false);
  return (
    <div className="relative">
      <IconWithNameButton
        icon={<IoFilter className="text-2xl aspect-square" />}
        name="Filter"
        onClick={() => setShow(!show)}
      />
      <Modal show={show} className="w-full min-w-40 max-w-80">
        <FilterOptions />
      </Modal>
    </div>
  );
}

export default Filter;
