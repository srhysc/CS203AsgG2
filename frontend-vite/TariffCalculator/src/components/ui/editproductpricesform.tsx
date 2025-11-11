"use client"

import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, FormProvider } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

const formSchema = z.object({
  id: z.string(),
  productCode: z.string(),
  productName: z.string(),
  price: z.number().nonnegative(),
  lastUpdated: z.string().optional(),
})

export type EditProductPriceFormValues = z.infer<typeof formSchema>

interface EditProductPriceFormProps {
  defaultValues: EditProductPriceFormValues
  onSubmit: (values: EditProductPriceFormValues) => Promise<void> | void
  onCancel?: () => void
}

export function EditProductPriceForm({ defaultValues, onSubmit, onCancel }: EditProductPriceFormProps) {
    const methods = useForm<EditProductPriceFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues,
});

const { register, handleSubmit, formState: { errors, isSubmitting } } = methods;

const handleFormSubmit = async (values: EditProductPriceFormValues) => {
const hasChanged = values.price !== defaultValues.price
const updatedValues = {
    ...values,
    lastUpdated: hasChanged ? new Date().toISOString().split("T")[0] : defaultValues.lastUpdated,
    };
    await onSubmit(updatedValues);
};

return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4 p-6">

        <input type="hidden" {...register("id")} />

        {/* Product Code (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Product Code
          </label>
          <Input
            {...register("productCode")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Product Name (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Product Name
          </label>
          <Input
            {...register("productName")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Price (editable) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Price 
          </label>
          <Input
            {...register("price", { valueAsNumber: true })}
            type="number"
            step="0.01"
            className="w-full h-9 text-sm"
          />
          {errors.price && (
            <p className="text-xs text-red-600 mt-1">
              {errors.price.message}
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
  )
}
