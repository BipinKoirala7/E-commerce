import { useRouter } from "next/navigation";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import {
  Popover,
  PopoverTrigger,
  PopoverContent,
} from "@/components/ui/popover";
import { LayoutList, LogOut, UserRound } from "lucide-react";
import { handleLogOut } from "@/lib/api/auth";

function AccountOption() {
  const [open, setOpen] = useState(false);
  const router = useRouter();

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger>
        <UserRound className="w-5 h-5" />
      </PopoverTrigger>

      <PopoverContent className="max-w-xs w-48 p-3" align="end">
        <p className="text-sm font-semibold px-2">My Account</p>

        <div className="flex flex-col gap-1">
          <Button
            variant="ghost"
            className="w-full justify-start gap-2 hover:bg-primary hover:text-primary-foreground"
            onClick={() => {
              setOpen(false);
              router.push("/account");
            }}
          >
            <UserRound className="w-4 h-4" />
            Account
          </Button>

          <Button
            variant="ghost"
            className="w-full justify-start gap-2 hover:bg-primary hover:text-primary-foreground"
            onClick={() => {
              setOpen(false);
              router.push("/order");
            }}
          >
            <LayoutList className="w-4 h-4" />
            Orders
          </Button>

          <Button
            variant="ghost"
            className="w-full justify-start gap-2 hover:bg-primary hover:text-primary-foreground"
            onClick={() => {
              setOpen(false);
              handleLogOut();
            }}
          >
            <LogOut className="w-4 h-4" />
            Log Out
          </Button>
        </div>
      </PopoverContent>
    </Popover>
  );
}

export default AccountOption;
