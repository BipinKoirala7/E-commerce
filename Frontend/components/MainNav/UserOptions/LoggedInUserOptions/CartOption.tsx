import { useState } from "react";
import { PiShoppingCartSimple } from "react-icons/pi";

import IconButton from "@/components/ui/IconButton";
import Modal from "@/components/ui/Modal";
import NavCartList from "@/components/Cart/NavCartList/NavCartList";

function CartOption() {
  const [show, setShow] = useState(false);
  return (
    <div className="relative">
      <IconButton
        onClick={() => setShow(!show)}
        icon={<PiShoppingCartSimple className="w-6 h-6" />}
      />
      <Modal show={show} className="w-full min-w-100 max-w-100 min-h-30 h-full">
        <NavCartList />
      </Modal>
    </div>
  );
}

export default CartOption;
