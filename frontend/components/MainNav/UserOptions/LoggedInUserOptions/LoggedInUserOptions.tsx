import AccountOption from "./AccountOption";
import CartOption from "./CartOption";

function LoggedInUserOptions() {
  return (
    <div className="flex gap-3">
      {/* <CartOption /> */}
      <AccountOption />
    </div>
  );
}

export default LoggedInUserOptions;
