import type { ColumnDef } from "@tanstack/react-table"
import { ArrowUpDown } from "lucide-react"
import { Button } from "@/components/ui/button"

export type ShippingFee = {
  id: string
  originCountry: string
  destinationCountry: string
  costPerTon: number
  costPerBarrel: number
  costPerMMBtu: number
  lastUpdated?: string
  updatedBy?: string
}

export const shippingFeeColumns: ColumnDef<ShippingFee>[] = [
  {
    id: "originCountry",
    accessorKey: "originCountry",
    header: ({ column }) => (
      <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}>
        Origin Country 
      <ArrowUpDown className="h-4 w-4" />
      </Button>
    ),
    meta: { label: "Origin Country" }
  },
  {
    id: "destinationCountry",
    accessorKey: "destinationCountry",
    header: ({ column }) => (
      <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}>
        Destination Country 
      <ArrowUpDown className="h-4 w-4" />
      </Button>
    ),
    meta: { label: "Destination Country" }
  },
  {
  id: "costPerTon",
  accessorKey: "costPerTon",
  header: ({ column }) => (
    <Button
      variant="ghost"
      onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
    >
      Cost per Ton (USD)
      <ArrowUpDown className="h-4 w-4" />
    </Button>
  ),
  meta: { label: "Cost per Ton (USD)" },
  cell: ({ row }) => {
    const value = Number(row.getValue("costPerTon"))
    return isNaN(value)
      ? "-"
      : new Intl.NumberFormat("en-US", {
          style: "currency",
          currency: "USD",
          minimumFractionDigits: 2,
        }).format(value)
    }
  },
  {
  id: "costPerBarrel",
  accessorKey: "costPerBarrel",
  header: ({ column }) => (
    <Button
      variant="ghost"
      onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
    >
      Cost per Barrel (USD)
      <ArrowUpDown className="h-4 w-4" />
    </Button>
  ),
  meta: { label: "Cost per Barrel (USD)" },
  cell: ({ row }) => {
    const value = Number(row.getValue("costPerBarrel"))
    return isNaN(value)
      ? "-"
      : new Intl.NumberFormat("en-US", {
          style: "currency",
          currency: "USD",
          minimumFractionDigits: 2,
        }).format(value)
    }
  },
  {
  id: "costPerMMBtu",
  accessorKey: "costPerMMBtu",
  header: ({ column }) => (
    <Button
      variant="ghost"
      onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
    >
      Cost per MMBtu (USD)
      <ArrowUpDown className="h-4 w-4" />
    </Button>
  ),
  meta: { label: "Cost per MMBtu (USD)" },
  cell: ({ row }) => {
    const value = Number(row.getValue("costPerMMBtu"))
    return isNaN(value)
      ? "-"
      : new Intl.NumberFormat("en-US", {
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
          <ArrowUpDown className="h-4 w-4" />
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
          <ArrowUpDown className="h-4 w-4" />
        </Button>
    ),
    meta: { label: "Updated By" }
  },
  { id: "actions", header: "Edit", cell: () => null, enableHiding: false, meta: { label: "Actions" } },
]
