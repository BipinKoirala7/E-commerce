import FooterLinks from "./FooterLinks";
import FooterMainSection from "./FooterMainSection";
import FooterCopyrightLabel from "./FooterCopyrightLabel";

/**
 * Main Footer of the Application
 */
function Footer() {
  return (
    <div className="w-full bg-text text-background px-8 pt-16 pb-4 grid grid-cols-2 gap-8">
      <FooterMainSection />
      <FooterLinks />
      <FooterCopyrightLabel />
    </div>
  );
}

export default Footer;
