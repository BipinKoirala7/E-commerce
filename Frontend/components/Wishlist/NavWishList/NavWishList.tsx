"use client";

import { GoArrowUpRight } from "react-icons/go";
import { useRouter } from "next/navigation";

import IconButton from "../../ui/IconButton";
import NavWishListUI from "./NavWishListUI";

function NavWishList() {
  const router = useRouter();

  return (
    <div className="h-full flex flex-col gap-1">
      <div className="flex items-center justify-between">
        <p className="text-xl header-font ml-1.5">WishList</p>
        <IconButton
          icon={<GoArrowUpRight className="w-5 h-5" />}
          className="hover:bg-primary"
          onClick={() => {
            router.push("/wishlist");
          }}
        />
      </div>
      <div className="h-full">
        <NavWishListUI />
      </div>
    </div>
  );
}

export default NavWishList;
