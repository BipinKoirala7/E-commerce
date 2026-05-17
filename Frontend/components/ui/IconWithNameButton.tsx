"use client";

type IconWithNameButtonT = {
  //  React icons icon is expected
  icon: React.ReactNode;
  //  Place holder for icon or button Action
  name: string;
  //  Function to be called when button is clicked
  onClick?: () => void;
};

export default function IconWithNameButton({
  icon,
  name,
  onClick,
}: IconWithNameButtonT) {
  return (
    <button
      onClick={onClick}
      className="flex gap-2 items-center px-2 py-1 hover:bg-foreground rounded-sm cursor-pointer smooth-transition"
    >
      {icon}
      {name}
    </button>
  );
}
