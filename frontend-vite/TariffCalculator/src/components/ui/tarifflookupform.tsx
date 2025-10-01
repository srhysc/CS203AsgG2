"use client";

import React from 'react';
import {z} from "zod";
import { zodResolver } from "@hookform/resolvers/zod"
import { FormProvider, useForm } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { InlineErrorInput } from "@/components/ui/inlineerrorinput";
import { FormCombobox } from "@/components/ui/combobox";
import type { Country } from "@/services/types/country";
import type { Petroleum } from "@/services/types/petroleum";


type LocalOption = { value: string; label: string };

//declare formschema for form
export const tariffSchema = z.object({
    importcountry: z.string().min(1, "Import country is required."),
    exportcountry:z.string().min(1, "Export country is required."),
    productcode:z.string().min(1, "A HS code is required."),
    units: z.string().min(1,"Please select at least 1 unit for calculation.")
})

type TariffFormProps = {
  onSubmit: (data: z.infer<typeof tariffSchema>) => void | Promise<void>;
  countries?: Country[] | null;
  petroleum?: Petroleum[] | null;
  clearSignal?: boolean;
  onClear?: () => void;
};

//TariffForm needs an onSubmit function, and will receive tariffSchema 
//Promise<void> promises to finish task - function can be sync or async, up to parent component
export function TariffForm({onSubmit, countries, petroleum, clearSignal, onClear} : TariffFormProps) {
  // 1. Define your form.
  const form = useForm<z.infer<typeof tariffSchema>>({
    resolver: zodResolver(tariffSchema),
    defaultValues: {
        importcountry: "",
        exportcountry:"",
        productcode:"",
        units: ""
    }});

    // reset form if clearSignal changes to true
    React.useEffect(() => {
    if (clearSignal) {
      form.reset(); // clears all input fields
    }
    }, [clearSignal, form]);
  
    const countryOptions: LocalOption[] = React.useMemo(
    () =>
      (countries ?? []).map((c) => ({
        label: c.name,
        value: c.name, // or use iso code if unique
      })),
    [countries]
  );

  const productOptions: LocalOption[] = React.useMemo(
    () =>
      (petroleum ?? []).map((p) => ({
        label: `${p.name} (${p.hsCode})`,
        value: String(p.hsCode),
      })),
    [petroleum]
  );

  function formSubmit(values:z.infer<typeof tariffSchema>){
    //send filled tariffSchema up to parent
    onSubmit(values);
  }

  return(
    <div className="flex flex-col items-center px-2">
      <div className="relative w-full max-w-md mb-4 flex justify-center">
        <h2 className="text-2xl font-bold">Calculate your Tariffs !</h2>
        <button
          type="button"
          onClick={() => {
            onClear?.();       // clears results in parent
            form.reset();      // resets input fields
          }}
          className="
            absolute right-0 top-0 
            w-8 h-8 
            bg-[#71869A] 
            text-white 
            font-bold 
            rounded 
            shadow-md 
            flex items-center justify-center 
            hover:bg-[#5a6a7c] 
            transition-colors duration-150
          "
        >
          C
        </button>
     </div>

        <FormProvider {...form}>
            {/* Form's submit function overriden by one above */}
            <form onSubmit={form.handleSubmit(formSubmit)} className="space-y-3 w-full max-w-md">

                {/* Grid of fields */}
                <div className="grid grid-cols-2 gap-3 items-center">

                {/* Importing Country */}
                <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Importing Country:</label>
                <div className="bg-white rounded-md p-2 shadow w-[195px]">
                    {/* <InlineErrorInput name="importcountry" placeholder="Select/Type to Add" />
                     */}
                      <FormCombobox
                        name="importcountry"
                        options={countryOptions}
                        placeholder="Select/Type to add"
                        widthClass="w-[180px]"    
                        dropdownWidth="w-[175px]"
                      />
                </div>

                {/* Exporting Country */}
                <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Exporting Country:</label>
                <div className="bg-white rounded-md p-2 shadow w-[195px]">
                    {/* <InlineErrorInput name="exportcountry" placeholder="Select/Type to Add" /> */}
                    <FormCombobox
                      name="exportcountry"
                      options={countryOptions}
                      placeholder="Select/Type to add"
                      widthClass="w-[180px]"    
                      dropdownWidth="w-[175px]"
                    />
                </div>

                {/* Product Code */}
                <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Product Code:</label>
                <div className="bg-white rounded-md p-2 shadow w-[195px]">
                         {/* <InlineErrorInput name="productcode" placeholder="HSXXX" /> */}
                         <FormCombobox
                          name="productcode"
                          options={productOptions}
                          placeholder="Select Code"
                          widthClass="w-[180px]"    
                          dropdownWidth="w-[175px]"
                        />
                </div>

                {/* Quantity */}
                <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Quantity (Barrels):</label>
                <div className="bg-white rounded-md p-2 shadow w-[195px]">
                          <InlineErrorInput name="units" placeholder="1" type="number" />
                </div>
            </div>
                {/* Submit button */}
                <Button
                    type="submit"
                    className="w-full bg-[#71869A] hover:bg-[#5a6a7c] text-white font-bold py-3 rounded-md shadow"
                >
                    CALCULATE
                </Button>
            </form>
        </FormProvider>
    </div>
  )

}