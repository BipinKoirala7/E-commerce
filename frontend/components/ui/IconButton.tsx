import { Button } from "@/components/ui/button";

type IconButtonT = {
  icon: React.ReactNode;
  className?: string;
  onClick?: () => void;
  disabled?: boolean;
};

export function IconButton({
  icon,
  className,
  onClick,
  disabled,
}: IconButtonT) {
  return (
    <Button
      variant="ghost"
      size="icon"
      className={className}
      onClick={onClick}
      disabled={disabled}
    >
      {icon}
    </Button>
  );
}
