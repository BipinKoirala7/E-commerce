"use client";

import { useState } from "react";
import Modal from "../../ui/Modal";
import IconWithNameButton from "../../ui/IconWithNameButton";
import { TbSortAscending } from "react-icons/tb";
import SortByOptions from "./SortByOptions";

function SortBy() {
  const [show, setShow] = useState(false);
  return (
    <div className="relative">
      <IconWithNameButton
        icon={<TbSortAscending className="text-2xl aspect-square" />}
        name="Sort By"
        onClick={() => setShow(!show)}
      />
      <Modal show={show} className="w-full min-w-40 max-w-80">
        <SortByOptions />
      </Modal>
    </div>
  );
}

export default SortBy;
