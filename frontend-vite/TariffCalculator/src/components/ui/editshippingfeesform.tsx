"use client"

import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { FormProvider } from "react-hook-form";

const formSchema = z.object({
  id: z.string(),
  originCountry: z.string(),
  destinationCountry: z.string(),
  costPerTon: z.number().nonnegative(),
  costPerBarrel: z.number().nonnegative(),
  costPerMMBtu: z.number().nonnegative(),
  lastUpdated: z.string().optional(),
})

export type EditShippingFeeFormValues = z.infer<typeof formSchema>

interface EditShippingFeeFormProps {
  defaultValues: EditShippingFeeFormValues
  onSubmit: (values: EditShippingFeeFormValues) => Promise<void> | void
  onCancel?: () => void
  currentUserName: string
}

export function EditShippingFeeForm({
  defaultValues,
  onSubmit,
  onCancel,
  currentUserName,
}: EditShippingFeeFormProps) {
  const methods = useForm<EditShippingFeeFormValues>({
  resolver: zodResolver(formSchema),
  defaultValues,
});

const { 
    register, 
    handleSubmit, 
    formState: { errors, isSubmitting },
} = methods;

const handleFormSubmit = async (values: EditShippingFeeFormValues) => {
    const hasChanged = values.costPerTon !== defaultValues.costPerTon ||
                       values.costPerBarrel !== defaultValues.costPerBarrel ||
                       values.costPerMMBtu !== defaultValues.costPerMMBtu;
    const updatedValues = {
    ...values,
    lastUpdated: hasChanged ? new Date().toISOString().split('T')[0] : defaultValues.lastUpdated,
    };
    await onSubmit(updatedValues);
};

return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4 p-6">
        <h2 className="text-xl font-semibold">Edit Shipping Fee</h2>
  
        {/* Hidden ID field */}
        <input type="hidden" {...register("id")} />
  
        {/* Origin Country (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Origin Country
          </label>
          <Input
            {...register("originCountry")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Destination Country (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Destination Country
          </label>
          <Input
            {...register("destinationCountry")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>
  
        {/* Cost per Ton (editable) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Cost per Ton 
          </label>
          <Input
            {...register("costPerTon", { valueAsNumber: true })}
            type="number"
            step="0.01"
            className="w-full h-9 text-sm"
          />
          {errors.costPerTon && (
            <p className="text-xs text-red-600 mt-1">
              {errors.costPerTon.message}
            </p>
          )}
        </div>

        {/* Cost per Barrel (editable) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Cost per Barrel 
          </label>
          <Input
            {...register("costPerBarrel", { valueAsNumber: true })}
            type="number"
            step="0.01"
            className="w-full h-9 text-sm"
          />
          {errors.costPerBarrel && (
            <p className="text-xs text-red-600 mt-1">
              {errors.costPerBarrel.message}
            </p>
          )}
        </div>
      
        {/* Cost per MMBtu (editable) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Cost per MMBtu
          </label>
          <Input
            {...register("costPerMMBtu", { valueAsNumber: true })}
            type="number"
            step="0.01"
            className="w-full h-9 text-sm"
          />
          {errors.costPerMMBtu && (
            <p className="text-xs text-red-600 mt-1">
              {errors.costPerMMBtu.message}
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
