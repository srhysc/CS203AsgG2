import { Input } from "@/components/ui/input";
import { FormControl, FormField, FormItem } from "@/components/ui/form";
import { AlertCircle } from "lucide-react";
import { useFormContext } from "react-hook-form";
import { Tooltip, TooltipTrigger, TooltipContent } from "./tooltip";

type InlineErrorInputProps = {
  name: string;
  placeholder?: string;
  type?: string;
};

export function InlineErrorInput({ name, placeholder, type = "text" }: InlineErrorInputProps) {
  const { control } = useFormContext();

  return (
    <FormField
      control={control}
      name={name}
      render={({ field, fieldState }) => (
        <FormItem className="relative">
          <FormControl>
            <Input
              {...field}
              type={type}
              placeholder={placeholder}
              className={`pr-8 ${fieldState.error ? "border-red-500" : ""}`}
            />
          </FormControl>


          {/* Error icon with hover tooltip */}
          {fieldState.error && (
            <Tooltip>
              <TooltipTrigger asChild>
                <AlertCircle
                  className="absolute right-2 top-1/2 transform -translate-y-1/2 text-red-500 cursor-pointer"
                  size={18}
                />
              </TooltipTrigger>
              <TooltipContent className="bg-red-600 text-white text-xs rounded-md px-2 py-1">
                {fieldState.error.message}
              </TooltipContent>
            </Tooltip>
          )}
        </FormItem>
      )}
    />
  );
}
