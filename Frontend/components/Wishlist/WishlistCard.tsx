import { WishListItem } from "@/types";
import Image from "next/image";
import IconButton from "../ui/IconButton";
import { TiMinus } from "react-icons/ti";
import { BsPatchPlus } from "react-icons/bs";
import { removeFromWishlist } from "@/lib/api/wishlist";

type CartSmallCardProps = {
  wishlistItem: WishListItem;
};

function WishlistCard({ wishlistItem: item }: CartSmallCardProps) {
  return (
    <div className="p-2 rounded-sm flex items-center space-x-4 overflow-hidden cursor-pointer hover:bg-primary smooth-transition">
      <div className="w-full flex items-center gap-4">
        <div className="flex items-center justify-center p-2 bg-background">
          <Image
            src={item.product.image.imageUrl}
            alt={item.product.name}
            width={400}
            height={400}
            className="w-60 aspect-4/3 rounded object-contain"
          />
        </div>
        <div className="w-full flex items-center justify-between">
          <div className="relative flex flex-col gap-1 overflow-hidden">
            <p className="text-3xl font-medium header-font">
              {item.product.brand}
            </p>
            <p className="text-1xl font-medium">{item.product.name}</p>
            <p className="text-sm text-muted-foreground">
              ${item.product.price.toFixed(2)}
            </p>
          </div>
          <div
            className={`h-full flex gap-2 p-1 items-center smooth-transition`}
          >
            <IconButton
              icon={<BsPatchPlus className="w-5 h-5" />}
              className="hover:bg-secondary"
            />
            <IconButton
              icon={<TiMinus className="w-5 h-5" />}
              className="hover:bg-secondary"
              onClick={() => {
                removeFromWishlist(item.id);
              }}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default WishlistCard;
