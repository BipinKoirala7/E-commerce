import { Button } from "@/components/ui/button";

function Menus() {
  return (
    <div className="h-full flex gap-2 items-center">
      <Button />
      {/*  icon={<RxHamburgerMenu className="w-6 h-6" />}  */}
      <div className=" ">
        <Button />
        {/* route="/" name="Shop" */}
      </div>
    </div>
  );
}

export default Menus;
