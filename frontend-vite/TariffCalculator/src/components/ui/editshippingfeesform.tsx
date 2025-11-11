"use client"

import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, FormProvider } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

const formSchema = z.object({
  id: z.string(),
  originCountry: z.string(),
  originCountryIso3: z.string().min(2, "Missing ISO3 code"),  // ✅ new
  destinationCountry: z.string(),
  destinationCountryIso3: z.string().min(2, "Missing ISO3 code"), // ✅ new
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
  })

  const { register, handleSubmit, formState: { errors, isSubmitting } } = methods

  const handleFormSubmit = async (values: EditShippingFeeFormValues) => {
    const hasChanged =
      values.costPerTon !== defaultValues.costPerTon ||
      values.costPerBarrel !== defaultValues.costPerBarrel ||
      values.costPerMMBtu !== defaultValues.costPerMMBtu

    const today = new Date().toISOString().split("T")[0]
    const updatedValues = {
      ...values,
      lastUpdated: hasChanged ? today : defaultValues.lastUpdated,
    }

    console.log("Submitting form values →", updatedValues) // ✅ helpful debug log
    await onSubmit(updatedValues)
  }

  return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4 p-6">
        <h2 className="text-xl font-semibold">Edit Shipping Fee</h2>

        {/* Hidden ID + ISO3 fields */}
        <input type="hidden" {...register("id")} />
        <input type="hidden" {...register("originCountryIso3")} /> {/* ✅ */}
        <input type="hidden" {...register("destinationCountryIso3")} /> {/* ✅ */}

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

        {/* Editable cost fields */}
        <div className="flex flex-col">
          <label className="text-sm font-medium">Cost per Ton</label>
          <Input
            {...register("costPerTon", { valueAsNumber: true })}
            type="number"
            step="0.01"
            className="w-full h-9 text-sm"
          />
          {errors.costPerTon && <p className="text-xs text-red-600">{errors.costPerTon.message}</p>}
        </div>

        <div className="flex flex-col">
          <label className="text-sm font-medium">Cost per Barrel</label>
          <Input
            {...register("costPerBarrel", { valueAsNumber: true })}
            type="number"
            step="0.01"
            className="w-full h-9 text-sm"
          />
          {errors.costPerBarrel && <p className="text-xs text-red-600">{errors.costPerBarrel.message}</p>}
        </div>

        <div className="flex flex-col">
          <label className="text-sm font-medium">Cost per MMBtu</label>
          <Input
            {...register("costPerMMBtu", { valueAsNumber: true })}
            type="number"
            step="0.01"
            className="w-full h-9 text-sm"
          />
          {errors.costPerMMBtu && <p className="text-xs text-red-600">{errors.costPerMMBtu.message}</p>}
        </div>

        {/* Last Updated (display only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium">Last Updated</label>
          <Input
            value={new Date().toISOString().split("T")[0]}
            readOnly
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Buttons */}
        <div className="flex justify-end gap-2 pt-3">
          <Button
            variant="outline"
            type="button"
            onClick={() => onCancel?.()}
            className="hover:bg-gray-100 dark:hover:bg-gray-700"
          >
            Cancel
          </Button>
          <Button type="submit" disabled={isSubmitting} className="btn-slate">
            {isSubmitting ? "Saving..." : "Save"}
          </Button>
        </div>
      </form>
    </FormProvider>
  )
}
