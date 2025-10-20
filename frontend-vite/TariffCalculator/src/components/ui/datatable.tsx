"use client"

import * as React from "react"
import type { ColumnDef, ColumnFiltersState, SortingState, VisibilityState } from "@tanstack/react-table"
import { flexRender, getCoreRowModel, getFilteredRowModel, getPaginationRowModel, getSortedRowModel, useReactTable,} from "@tanstack/react-table"
import { ChevronDown, Pencil } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Button } from "@/components/ui/button"
import { DropdownMenu, DropdownMenuCheckboxItem, DropdownMenuContent, DropdownMenuTrigger, } from "@/components/ui/dropdown-menu"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger, } from "@/components/ui/dialog"

type DataTableProps<T> = {
  columns: ColumnDef<T>[]
  data: T[]
  setData?: React.Dispatch<React.SetStateAction<T[]>>
  filterPlaceholder?: string
  filterKey?: string // Add this prop for filter column
}

export function DataTable<T extends Record<string, any>>({ columns, data, filterPlaceholder, filterKey = "name" }: DataTableProps<T>) {
  const [sorting, setSorting] = React.useState<SortingState>([])
  const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>([])
  const [columnVisibility, setColumnVisibility] = React.useState<VisibilityState>({})
  const [rowSelection, setRowSelection] = React.useState({})
  const [tableData, setTableData] = React.useState<T[]>(data)
  const [editingRow, setEditingRow] = React.useState<T | null>(null) 
  const [formValues, setFormValues] = React.useState<Record<string, any>>({}) 

  const handleEditClick = (row: T) => {
    setEditingRow(row)
    setFormValues({ ...row })
  }

  const handleSave = () => {
    if (!editingRow) return

    const updatedData = tableData.map((r) =>
      r === editingRow ? { ...formValues } : r
    )

    setTableData(updatedData)

    if (setData) {
      setData(updatedData)
    }

    setEditingRow(null)
  }

  const enhancedColumns = React.useMemo(() => {
    return columns.map((col) => {
      if (col.id === "actions") {
        return {
          ...col,
          cell: ({ row }: any) => (
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleEditClick(row.original)}
            >
              <Pencil className="h-4 w-4" />
            </Button>
          ),
        }
      }
      return col
    })
  }, [columns])

  const table = useReactTable({
    data: tableData,
    columns: enhancedColumns,
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
                <TableRow key={row.id}>
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

      <Dialog open={!!editingRow} onOpenChange={() => setEditingRow(null)}>
        <DialogContent className="sm:max-w-[500px] bg-white dark:bg-gray-800 rounded-md shadow-lg p-6 z-50 text-gray-900 dark:text-gray-100">
          <DialogHeader>
            <DialogTitle>Edit Tariff</DialogTitle>
            <DialogDescription>Modify the fields and click Save.</DialogDescription>
          </DialogHeader>

          {editingRow && (
            <div className="space-y-3 py-2">
              {Object.keys(editingRow).map((key) => (
                <div key={key} className="flex flex-col">
                  <label className="text-sm font-medium capitalize">{key}</label>
                  <Input
                    value={formValues[key] ?? ""}
                    onChange={(e) =>
                      setFormValues((prev) => ({ ...prev, [key]: e.target.value }))
                    }
                  />
                </div>
              ))}
            </div>
          )}

          <div className="flex justify-end gap-2 mt-4">
            <Button variant="outline" onClick={() => setEditingRow(null)}>
              Cancel
            </Button>
            <Button onClick={handleSave}>Save</Button>
          </div>
        </DialogContent>
      </Dialog>

    </div>
  )
}

