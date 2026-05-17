import Button from "../ui/Button";

export default function CategoriesOptions() {
  return (
    <div className="flex gap-4 items-center">
      <Button name="All Products" />
      <Button name="Male" />
      <Button name="Female" />
    </div>
  );
}
