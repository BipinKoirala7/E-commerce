import { useRouter } from "next/navigation";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import {
  Popover,
  PopoverTrigger,
  PopoverContent,
} from "@/components/ui/popover";
import { LayoutList, LogOut, UserRound } from "lucide-react";

function AccountOption() {
  const [open, setOpen] = useState(false);
  const router = useRouter();

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <UserRound className="w-5 h-5" />
      </PopoverTrigger>

      <PopoverContent className="max-w-xs w-48 p-3" align="end">
        <p className="text-sm font-semibold px-2">My Account</p>

        <div className="flex flex-col gap-1">
          {(
            [
              {
                label: "Account",
                icon: <UserRound className="w-4 h-4" />,
                href: "/account",
              },
              {
                label: "Orders",
                icon: <LayoutList className="w-4 h-4" />,
                href: "/orders",
              },
              {
                label: "Log Out",
                icon: <LogOut className="w-4 h-4" />,
                href: "/logout",
              },
            ] as const
          ).map(({ label, icon, href }) => (
            <Button
              key={label}
              variant="ghost"
              className="w-full justify-start gap-2 hover:bg-primary hover:text-primary-foreground"
              onClick={() => {
                setOpen(false);
                router.push(href);
              }}
            >
              {icon}
              {label}
            </Button>
          ))}
        </div>
      </PopoverContent>
    </Popover>
  );
}

export default AccountOption;
