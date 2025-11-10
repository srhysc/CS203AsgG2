import type { ColumnDef } from "@tanstack/react-table"
import { ArrowUpDown } from "lucide-react"
import { Button } from "@/components/ui/button"

export type ProductPrice = {
  id: string
  productCode: string
  productName: string
  price: number
  lastUpdated?: string
  unit?: string
}

export const productPriceColumns: ColumnDef<ProductPrice>[] = [
  {
    id: "productCode",
    accessorKey: "productCode",
    header: ({ column }) => (
      <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}>
        Product Code <ArrowUpDown className="ml-2 h-4 w-4" />
      </Button>
    ),
    meta: { label: "Product Code" }
  },
  {
    id: "productName",
    accessorKey: "productName",
    header: ({ column }) => (
        <Button
        variant="ghost"
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
        Product Name
        <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
    ),
    cell: ({ row }) => (
        <div className="whitespace-pre-wrap break-words max-w-xs">
        {row.getValue("productName")}
        </div>
    ),
    meta: { label: "Product Name" }
  },
  {
    id: "price",
    accessorKey: "price",
    header: ({ column }) => (
      <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}>
        Price (USD) <ArrowUpDown className="ml-2 h-4 w-4" />
      </Button>
    ),
    meta: { label: "Price (USD)" },
    cell: ({ row }) => {
      const value = Number(row.getValue("price"))
      return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        minimumFractionDigits: 2,
      }).format(value)
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
  { id: "actions", header: "Edit", cell: () => null, enableHiding: false },
]
