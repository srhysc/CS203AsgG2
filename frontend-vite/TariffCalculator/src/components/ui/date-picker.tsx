"use client"

import * as React from "react"
import { Calendar as CalendarIcon } from "lucide-react"
import { format } from "date-fns"
import { Button } from "@/components/ui/button"
import { Popover, PopoverTrigger, PopoverContent } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import { cn } from "@/lib/utils"

type DatePickerProps = {
  value?: Date
  onChange?: (date: Date | undefined) => void
  className?: string
  buttonClassName?: string
  popoverClassName?: string
}

export function DatePicker({ 
  value, 
  onChange, 
  className,
  buttonClassName,
  popoverClassName 
}: DatePickerProps) {
  const [selected, setSelected] = React.useState<Date | undefined>(value)

  // Sync internal state with external value prop
  React.useEffect(() => {
    setSelected(value)
  }, [value])

  const handleSelect = (date?: Date) => {
    setSelected(date)
    onChange?.(date)
  }

  return (
    <div className={cn("inline-block", className)}>
      <Popover>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            className={cn(
              "justify-start text-left font-normal",
              buttonClassName
            )}
          >
            <CalendarIcon className="mr-2 h-4 w-4 flex-shrink-0" />
            {selected ? format(selected, "PPP") : <span>Pick a date</span>}
          </Button>
        </PopoverTrigger>
        <PopoverContent 
          className={cn("w-[300px] p-3", popoverClassName)}
          align="start"
          sideOffset={5}
        >
          <Calendar
            mode="single"
            selected={selected}
            onSelect={handleSelect}
            initialFocus
            classNames={{
              months: "flex flex-col space-y-4",
              month: "space-y-4 w-full",
              caption: "flex justify-center items-center relative mb-4",
              caption_label: "text-sm font-medium px-2",
              nav: "flex items-center gap-1 absolute inset-x-0 justify-between px-2",
              button_previous: "h-7 w-7 bg-transparent p-0 opacity-50 hover:opacity-100 inline-flex items-center justify-center rounded-md hover:bg-accent",
              button_next: "h-7 w-7 bg-transparent p-0 opacity-50 hover:opacity-100 inline-flex items-center justify-center rounded-md hover:bg-accent",
              table: "w-full border-collapse",
              head_row: "flex justify-around mb-1",
              head_cell: "text-muted-foreground w-9 font-normal text-[0.8rem] text-center",
              row: "flex justify-around w-full mt-1",
              cell: "relative p-0 text-center text-sm focus-within:relative focus-within:z-20 w-9 h-9",
              day: "h-9 w-9 p-0 font-normal inline-flex items-center justify-center rounded-md hover:bg-accent hover:text-accent-foreground",
              day_selected: "bg-primary text-primary-foreground hover:bg-primary hover:text-primary-foreground focus:bg-primary focus:text-primary-foreground",
              day_today: "bg-accent text-accent-foreground",
              day_outside: "text-muted-foreground opacity-50",
              day_disabled: "text-muted-foreground opacity-50",
              day_hidden: "invisible",
            }}
          />
        </PopoverContent>
      </Popover>
    </div>
  )
}
