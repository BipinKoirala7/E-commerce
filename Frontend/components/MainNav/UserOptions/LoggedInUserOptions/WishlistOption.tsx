"use client";

import Modal from "@/components/ui/Modal";
import { useState } from "react";
import { SlHeart } from "react-icons/sl";

import IconButton from "@/components/ui/IconButton";
import NavWishList from "@/components/Wishlist/NavWishList/NavWishList";

function WishlistOption() {
  const [show, setShow] = useState(false);

  return (
    <div className="relative">
      <IconButton
        onClick={() => {
          setShow(!show);
        }}
        icon={<SlHeart className="w-6 h-6" />}
      />
      <Modal show={show} className="w-full min-w-100 max-w-100 min-h-30 h-fit">
        <NavWishList />
      </Modal>
    </div>
  );
}

export default WishlistOption;
