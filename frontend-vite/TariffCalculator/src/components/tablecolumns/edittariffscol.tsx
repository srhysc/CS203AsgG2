import type { ColumnDef } from "@tanstack/react-table"
import { ArrowUpDown } from "lucide-react"
import { Button } from "@/components/ui/button"

export type Tariff = {
  id: string
  productCode: string
  exportingCountry: string
  importingCountry: string
  tariffRate: number
  lastUpdated?: string
}

export const tariffColumns: ColumnDef<Tariff>[] = [
  {
    id: "productCode",
    accessorKey: "productCode",
    header: ({ column }) => (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Product Code
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
    ),
    meta: { label: "Product Code" }
  },

  {
    id: "exportingCountry",
    accessorKey: "exportingCountry",
    header: ({ column }) => (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Exporting Country
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
    ),
    meta: { label: "Exporting Country" }
  },

  {
    id: "importingCountry",
    accessorKey: "importingCountry",
    header: ({ column }) => (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Importing Country
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
    ),
    meta: { label: "Importing Country" }
  },

  {
    id: "tariffRate",
    accessorKey: "tariffRate",
    header: ({ column }) => (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Tariff Rate
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
    ),
    meta: { label: "Tariff Rate" },
    cell: ({ row }) => {
      const rate = Number(row.getValue("tariffRate"))
      return isNaN(rate)
        ? "-"
        : new Intl.NumberFormat("en-US", {
            style: "percent",
            minimumFractionDigits: 2,
          }).format(rate)
    },
  },

  {
    id: "lastUpdated",
    accessorKey: "lastUpdated",
    header: ({ column }) => (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Last Updated
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
    ),
    meta: { label: "Last Updated" }
  },

  {
    id: "actions",
    header: "Edit",
    // Cell will be handled by DataTable component
    cell: () => null,
    enableHiding: false,
    meta: { label: "Actions" }
  },
]