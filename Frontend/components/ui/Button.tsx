type ButtonT = {
  //  Display Text for Button
  name: string;
  //  Additional classes for additional styling
  className?: string;
  //  onClick function for button
  onClick?: () => void;
};

export default function Button({ name, className, onClick }: ButtonT) {
  return (
    <button
      onClick={onClick}
      //  Uses smooth transition defined in globals.css (.smooth-transition)
      className={`${className} flex items-center justify-center px-4 py-1 rounded-sm hover:bg-foreground cursor-pointer smooth-transition`}
    >
      {name}
    </button>
  );
}
