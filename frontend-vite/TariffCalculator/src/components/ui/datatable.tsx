"use client"

import * as React from "react"
import type { ColumnDef, ColumnFiltersState, SortingState, VisibilityState } from "@tanstack/react-table"
import {
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
} from "@tanstack/react-table"
import { ChevronDown, Pencil } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

type DataTableProps<T> = {
  columns: ColumnDef<T>[]
  data: T[]
  filterPlaceholder?: string
  filterKey?: string // Add this prop for filter column
}

export function DataTable<T>({ columns, data, filterPlaceholder, filterKey = "name" }: DataTableProps<T>) {
  const [sorting, setSorting] = React.useState<SortingState>([])
  const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>([])
  const [columnVisibility, setColumnVisibility] = React.useState<VisibilityState>({})
  const [rowSelection, setRowSelection] = React.useState({})

  const table = useReactTable({
    data,
    columns,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    onColumnVisibilityChange: setColumnVisibility,
    onRowSelectionChange: setRowSelection,
    globalFilterFn: (row, columnId, filterValue) => {
      if (!filterValue) return true
      const search = filterValue.toString().toLowerCase().trim().replace(/[%\s]/g, "")

      return row.getAllCells().some(cell => {
        const value = cell.getValue()
        if (value == null) return false

        // --- Handle tariffRate (numeric) ---
        if (typeof value === "number") {
          const asPercent = (value * 100).toFixed(2) 
          const asRaw = value.toString()             
          return asPercent.includes(search) || asRaw.includes(search)
        }

        // --- Handle date strings ---
        if (typeof value === "string" && /^\d{4}-\d{2}-\d{2}/.test(value)) {
          const dateString = value.toLowerCase()
          return dateString.includes(search)
        }

        // --- Handle general strings (countries, status, names, etc.) ---
        if (typeof value === "string") {
          return value.toLowerCase().includes(search)
        }

        return false
      })
    },
    state: {
      sorting,
      columnFilters,
      columnVisibility,
      rowSelection,
    },
    initialState: {
      pagination: {
        pageSize: 8,
      },
    },
  })

  
  const pageIndex = table.getState().pagination.pageIndex
  const pageCount = table.getPageCount()
  
  return (
    <div className="w-full">
      <div className="flex items-center py-4">
        {filterPlaceholder && (
          <Input
            placeholder={filterPlaceholder}
            value={table.getState().globalFilter ?? ""}
            onChange={(event) => table.setGlobalFilter(event.target.value)}
            className="max-w-sm"
          />
        )}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" className="ml-auto cursor-pointer">
              Columns <ChevronDown />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-700 shadow-lg p-2 rounded-md min-w-[160px] text-gray-900 dark:text-gray-100">
            {table
              .getAllColumns()
              .filter((column) => column.getCanHide())
              .map((column) => (
                <DropdownMenuCheckboxItem
                  key={column.id}
                  className="capitalize relative pl-8 pr-2 py-1 rounded-md hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
                  checked={column.getIsVisible()}
                  onCheckedChange={(value) => column.toggleVisibility(!!value)}
                >
                  {column.id}
                </DropdownMenuCheckboxItem>
              ))}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <div className="overflow-hidden rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <TableHead className="text-center" key={header.id}>
                    {header.isPlaceholder
                      ? null
                      : flexRender(header.column.columnDef.header, header.getContext())}
                  </TableHead>
                ))}
              </TableRow>
            ))}
          </TableHeader>

          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow key={row.id} data-state={row.getIsSelected() && "selected"}>
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

    <div className="flex justify-center items-center py-4 gap-4">
        <Button
          size="sm"
          variant="outline"
          onClick={() => table.previousPage()}
          disabled={!table.getCanPreviousPage()}
          className="hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors cursor-pointer"
        >
          Previous
        </Button>

        <span className="text-sm text-gray-600 dark:text-gray-300">
          Page <strong>{pageIndex + 1}</strong> of <strong>{pageCount}</strong>
        </span>

        <Button
          size="sm"
          variant="outline"
          onClick={() => table.nextPage()}
          disabled={!table.getCanNextPage()}
          className="hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors cursor-pointer"
        >
          Next
        </Button>
      </div>
    </div>
  )
}

export const columns: ColumnDef<any>[] = [
  {
    id: "actions",
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
  },
]
