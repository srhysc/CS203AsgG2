"use client"

import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { FormProvider } from "react-hook-form"
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
  previousRole: z.enum(["user", "admin"]),
  currentRole: z.enum(["user", "admin"]),
  createdOn: z.string(),
  lastLogin: z.string().optional(),
  updatedAt: z.string().optional(),
})

export type EditUserRoleFormValues = z.infer<typeof formSchema>

interface EditUserRoleFormProps {
  defaultValues: EditUserRoleFormValues
  onSubmit: (values: EditUserRoleFormValues) => Promise<void> | void
  onCancel?: () => void
  currentUserName: string
}

export function EditUserRoleForm({
  defaultValues,
  onSubmit,
  onCancel,
  currentUserName,
}: EditUserRoleFormProps) {
  const methods = useForm<EditUserRoleFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues,
  })

  const { 
    register, 
    handleSubmit, 
    watch,
    setValue,
    formState: { errors, isSubmitting },
  } = methods

  const currentRole = watch("currentRole")

  const handleFormSubmit = async (values: EditUserRoleFormValues) => {
    const hasChanged = values.currentRole !== defaultValues.currentRole

    const updatedValues = {
      ...values,
      previousRole: defaultValues.currentRole, // Current becomes previous
      updatedAt: hasChanged ? new Date().toISOString().split('T')[0] : defaultValues.updatedAt,
    }
    
    await onSubmit(updatedValues)
  }

  return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4 p-6">
        <h2 className="text-xl font-semibold">Edit User Role</h2>

        {/* Hidden ID field */}
        <input type="hidden" {...register("id")} />

        {/* Username (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Username
          </label>
          <Input
            {...register("username")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Email (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Email
          </label>
          <Input
            {...register("email")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Previous Role (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Previous Role
          </label>
          <Input
            value={defaultValues.currentRole.charAt(0).toUpperCase() + defaultValues.currentRole.slice(1)}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Current Role (editable but disabled current role option) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Current Role
          </label>
          <Select
            value={currentRole}
            onValueChange={(value: "user" | "admin") => setValue("currentRole", value)}
          >
            <SelectTrigger className="w-full h-9 text-sm bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-700 rounded-md shadow-sm text-gray-900 dark:text-gray-100">
              <SelectValue />
            </SelectTrigger>
            <SelectContent className="bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-700 shadow-lg rounded-md text-gray-900 dark:text-gray-100">
              <SelectItem
                value="user"
                disabled={defaultValues.currentRole === "user"}
                className={`hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-900 dark:text-gray-100 ${
                  defaultValues.currentRole === "user" ? "cursor-not-allowed opacity-50" : ""
                }`}
              >
                User
              </SelectItem>
              <SelectItem
                value="admin"
                disabled={defaultValues.currentRole === "admin"}
                className={`hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-900 dark:text-gray-100 ${
                  defaultValues.currentRole === "admin" ? "cursor-not-allowed opacity-50" : ""
                }`}
              >
                Admin
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Created On (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Created On
          </label>
          <Input
            {...register("createdOn")}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Last Login (read-only) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Last Login
          </label>
          <Input
            value={defaultValues.lastLogin || "Never"}
            readOnly
            tabIndex={-1}
            className="w-full h-9 text-sm bg-gray-100 dark:bg-gray-700"
          />
        </div>

        {/* Updated At (preview) */}
        <div className="flex flex-col">
          <label className="text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">
            Updated At (will be set to today)
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
          <Button 
            variant="outline" 
            type="button" 
            onClick={() => onCancel?.()}
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