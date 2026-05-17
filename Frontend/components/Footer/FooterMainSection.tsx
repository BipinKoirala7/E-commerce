import Socials from "./Socials";

function FooterMainSection() {
  return (
    <div className="flex flex-col gap-8">
      <div className="flex flex-col gap-2">
        <p id="logo" className="text-8xl">
          Obsidian
        </p>
        <p>Where exceptional craftsmanship meets timeless style.</p>
      </div>
      <Socials />
    </div>
  );
}

export default FooterMainSection;
