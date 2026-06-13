import Image from "next/image";
import GreetingWatchImg from "@/public/Greeting_watch.webp";

import { ArrowUpRight } from "lucide-react";
import Link from "next/link";
import { NavButton } from "../ui/NavButton";

function Greeting() {
  return (
    <div className="h-full flex justify-between gap-20 p-6 bg-linear-to-r from-p via-transparent to-white rounded-4xl">
      <div className="flex flex-col justify-between">
        <div className="flex flex-col gap-2">
          <p className="text-8xl header-font font-bold">
            Browse the World&apos;s largest selection of Watches
          </p>
          <p className="text-3xl header-font font-bold">For Men and Women</p>
        </div>
        <div className="flex flex-col gap-4">
          <p className="text-1xl header-font font-bold">
            Discover the latest styles and trends
          </p>
          <NavButton
            route="/search"
            name="Shop Now"
            className="w-fit px-8 py-6 rounded-4xl flex items-center gap-2 bg-s text-t hover:text-p cursor-pointer hover:bg-green2 transition-colors"
          />
          {/* <Link
            href={`/search`}
            className="w-fit px-6 py-4 rounded-4xl flex items-center gap-2 bg-text text-background cursor-pointer"
          >
            <p>Shop Now</p>
            <ArrowUpRight />
          </Link> */}
        </div>
      </div>
      <Image
        src={GreetingWatchImg}
        alt="Greeting Watch"
        width={400}
        height={400}
        className="w-92"
      />
    </div>
  );
}

export default Greeting;
