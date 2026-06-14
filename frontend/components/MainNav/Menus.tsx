import { NavButton } from "@/components/ui/NavButton";

function Menus() {
  return (
    <div className="h-full flex gap-2 items-center">
      <NavButton route={"/"} name="Shop" />
    </div>
  );
}

export default Menus;
