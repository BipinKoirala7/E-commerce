import NavButton from "@/components/ui/NavButton";

function NotLoggedInUserOptions() {
  return (
    <div className="flex gap-2">
      <NavButton route="/auth/login" name="Login" />
      <NavButton route="/auth/register" name="Sign Up" />
    </div>
  );
}

export default NotLoggedInUserOptions;
