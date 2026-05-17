import AccountOption from "./AccountOption";
import CartOption from "./CartOption";
import WishlistOption from "./WishlistOption";

function LoggedInUserOptions() {
  return (
    <div className="flex gap-3">
      <WishlistOption />
      <CartOption />
      <AccountOption />
    </div>
  );
}

export default LoggedInUserOptions;
