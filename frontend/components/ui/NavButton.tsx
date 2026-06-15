import Link from "next/link";
import { Button } from "@/components/ui/button";

type NavButtonT = {
  route: string;
  name: string;
  className?: string;
};

export function NavButton({ route, name, className }: NavButtonT) {
  return (
    <Button variant="ghost" className={`${className} w-fit text-[1rem]`}>
      <Link href={route}>{name}</Link>
    </Button>
  );
}
