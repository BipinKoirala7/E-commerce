function FooterLinks() {
  return (
    <div className="flex justify-evenly">
      <div className="flex flex-col gap-4">
        <p className="text-2xl font-bold">Discover</p>
        <div className="flex flex-col gap-2">
          <p className="cursor-pointer hover:underline">Buy</p>
          <p className="cursor-pointer hover:underline">Sell</p>
          <p className="cursor-pointer hover:underline">Giving Back</p>
          <p className="cursor-pointer hover:underline">Support</p>
        </div>
      </div>
      <div className="flex flex-col gap-4">
        <p className="text-2xl font-bold">About</p>
        <div className="flex flex-col gap-2">
          <p className="cursor-pointer hover:underline">Careers</p>
          <p className="cursor-pointer hover:underline">Team</p>
          <p className="cursor-pointer hover:underline">Blog</p>
          <p className="cursor-pointer hover:underline">Press</p>
        </div>
      </div>
      <div className="flex flex-col gap-4">
        <p className="text-2xl font-bold">Legal</p>
        <div className="flex flex-col gap-2">
          <p className="cursor-pointer hover:underline">Privacy Policy</p>
          <p className="cursor-pointer hover:underline">Terms of Service</p>
          <p className="cursor-pointer hover:underline">Contact</p>
        </div>
      </div>
    </div>
  );
}

export default FooterLinks;
