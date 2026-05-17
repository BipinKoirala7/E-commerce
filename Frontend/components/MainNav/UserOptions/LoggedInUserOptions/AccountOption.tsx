import { useRouter } from "next/navigation";
import { useState } from "react";

import Button from "@/components/ui/Button";
import IconButton from "@/components/ui/IconButton";
import { LuUserRound } from "react-icons/lu";
import Modal from "@/components/ui/Modal";

function AccountOption() {
  const [show, setShow] = useState(false);
  const router = useRouter();
  return (
    <div className="relative">
      <IconButton
        onClick={() => setShow(!show)}
        icon={<LuUserRound className="w-6 h-6" />}
      />
      <Modal show={show} className="w-full min-w-40 max-w-80">
        <div className="flex flex-col gap-2">
          <Button
            name="Account"
            className="w-full hover:bg-primary"
            onClick={() => {
              router.push("/account");
              setShow(false);
            }}
          />
          <Button
            name="Orders"
            className="w-full hover:bg-primary"
            onClick={() => {
              router.push("/orders");
              setShow(false);
            }}
          />
          <Button
            name="Log Out"
            className="w-full hover:bg-primary"
            onClick={() => {
              router.push("/logout");
              setShow(false);
            }}
          />
        </div>
      </Modal>
    </div>
  );
}

export default AccountOption;
