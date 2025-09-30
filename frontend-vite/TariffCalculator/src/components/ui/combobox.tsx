"use client"

import * as React from "react"
import { CheckIcon, ChevronsUpDownIcon } from "lucide-react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { FormField, FormItem, FormControl } from "@/components/ui/form";
import { useFormContext } from "react-hook-form";

export type Option = {
  value: string;
  label: string;
};

export const Combobox: React.FC<{
  value: string;
  onChange: (value: string) => void;
  options: Option[];
  placeholder?: string;
  className?: string;
  widthClass?: string;
  dropdownWidth?: string;
}> = ({ value, onChange, options, placeholder = "Select...", className, widthClass = "w-[200px]", dropdownWidth }) => {
  const [open, setOpen] = React.useState(false);
  const label = React.useMemo(
    () => options.find((o) => o.value === value)?.label ?? "",
    [options, value]
  );


  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className={cn(widthClass, "flex justify-between items-center", className)}
        >
          <span className="truncate">{label || placeholder}</span>
          <ChevronsUpDownIcon className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent 
        className={cn(dropdownWidth ?? widthClass, 
        "p-0 bg-white shadow-md rounded-md z-50"
        )}
        side="bottom"
        align="start"
        >
        <Command>
          <CommandInput placeholder={`Search `} />
          <CommandList>
            <CommandEmpty>No results.</CommandEmpty>
            <CommandGroup>
              {options.map((opt) => (
                <CommandItem
                  key={opt.value}
                  value={opt.value}
                  onSelect={(currentValue) => {
                    onChange(currentValue === value ? "" : currentValue)
                    setOpen(false);
                  }}
                  className="px-2 py-1 hover:bg-gray-100 rounded"
                >
                  <CheckIcon
                    className={cn(
                      "mr-2 h-4 w-4",
                      value === opt.value ? "opacity-100" : "opacity-0"
                    )}
                  />
                  {opt.label}
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
};

export const FormCombobox: React.FC<{
  name: string;
  options: Option[];
  placeholder?: string;
  widthClass?: string;
  dropdownWidth?: string; 
}> = ({ name, options, placeholder = "Select...", widthClass, dropdownWidth }) => {
  const { control } = useFormContext();

  return (
    <FormField
      control={control}
      name={name}
      render={({ field, fieldState }) => (
          <FormItem className="relative">
            <FormControl>
              <Combobox
                value={field.value ?? ""}
                onChange={(v) => field.onChange(v)}
                options={options}
                placeholder={placeholder}
                widthClass={widthClass}
                dropdownWidth={dropdownWidth}
                className={fieldState.error ? "border-red-500" : ""}
              />
            </FormControl>
          </FormItem>
  )}
    />
  )
}

export default Combobox