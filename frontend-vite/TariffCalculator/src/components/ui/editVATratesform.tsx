"use client"

import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { FormProvider } from "react-hook-form";


const formSchema = z.object({
  id: z.string(),
  country: z.string(),
  vatRate: z.number().min(0).max(1, "Use decimal: e.g., 0.07 for 7%"),
  lastUpdated: z.string().optional(),
})

export type EditVATRateFormValues = z.infer<typeof formSchema>

interface EditVATRateFormProps {
  defaultValues: EditVATRateFormValues;
  onSubmit: (values: EditVATRateFormValues) => Promise<void> | void;
  onCancel?: () => void;
  currentUserName: string;
}

export function EditVATRateForm({
  defaultValues,
  onSubmit,
  onCancel,
  currentUserName
}: EditVATRateFormProps) {
  const methods = useForm<EditVATRateFormValues>({
  resolver: zodResolver(formSchema),
  defaultValues,
});

const { register, handleSubmit, formState: { errors, isSubmitting } } = methods;
  

const handleFormSubmit = async (values: EditVATRateFormValues) => {
    const hasChanged = values.vatRate !== defaultValues.vatRate;
    const updatedValues = {
    ...values,
    lastUpdated: hasChanged ? new Date().toISOString().split('T')[0] : defaultValues.lastUpdated,
    };
    await onSubmit(updatedValues);
};
 
return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4 p-6">
        <h2 className="text-xl font-semibold">Edit VAT rates</h2>
  
        {/* Hidden ID field */}
        <input type="hidden" {...register("id")} />
  
        {/* Country (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Country
          </label>
          <Input
            {...register("country")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>
  
        {/* VAT Rate (editable) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            VAT Rate (decimal, e.g., 0.05 for 5%)
          </label>
          <Input
            {...register("vatRate", { valueAsNumber: true })}
            type="number"
            step="0.01"
            className="w-full h-9 text-sm"
          />
          {errors.vatRate && (
            <p className="text-xs text-red-600 mt-1">
              {errors.vatRate.message}
            </p>
          )}
        </div>
  
        {/* Last Updated (preview) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Last Updated (will be set to today)
          </label>
          <Input
            value={new Date().toISOString().split('T')[0]}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Action buttons */}
        <div className="flex justify-end gap-2 pt-3">
          <Button variant="outline" type="button" onClick={() => onCancel?.()}
              className="hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors cursor-pointer"
          >
            Cancel
          </Button>
          <Button
              type="submit"
              disabled={isSubmitting}
              className="btn-slate cursor-pointer"        
          >
            {isSubmitting ? "Saving..." : "Save"}
          </Button>
        </div>
      </form>
    </FormProvider>
  );
}
