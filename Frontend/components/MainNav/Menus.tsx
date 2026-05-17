import IconButton from "../ui/IconButton";
import { RxHamburgerMenu } from "react-icons/rx";
import NavButton from "../ui/NavButton";

function Menus() {
  return (
    <div className="h-full flex gap-2 items-center">
      <IconButton icon={<RxHamburgerMenu className="w-6 h-6" />} />
      <div className=" ">
        <NavButton route="/" name="Shop" />
      </div>
    </div>
  );
}

export default Menus;
