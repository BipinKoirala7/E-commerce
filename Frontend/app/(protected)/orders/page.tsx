import OrderList from "@/components/Order/OrderList";

function page() {
  return (
    <div className="flex flex-col items-center justify-center">
      <div className="w-2/3 bg-foreground flex flex-col gap-4 p-4 rounded-sm">
        <p className="text-2xl header-font">Orders</p>
        <OrderList />
      </div>
    </div>
  );
}

export default page;
