import { useRouter } from "next/navigation";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { LayoutList, LogOut, UserRound } from "lucide-react";

function AccountOption() {
  const [open, setOpen] = useState(false);
  const router = useRouter();
  return (
    <Dialog open={open} onOpenChange={() => setOpen((prev) => !prev)}>
      <DialogTrigger
        render={
          <Button variant="ghost" size="icon">
            <UserRound className="w-5 h-5" />
            <span className="sr-only">Account options</span>
          </Button>
        }
      />

      <DialogContent showCloseButton={false} className="max-w-xs p-3">
        <DialogHeader>
          <DialogTitle>My Account</DialogTitle>
        </DialogHeader>

        <div className="flex flex-col gap-1">
          <Dialog>
            <DialogTrigger>
              {/* aschild threw error */}
              {/* Inner triggers not needed — use DialogClose pattern */}
            </DialogTrigger>
          </Dialog>

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
              onClick={() => router.push(href)}
            >
              {icon}
              {label}
            </Button>
          ))}
        </div>
      </DialogContent>
    </Dialog>
  );
}

export default AccountOption;
