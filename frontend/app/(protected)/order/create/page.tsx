import CreateOrderForm from "@/components/Order/CreateOrderForm";

function page() {
  return (
    <div className="flex flex-col items-center justify-center py-10 px-4">
      <div className="w-full max-w-2xl flex flex-col gap-6">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight header-font">
            Place Order
          </h1>
          <p className="text-sm text-muted-foreground mt-1">
            Confirm your details and review selected items
          </p>
        </div>
        <CreateOrderForm />
      </div>
    </div>
  );
}

export default page;
