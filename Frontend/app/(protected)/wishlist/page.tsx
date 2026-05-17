import WishList from "@/components/Wishlist/WishList";

function page() {
  return (
    <div className="flex flex-col items-center justify-center">
      <div className="min-h-60 w-1/2 bg-foreground flex flex-col gap-4 p-4 rounded-sm">
        <p className="text-2xl header-font">Wishlist</p>
        <div className="w-full h-full flex flex-col">
          <WishList />
        </div>
      </div>
    </div>
  );
}

export default page;
