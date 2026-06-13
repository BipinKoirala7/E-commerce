import MensCollectionShowCase from "./MensCollectionShowCase";

function MensCollection() {
  return (
    <div className=" flex flex-col gap-4 p-6 bg-primary rounded-4xl">
      <p className="text-3xl header-font font-bold">Mens Collection</p>
      <div className="w-full min-h-122 h-full flex">
        <MensCollectionShowCase />
      </div>
    </div>
  );
}

export default MensCollection;
