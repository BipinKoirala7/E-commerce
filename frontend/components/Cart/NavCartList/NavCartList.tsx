"use client";

import IconButton from "../../ui/IconButton";

import { GoArrowUpRight } from "react-icons/go";
import { useRouter } from "next/navigation";
import NavCartUI from "./NavCartUI";

function NavCartList() {
  const router = useRouter();

  return (
    <div className="h-full flex flex-col gap-1">
      <div className="flex items-center justify-between">
        <p className="text-xl header-font ml-1.5">Cart</p>
        <IconButton
          icon={<GoArrowUpRight className="w-5 h-5" />}
          className="hover:bg-primary"
          onClick={() => {
            router.push("/cart");
          }}
        />
      </div>
      <div className="h-full">
        <NavCartUI />
      </div>
    </div>
  );
}

export default NavCartList;
