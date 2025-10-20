"use client"

import { Checkbox as BaseCheckbox } from "@radix-ui/react-checkbox"
import { Label } from "@/components/ui/label"

// Reusable base Checkbox
export { BaseCheckbox as Checkbox }

// Labeled CheckboxField for forms/settings
type CheckboxFieldProps = {
  id: string
  label: string
  description?: string
  checked?: boolean
  defaultChecked?: boolean
  disabled?: boolean
  onChange?: (checked: boolean) => void
  className?: string
}

export function CheckboxField({
  id,
  label,
  description,
  checked,
  defaultChecked,
  disabled,
  onChange,
  className = "",
}: CheckboxFieldProps) {
  return (
    <Label
      htmlFor={id}
      className={`flex items-start gap-3 rounded-lg border p-3 cursor-pointer ${className}`}
    >
      <BaseCheckbox
        id={id}
        checked={checked}
        defaultChecked={defaultChecked}
        disabled={disabled}
        onCheckedChange={onChange}
        className="border border-gray-400 dark:border-gray-600 bg-white dark:bg-gray-900 rounded w-5 h-5 data-[state=checked]:border-blue-600 data-[state=checked]:bg-blue-600 data-[state=checked]:text-white dark:data-[state=checked]:border-blue-400 dark:data-[state=checked]:bg-blue-400"
      />
      <div className="grid gap-1.5 font-normal">
        <span className="text-sm leading-none font-medium">{label}</span>
        {description && (
          <span className="text-muted-foreground text-sm">{description}</span>
        )}
      </div>
    </Label>
  )
}
