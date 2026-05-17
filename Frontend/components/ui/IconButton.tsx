"use client";

type IconButtonT = {
  //  React Icons (expected)
  icon: React.ReactNode;
  //  Any additional classes for additional style
  className?: string;
  //  Function for onClick
  onClick?: () => void;
  //  Disables the button
  disabled?: boolean;
};

function IconButton(props: IconButtonT) {
  const { icon, className, onClick, disabled } = props;
  return (
    <button
      disabled={disabled}
      onClick={onClick}
      // Uses theme foreground color for hover state
      // Transition timing defined in globals.css (.smooth-transition)
      className={
        `flex items-center justify-center p-1.5 rounded-sm hover:bg-primary cursor-pointer smooth-transition ` +
        className
      }
    >
      {icon}
    </button>
  );
}

export default IconButton;
