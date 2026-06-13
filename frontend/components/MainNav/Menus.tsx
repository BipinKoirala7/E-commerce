import { Menu } from "lucide-react";
import { NavButton } from "@/components/ui/NavButton";
import { IconButton } from "@/components/ui/IconButton";

function Menus() {
  return (
    <div className="h-full flex gap-2 items-center">
      <IconButton icon={<Menu />} />
      <NavButton route={"/"} name="Shop" />
    </div>
  );
}

export default Menus;
