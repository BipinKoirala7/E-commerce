import Link from "next/link";

function Logo() {
  return (
    <div className="w-full h-full flex items-center justify-center">
      <Link id="logo" href={"/"} className="text-4xl">
        Obsidian
      </Link>
    </div>
  );
}

export default Logo;
