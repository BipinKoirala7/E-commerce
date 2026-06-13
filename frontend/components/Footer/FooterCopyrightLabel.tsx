import { Copyright } from "lucide-react";

function FooterCopyrightLabel() {
  return (
    <div className="w-full flex gap-1 opacity-50 items-center justify-center col-span-2">
      <p>Copyrights</p>
      <Copyright />
      <p>Bipin Koirala. All rights reserved.</p>
    </div>
  );
}

export default FooterCopyrightLabel;
