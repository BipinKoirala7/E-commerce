import React from "react";
import ExternalLink from "../ui/ExternalLink";
import { FaFacebook, FaInstagram, FaLinkedin, FaTwitter } from "react-icons/fa";

function Socials() {
  return (
    <div className="flex gap-6">
      <ExternalLink link="https://x.com" className="hover:text-blue-400">
        <FaTwitter className="text-[1.5rem] smooth-transition" />
      </ExternalLink>
      <ExternalLink
        link="https://instagram.com"
        className="hover:text-pink-500"
      >
        <FaInstagram className="text-[1.5rem] smooth-transition" />
      </ExternalLink>
      <ExternalLink link="https://facebook.com" className="hover:text-blue-400">
        <FaFacebook className="text-[1.5rem]  smooth-transition" />
      </ExternalLink>
      <ExternalLink link="https://linkedin.com" className="hover:text-blue-400">
        <FaLinkedin className="text-[1.5rem] smooth-transition" />
      </ExternalLink>
    </div>
  );
}

export default Socials;
