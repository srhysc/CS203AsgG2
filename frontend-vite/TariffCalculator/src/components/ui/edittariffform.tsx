"use client";

import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, Controller } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { FormProvider } from "react-hook-form";


const formSchema = z.object({
  id: z.string(),
  productCode: z.string().min(1, "Product Code is required"),
  importingCountry: z.string().min(1, "Importing Country is required"),
  exportingCountry: z.string().min(1, "Exporting Country is required"),
  tariffRate: z.number().nonnegative("Tariff Rate must be a positive number"),
  lastUpdated: z.string().optional(),
  updatedBy: z.string().optional(),
});

export type EditTariffFormValues = z.infer<typeof formSchema>;

interface EditTariffFormProps {
  defaultValues: EditTariffFormValues;
  onSubmit: (values: EditTariffFormValues) => Promise<void> | void;
  onCancel?: () => void;
  currentUserName: string;
  countryOptions: { label: string; value: string }[];
}

export function EditTariffForm({
  defaultValues,
  onSubmit,
  onCancel,
  currentUserName,
  countryOptions,
}: EditTariffFormProps) {
  const methods = useForm<EditTariffFormValues>({
  resolver: zodResolver(formSchema),
  defaultValues,
});

const {
  register,
  handleSubmit,
  control,
  formState: { errors, isSubmitting },
} = methods;
  

  const handleFormSubmit = async (values: EditTariffFormValues) => {
    const hasChanged = values.tariffRate !== defaultValues.tariffRate;
    const updatedValues = {
    ...values,
    lastUpdated: hasChanged ? new Date().toISOString().split('T')[0] : defaultValues.lastUpdated,
    updatedBy: hasChanged ? currentUserName : defaultValues.updatedBy,
    };
    await onSubmit(updatedValues);
  };

  return (
    <FormProvider {...methods}>
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4 p-6">
      <h2 className="text-xl font-semibold mb-4">Edit Tariff</h2>

      {/* Hidden ID field */}
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

      {/* Exporting Country (read-only) */}
      <div className="flex flex-col">
        <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
          Exporting Country
        </label>
        <Input
          {...register("exportingCountry")}
          readOnly
          tabIndex={-1}
          className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
        />
      </div>

      {/* Importing Country (read-only) */}
      <div className="flex flex-col">
        <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
          Importing Country
        </label>
        <Input
          {...register("importingCountry")}
          readOnly
          tabIndex={-1}
          className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
        />
      </div>

      {/* Tariff Rate (editable) */}
      <div className="flex flex-col">
        <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
          Tariff Rate (decimal, e.g., 0.05 for 5%)
        </label>
        <Input
          {...register("tariffRate", { valueAsNumber: true })}
          type="number"
          step="0.0001"
          className="w-full h-9 text-sm"
        />
        {errors.tariffRate && (
          <p className="text-xs text-red-600 mt-1">
            {errors.tariffRate.message}
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

      {/* Updated By (preview) */}
      <div className="flex flex-col">
        <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
          Updated By
        </label>
        <Input
          value={currentUserName}
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