import { AnimatePresence } from "motion/react";

type ModalProps = {
  className?: string;
  show: boolean;
  children: React.ReactNode;
};

function Modal({ className, show, children }: ModalProps) {
  return (
    <AnimatePresence>
      {show && (
        <div
          className={`absolute right-full top-full mt-2 z-10 min-w-20 bg-foreground border border-foreground p-2 rounded-sm shadow-xl/30 ring-1 ring-primary overflow-hidden ${className}`}
        >
          {children}
        </div>
      )}
    </AnimatePresence>
  );
}

export default Modal;
