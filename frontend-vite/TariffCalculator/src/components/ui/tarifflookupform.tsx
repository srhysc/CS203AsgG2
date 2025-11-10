// "use client";

// import React from 'react';
// import { z } from "zod";
// import { zodResolver } from "@hookform/resolvers/zod"
// import { FormProvider, useForm } from "react-hook-form"
// import { Button } from "@/components/ui/button"
// import { InlineErrorInput } from "@/components/ui/inlineerrorinput";
// import { FormCombobox } from "@/components/ui/combobox";
// import { DatePicker } from "@/components/ui/date-picker";
// import type { Country } from "@/services/types/country";
// import type { Petroleum } from "@/services/types/petroleum";


// type LocalOption = { value: string; label: string };

// //declare formschema for form
// export const tariffSchema = z.object({
//   importcountry: z.string().min(1, "Import country is required."),
//   exportcountry: z.string().min(1, "Export country is required."),
//   productcode: z.string().min(1, "A HS code is required."),
//   units: z.string().min(1, "Please select at least 1 unit for calculation."),
//   date: z.date().optional(),
// })

// type TariffFormProps = {
//   onSubmit: (data: z.infer<typeof tariffSchema>) => void | Promise<void>;
//   countries?: Country[] | null;
//   petroleum?: Petroleum[] | null;
//   clearSignal?: boolean;
//   onClear?: () => void;
// };

// //TariffForm needs an onSubmit function, and will receive tariffSchema 
// //Promise<void> promises to finish task - function can be sync or async, up to parent component
// export function TariffForm({ onSubmit, countries, petroleum, clearSignal, onClear }: TariffFormProps) {
//   // 1. Define your form.
//   const form = useForm<z.infer<typeof tariffSchema>>({
//     resolver: zodResolver(tariffSchema),
//     defaultValues: {
//       importcountry: "",
//       exportcountry: "",
//       productcode: "",
//       units: "",
//       date: new Date(), // default to current date
//     }
//   });

//   // reset form if clearSignal changes to true
//   React.useEffect(() => {
//     if (clearSignal) {
//       form.reset(); // clears all input fields
//     }
//   }, [clearSignal, form]);

//   const countryOptions: LocalOption[] = React.useMemo(
//     () =>
//       (countries ?? []).map((c) => ({
//         label: c.name,
//         value: c.name, // or use iso code if unique
//       })),
//     [countries]
//   );

//   const productOptions: LocalOption[] = React.useMemo(
//     () =>
//       (petroleum ?? []).map((p) => ({
//         label: `${p.name} (${p.hsCode})`,
//         value: String(p.hsCode),
//       })),
//     [petroleum]
//   );

//   function formSubmit(values: z.infer<typeof tariffSchema>) {
//     //send filled tariffSchema up to parent
//     onSubmit(values);
//   }

//   return (
//     <div className="flex flex-col items-center px-2">
//       <div className="relative w-full max-w-md mb-4 flex justify-center">
//         <h2 className="text-2xl font-bold">Calculate your Tariffs !</h2>
//         <button
//           type="button"
//           onClick={() => {
//             onClear?.();       // clears results in parent
//             form.reset();      // resets input fields
//           }}
//           className="
//             absolute right-0 top-0 
//             w-8 h-8 
//             bg-[#71869A] 
//             text-white 
//             font-bold 
//             rounded 
//             shadow-md 
//             flex items-center justify-center 
//             hover:bg-[#5a6a7c] 
//             transition-colors duration-150
//           "
//         >
//           C
//         </button>
//       </div>

//       <FormProvider {...form}>
//         {/* Form's submit function overriden by one above */}
//         <form onSubmit={form.handleSubmit(formSubmit)} className="space-y-3 w-full max-w-md">

//           {/* Grid of fields */}
//           <div className="grid grid-cols-2 gap-3 items-center">

//             {/* Importing Country */}
//             <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Importing Country:</label>
//             <div className="bg-white rounded-md p-2 shadow w-[195px]">
//               {/* <InlineErrorInput name="importcountry" placeholder="Select/Type to Add" />
//                      */}
//               <FormCombobox
//                 name="importcountry"
//                 options={countryOptions}
//                 placeholder="Select/Type to add"
//                 widthClass="w-[180px]"
//                 dropdownWidth="w-[175px]"
//               />
//             </div>

//             {/* Exporting Country */}
//             <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Exporting Country:</label>
//             <div className="bg-white rounded-md p-2 shadow w-[195px]">
//               {/* <InlineErrorInput name="exportcountry" placeholder="Select/Type to Add" /> */}
//               <FormCombobox
//                 name="exportcountry"
//                 options={countryOptions}
//                 placeholder="Select/Type to add"
//                 widthClass="w-[180px]"
//                 dropdownWidth="w-[175px]"
//               />
//             </div>

//             {/* Product Code */}
//             <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Product Code:</label>
//             <div className="bg-white rounded-md p-2 shadow w-[195px]">
//               {/* <InlineErrorInput name="productcode" placeholder="HSXXX" /> */}
//               <FormCombobox
//                 name="productcode"
//                 options={productOptions}
//                 placeholder="Select Code"
//                 widthClass="w-[180px]"
//                 dropdownWidth="w-[175px]"
//               />
//             </div>

//             {/*Date Picker*/}
//             <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">
//               Date:
//             </label>
//             <div className="bg-white rounded-md p-2 shadow w-[195px]">
//               <div className="bg-white rounded-md">
//                 <DatePicker
//                   value={form.watch("date")}
//                   onChange={(date) => form.setValue("date", date ?? new Date())}
//                   className="w-[180px]"
//                   buttonClassName="
//                   w-full text-left bg-white border-1 border-black
//                   rounded-md h-[38px] px-3 text-gray-700 
//                   focus:outline-none focus:ring-2 focus:ring-[#71869A]
//                   hover:border-[#5a6a7c]
//                   flex items-center gap-0
//                   truncate
//                   "
//                   popoverClassName="rounded-md border-2 border-[#71869A] bg-white shadow-md"
//                 />
//               </div>
//             </div>

//             {/* Quantity */}
//             <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Quantity (Barrels):</label>
//             <div className="bg-white rounded-md p-2 shadow w-[195px]">
//               <InlineErrorInput name="units" placeholder="1" type="number" />
//             </div>
//           </div>
//           {/* Submit button */}
//           <Button
//             type="submit"
//             className="w-full bg-[#71869A] hover:bg-[#5a6a7c] text-white font-bold py-3 rounded-md shadow"
//           >
//             CALCULATE
//           </Button>
//         </form>
//       </FormProvider>
//     </div>
//   )

// }"use client";
"use client";

import React from 'react';
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { FormProvider, useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Search, Calculator, Loader2, Calendar, ChevronLeft, ChevronRight } from "lucide-react";
import type { Country } from "@/services/types/country";
import type { Petroleum } from "@/services/types/petroleum";

const CalendarView = ({ date, onDateChange }: { date: Date, onDateChange: (date: Date) => void }) => {
  const [viewDate, setViewDate] = React.useState(date);
  
  // Get the first day of the month and number of days
  const firstDay = new Date(viewDate.getFullYear(), viewDate.getMonth(), 1);
  const lastDay = new Date(viewDate.getFullYear(), viewDate.getMonth() + 1, 0);
  const daysInMonth = lastDay.getDate();
  const startDay = firstDay.getDay();
  
  // Create calendar grid
  const days = Array.from({ length: 42 }, (_, i) => {
    const dayNumber = i - startDay + 1;
    if (dayNumber < 1 || dayNumber > daysInMonth) return null;
    return new Date(viewDate.getFullYear(), viewDate.getMonth(), dayNumber);
  });

  return (
    <div className="p-4 bg-slate-800 rounded-lg border border-slate-700 w-[350px]">
      <div className="flex items-center justify-between mb-4">
        <Button 
          variant="ghost" 
          size="icon"
          onClick={() => setViewDate(new Date(viewDate.getFullYear(), viewDate.getMonth() - 1))}
          className="text-gray-100 hover:text-[#dcff1a] hover:bg-slate-700"
        >
          <ChevronLeft className="h-5 w-5" />
        </Button>
        <div className="text-lg font-semibold text-gray-100">
          {viewDate.toLocaleString('default', { month: 'long', year: 'numeric' })}
        </div>
        <Button 
          variant="ghost" 
          size="icon"
          onClick={() => setViewDate(new Date(viewDate.getFullYear(), viewDate.getMonth() + 1))}
          className="text-gray-100 hover:text-[#dcff1a] hover:bg-slate-700"
        >
          <ChevronRight className="h-5 w-5" />
        </Button>
      </div>
      <div className="grid grid-cols-7 gap-1 mb-2">
        {['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'].map((day) => (
          <div key={day} className="text-[#dcff1a] font-medium text-center py-2 text-sm">
            {day}
          </div>
        ))}
      </div>
      <div className="grid grid-cols-7 gap-1">
        {days.map((day, i) => (
          <Button
            key={i}
            variant="ghost"
            disabled={!day}
            onClick={() => day && onDateChange(day)}
            className={`
              h-10 w-10 p-0 font-normal text-base
              ${!day ? 'invisible' : ''}
              ${day?.toDateString() === date?.toDateString() 
                ? 'bg-[#dcff1a] text-slate-900 hover:bg-[#dcff1a]/90' 
                : 'text-gray-100 hover:bg-slate-700 hover:text-[#dcff1a]'}
            `}
          >
            {day?.getDate()}
          </Button>
        ))}
      </div>
    </div>
  );
};

export const tariffSchema = z.object({
  importcountry: z.string().min(1, "Import country is required."),
  exportcountry: z.string().min(1, "Export country is required."),
  productcode: z.string().min(1, "A HS code is required."),
  units: z.string().min(1, "Please select at least 1 unit for calculation."),
  date: z.date().optional(),
});

type TariffFormProps = {
  onSubmit: (data: z.infer<typeof tariffSchema>) => void | Promise<void>;
  countries?: Country[] | null;
  petroleum?: Petroleum[] | null;
  clearSignal?: boolean;
  onClear?: () => void;
  loading?: boolean;
};

const SelectField = ({
  label,
  value,
  onChange,
  options,
  error,
  placeholder,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: { label: string; value: string }[];
  error?: string;
  placeholder?: string;
}) => (
  <div className="space-y-2">
    <label className="text-sm font-medium text-gray-200">{label}</label>
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          className={`w-full h-10 px-3 py-2 text-base flex items-center justify-between
            bg-slate-800 border-slate-700 text-gray-100 
            hover:bg-slate-700 hover:border-[#dcff1a] transition-colors
            ${error ? "border-red-500" : ""}`}
        >
          {value || placeholder || `Select ${label.toLowerCase()}...`}
          <Search className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent 
        className="w-[300px] p-0 bg-slate-800 border-slate-700"
        align="start"
      >
        <Command className="bg-transparent">
          <CommandInput
            placeholder={`Search ${label.toLowerCase()}...`}
            className="text-gray-100 placeholder:text-gray-400"
          />
          <CommandEmpty className="py-2 text-gray-400">No results found.</CommandEmpty>
          <CommandGroup className="max-h-[200px] overflow-auto">
            {options.map((option) => (
              <CommandItem
                key={option.value}
                value={option.label}
                onSelect={() => onChange(option.value)}
                className="text-base py-2 text-gray-100 
                  hover:bg-slate-700 hover:text-[#dcff1a] 
                  cursor-pointer"
              >
                {option.label}
              </CommandItem>
            ))}
          </CommandGroup>
        </Command>
      </PopoverContent>
    </Popover>
    {error && <p className="text-sm text-red-400">{error}</p>}
  </div>
);

export function TariffForm({ onSubmit, countries, petroleum, clearSignal, onClear, loading }: TariffFormProps) {
  const form = useForm<z.infer<typeof tariffSchema>>({
    resolver: zodResolver(tariffSchema),
    defaultValues: {
      importcountry: "",
      exportcountry: "",
      productcode: "",
      units: "",
      date: new Date(),
    }
  });

  React.useEffect(() => {
    if (clearSignal) {
      form.reset();
    }
  }, [clearSignal, form]);

  const countryOptions = React.useMemo(
    () => (countries ?? []).map((c) => ({
      label: c.name,
      value: c.name,
    })),
    [countries]
  );

  const productOptions = React.useMemo(
    () => (petroleum ?? []).map((p) => ({
      label: `${p.name} (${p.hsCode})`,
      value: String(p.hsCode),
    })),
    [petroleum]
  );

  return (
    <div className="w-full max-w-4xl mx-auto">
      <FormProvider {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <SelectField
              label="Importing Country"
              value={form.watch("importcountry")}
              onChange={(value) => form.setValue("importcountry", value)}
              options={countryOptions}
              error={form.formState.errors.importcountry?.message}
              placeholder="Select importing country..."
            />
            <SelectField
              label="Exporting Country"
              value={form.watch("exportcountry")}
              onChange={(value) => form.setValue("exportcountry", value)}
              options={countryOptions}
              error={form.formState.errors.exportcountry?.message}
              placeholder="Select exporting country..."
            />
            <SelectField
              label="Product Code"
              value={form.watch("productcode")}
              onChange={(value) => form.setValue("productcode", value)}
              options={productOptions}
              error={form.formState.errors.productcode?.message}
              placeholder="Select HS code..."
            />
            <div className="space-y-2">
              <label className="text-sm font-medium text-gray-200">Quantity</label>
              <Input
                type="number"
                {...form.register("units")}
                className="h-10 bg-slate-800 border-slate-700 
                  text-gray-100 placeholder:text-gray-400
                  hover:border-[#dcff1a] transition-colors"
                placeholder="Enter quantity in barrels..."
              />
              {form.formState.errors.units && (
                <p className="text-sm text-red-400">{form.formState.errors.units.message}</p>
              )}
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-gray-200">Date (Optional)</label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    className="w-full h-10 px-3 py-2 text-base flex items-center justify-between
                      bg-slate-800 border-slate-700 text-gray-100 
                      hover:bg-slate-700 hover:border-[#dcff1a] transition-colors"
                  >
                    {form.watch("date")?.toLocaleDateString() || "Pick a date"}
                    <Calendar className="ml-2 h-4 w-4 opacity-50" />
                  </Button>
                </PopoverTrigger>
                <PopoverContent align="start" className="p-0">
                  <CalendarView
                    date={form.watch("date") || new Date()}
                    onDateChange={(date) => form.setValue("date", date)}
                  />
                </PopoverContent>
              </Popover>
            </div>
          </div>

          <div className="flex justify-end gap-4">
            <Button
              type="button"
              variant="outline"
              onClick={() => {
                onClear?.();
                form.reset();
              }}
              className="px-4 py-2 bg-slate-800 border-slate-700 
                text-gray-100 hover:bg-slate-700
                hover:border-[#dcff1a] transition-colors"
            >
              Clear
            </Button>
            <Button
              type="submit"
              disabled={loading}
              className="px-4 py-2 bg-[#dcff1a] text-slate-900 
                hover:bg-[#dcff1a]/90 transition-colors
                flex items-center gap-2"
            >
              {loading ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Calculator className="h-4 w-4" />
              )}
              Calculate
            </Button>
          </div>
        </form>
      </FormProvider>
    </div>
  );
}