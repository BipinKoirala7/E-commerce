import Greeting from "@/components/Home/Greeting";
import MensCollection from "@/components/Home/MensCollection";
import WomenCollection from "@/components/Home/WomenCollection";

export default function Home() {
  return (
    <div className="w-full min-h-full flex flex-col gap-4 px-8 pt-2 pb-16">
      <Greeting />
      <MensCollection />
      <WomenCollection />
    </div>
  );
}
