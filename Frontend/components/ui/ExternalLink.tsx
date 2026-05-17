import {ReactNode} from "react";

type ExternalLinkT = {
  //    Link to external Path.(eg. https://youtube.com.)
  link: string;
  //    To either open in the same tab or a new one
  target?: string;
  //    Any additional styles if needed
  className?: string;
  children: ReactNode;
};

function ExternalLink({link, target, className, children}: ExternalLinkT) {
  return (
    <a
      href={link}
      //    If provided different, otherwise open in a new tab.
      target={target ? target : "_blank"}
      className={`p-1 cursor-pointer ` + className}
    >
      {children}
    </a>
  );
}

export default ExternalLink;
