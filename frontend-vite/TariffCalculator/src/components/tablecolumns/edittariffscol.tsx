import type { ColumnDef } from "@tanstack/react-table"
import { Button } from "@/components/ui/button"
import { ArrowUpDown, Pencil } from "lucide-react"

export type Tariff = {
  id: string
  tariffCode: string
  tariffName: string
  exportingCountry: string
  importingCountry: string
  tariffRate: number
  status: "active" | "inactive" | "pending"
  effectiveFrom: string
  effectiveTo: string
}

export const tariffColumns: ColumnDef<Tariff>[] = [

  {
    accessorKey: "tariffCode",
    header: "Tariff Code",
  },

  {
    accessorKey: "tariffName",
    header: "Tariff Name",
  },

  {
    accessorKey: "exportingCountry",
    header: "Exporting Country",
  },

  {
    accessorKey: "importingCountry",
    header: "Importing Country",
  },

  {
    accessorKey: "tariffRate",
    header: "Tariff Rate",
    cell: ({ row }) => {
      const rate = Number(row.getValue("tariffRate"))
      // prevent NaN and display as percentage
      return isNaN(rate)
        ? "-"
        : new Intl.NumberFormat("en-US", {
            style: "percent",
            minimumFractionDigits: 2,
          }).format(rate)
    },
  },

  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => {
      const status = row.getValue("status") as string
      return (
        <span
          className={`capitalize px-2 py-1 rounded-md text-xs font-medium
            ${
              status === "active"
                ? "bg-green-100 text-green-700 dark:bg-green-800 dark:text-green-200"
                : status === "pending"
                ? "bg-yellow-100 text-yellow-700 dark:bg-yellow-800 dark:text-yellow-200"
                : "bg-gray-200 text-gray-900 dark:bg-gray-700 dark:text-gray-100"
            }`}
        >
          {status}
        </span>
      )
    },
  },

  {
    accessorKey: "effectiveFrom",
    header: "Effective From",
  },

  {
    accessorKey: "effectiveTo",
    header: "Effective To",
  },

  {
    id: "actions",
    header: "Edit",
    cell: ({ row }) => (
      <Button
        variant="ghost"
        size="sm"
        onClick={() => console.log("Edit tariff", row.original.id)}
        aria-label="Edit"
      >
        <Pencil className="h-4 w-4" />
      </Button>
    ),
    enableHiding: false,
  },
]
