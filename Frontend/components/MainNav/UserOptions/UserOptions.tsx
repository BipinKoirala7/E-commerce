"use client";

import { useUser } from "@/Context/UserProvider";
import LoggedInUserOptions from "./LoggedInUserOptions/LoggedInUserOptions";
import NotLoggedInUserOptions from "./NotLoggedInUserOptions/NotLoggedInUserOptions";

function UserOptions() {
  const { isAuthenticated } = useUser();
  return isAuthenticated() ? (
    <LoggedInUserOptions />
  ) : (
    <NotLoggedInUserOptions />
  );
}
export default UserOptions;
