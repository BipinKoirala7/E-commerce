import OrderDetail from "@/components/Order/OrderDetail";

type Props = {
  params: {
    orderNumber: string;
  };
};

async function page({ params }: Props) {
  const { orderNumber } = await params;
  return (
    <div className="flex flex-col items-center justify-center py-10 px-4">
      <div className="w-full max-w-2xl flex flex-col gap-6">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight header-font">
            Order details
          </h1>
          <p className="text-sm text-muted-foreground mt-1">#{orderNumber}</p>
        </div>
        <OrderDetail orderNumber={orderNumber} />
      </div>
    </div>
  );
}

export default page;
