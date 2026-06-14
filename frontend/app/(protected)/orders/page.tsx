import OrderList from "@/components/Order/OrderList";

function page() {
  return (
    <div className="flex flex-col items-center justify-center py-10 px-4">
      <div className="w-full max-w-3xl flex flex-col gap-6">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight header-font">
            Orders
          </h1>
          <p className="text-sm text-muted-foreground mt-1">
            View and track your order history
          </p>
        </div>
        <OrderList />
      </div>
    </div>
  );
}

export default page;
