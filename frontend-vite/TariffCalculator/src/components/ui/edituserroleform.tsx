"use client"

import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, Controller, FormProvider } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"


const formSchema = z.object({
  id: z.string(),
  username: z.string(),
  email: z.string(),
  currentRole: z.enum(["USER", "ADMIN"]),
})

export type EditUserRoleFormValues = z.infer<typeof formSchema>

interface EditUserRoleFormProps {
  defaultValues: EditUserRoleFormValues
  onSubmit: (values: EditUserRoleFormValues) => Promise<void> | void
  onCancel?: () => void
}

export function EditUserRoleForm({
  defaultValues,
  onSubmit,
  onCancel,
}: EditUserRoleFormProps) {
  const methods = useForm<EditUserRoleFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues,
  })

  const {
    register,
    handleSubmit,
    control,
    formState: { isSubmitting },
  } = methods

  return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 p-6">
        <h2 className="text-xl font-semibold">Edit User Role</h2>

        <input type="hidden" {...register("id")} />

        <div className="flex flex-col">
          <label className="text-sm font-medium mb-1">Username</label>
          <Input
            {...register("username")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        <div className="flex flex-col">
          <label className="text-sm font-medium mb-1">Email</label>
          <Input
            {...register("email")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>


        <div className="flex flex-col">
          <label className="text-sm font-medium mb-1">Current Role</label>
          <Controller
            name="currentRole"
            control={control}
            render={({ field }) => (
              <Select value={field.value} onValueChange={field.onChange}>
                <SelectTrigger className="w-full h-9 text-sm bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-700 rounded-md shadow-sm text-gray-900 dark:text-gray-100">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-700 shadow-lg rounded-md text-gray-900 dark:text-gray-100">
                  <SelectItem
                    value="USER"
                    disabled={defaultValues.currentRole === "USER"}
                    className={defaultValues.currentRole === "USER" ? "cursor-not-allowed opacity-50" : ""}
                  >
                    USER
                  </SelectItem>
                  <SelectItem
                    value="ADMIN"
                    disabled={defaultValues.currentRole === "ADMIN"}
                    className={defaultValues.currentRole === "ADMIN" ? "cursor-not-allowed opacity-50" : ""}
                  >
                    ADMIN
                  </SelectItem>
                </SelectContent>
              </Select>
            )}
          />
        </div>

        <div className="flex justify-end gap-2 pt-3">
          <Button type="button" variant="outline" onClick={onCancel}>
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
