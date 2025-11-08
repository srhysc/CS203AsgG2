import type { ColumnDef } from "@tanstack/react-table"
import { ArrowUpDown } from "lucide-react"
import { Button } from "@/components/ui/button"

export type VATRate = {
  id: string
  country: string
  vatRate: number
  lastUpdated?: string
  updatedBy?: string
}

export const VATRateColumns: ColumnDef<VATRate>[] = [
  {
    id: "country",
    accessorKey: "country",
    header: ({ column }) => (
      <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}>
        Country <ArrowUpDown className="ml-2 h-4 w-4" />
      </Button>
    ),
    meta: { label: "Country" }
  },
  {
  id: "vatRate",
  accessorKey: "vatRate",
  header: ({ column }) => (
    <Button
      variant="ghost"
      onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
    >
      VAT Rate <ArrowUpDown className="ml-2 h-4 w-4" />
    </Button>
  ),
  meta: { label: "VAT Rate" },
  cell: ({ row }) => {
    const rate = Number(row.getValue("vatRate"))
    return isNaN(rate)
      ? "-"
      : new Intl.NumberFormat("en-US", {
          style: "percent",
          minimumFractionDigits: 2,
        }).format(rate)
    }
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
    id: "updatedBy",
    accessorKey: "updatedBy",
    header: ({ column }) => (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Updated By
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
    ),
    meta: { label: "Updated By" }
  },
  { id: "actions", header: "Edit", cell: () => null, enableHiding: false },
]
