import { Button } from "@/components/ui/button";

function NotLoggedInUserOptions() {
  return (
    <div className="flex gap-2">
      <Button
      // route="/auth/login" name="Login"
      />
      <Button
      // route="/auth/register" name="Sign Up"
      />
    </div>
  );
}

export default NotLoggedInUserOptions;
