import Search from "./Search";
import UserOptions from "./UserOptions/UserOptions";
import Logo from "./Logo";
import Menus from "./Menus";

const MainNav = () => {
  return (
    <div className="w-full h-16 grid grid-cols-3 items-center px-8 text-text border-b border-b-foreground">
      <Menus />
      <Logo />
      <div className="h-full flex justify-end gap-4 items-center">
        <Search />
        <UserOptions />
      </div>
    </div>
  );
};

export default MainNav;
