"use client"

import * as React from "react"
import type {
  CellContext,
  ColumnDef,
  ColumnFiltersState,
  SortingState,
  VisibilityState
} from "@tanstack/react-table"
import {
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable
} from "@tanstack/react-table"
import { ChevronDown, Pencil } from "lucide-react"
import { Input } from "@/components/ui/input"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow
} from "@/components/ui/table"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuTrigger
} from "@/components/ui/dropdown-menu"
import { Dialog, DialogContent } from "@/components/ui/dialog"

type Identifiable = { id?: string | number }
type DataTableColumnMeta = { label?: string }

type DataTableProps<T extends Identifiable> = {
  columns: ColumnDef<T>[]
  data: T[]
  setData?: React.Dispatch<React.SetStateAction<T[]>>
  filterPlaceholder?: string
  renderRowEditForm?: (
    row: T,
    onSave: (updatedRow: T) => void,
    onCancel: () => void
  ) => React.ReactNode
  tableHeight?: string
  tableWidth?: string
}

export function DataTable<T extends Identifiable>({
  columns,
  data,
  setData,
  filterPlaceholder,
  renderRowEditForm,
  tableHeight = "auto",
  tableWidth = "100%"
}: DataTableProps<T>) {
  const [sorting, setSorting] = React.useState<SortingState>([])
  const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>([])
  const [columnVisibility, setColumnVisibility] = React.useState<VisibilityState>({})
  const [rowSelection, setRowSelection] = React.useState({})
  const [tableData, setTableData] = React.useState<T[]>(data)
  const [editingRow, setEditingRow] = React.useState<T | null>(null)

  React.useEffect(() => {
    setTableData(data)
  }, [data])

  const handleEditClick = React.useCallback((row: T) => setEditingRow(row), [])

  const handleSaveRow = (updatedRow: T) => {
    const updatedData = tableData.map((r) => {
      const rowId = r.id
      const editingId = editingRow?.id
      return rowId != null && editingId != null && rowId === editingId ? updatedRow : r
    })
    setTableData(updatedData)
    if (setData) setData(updatedData)
    setEditingRow(null)
  }

  const enhancedColumns = React.useMemo(() => {
    return columns.map((col) => {
      if (col.id === "actions") {
        return {
          ...col,
          cell: ({ row }: CellContext<T, unknown>) => (
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleEditClick(row.original)}
              aria-label="Edit"
              className="h-8 w-8 p-1 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors cursor-pointer"
            >
              <Pencil className="h-4 w-4" />
            </Button>
          )
        }
      }
      return col
    })
  }, [columns, handleEditClick])

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
    state: {
      sorting,
      columnFilters,
      columnVisibility,
      rowSelection
    },
    initialState: {
      pagination: { pageSize: 8 }
    },
    globalFilterFn: (row, _columnId, filterValue) => {
      if (!filterValue) return true
      const search = filterValue.toString().toLowerCase().trim().replace(/[%\s]/g, "")
      return row.getAllCells().some(cell => {
        const value = cell.getValue()
        if (value == null) return false
        if (typeof value === "number") {
          const asPercent = (value * 100).toFixed(2)
          return asPercent.includes(search) || value.toString().includes(search)
        }
        if (typeof value === "string") return value.toLowerCase().includes(search)
        return false
      })
    }
  })

  const pageIndex = table.getState().pagination.pageIndex
  const pageCount = table.getPageCount()

  return (
    <div className="w-full" style={{ maxWidth: tableWidth }}>
      {/* Search + Column visibility */}
      <div className="flex items-center py-4 gap-2">
        {filterPlaceholder && (
          <Input
            placeholder={filterPlaceholder}
            value={table.getState().globalFilter ?? ""}
            onChange={(e) => table.setGlobalFilter(e.target.value)}
            className="max-w-sm"
          />
        )}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" className="ml-auto cursor-pointer">
              Columns <ChevronDown className="ml-2 h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            align="end"
            className="bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-700 shadow-lg p-2 rounded-md min-w-[160px] text-gray-900 dark:text-gray-100"
          >
            {table
              .getAllColumns()
              .filter((col) => col.getCanHide())
              .map((col) => (
                <DropdownMenuCheckboxItem
                  key={col.id}
                  className="capitalize relative pl-8 pr-2 py-1.5 rounded-md hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors cursor-pointer"
                  checked={col.getIsVisible()}
                  onCheckedChange={(value) => col.toggleVisibility(!!value)}
                >
                  {(col.columnDef.meta as DataTableColumnMeta | undefined)?.label ?? col.id}
                </DropdownMenuCheckboxItem>
              ))}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      {/* Table with fixed size */}
      <div 
        className="overflow-hidden rounded-md border"
        style={{ 
          height: tableHeight,
          display: tableHeight !== 'auto' ? 'flex' : 'block',
          flexDirection: 'column'
        }}
      >
        <div className={tableHeight !== 'auto' ? 'overflow-auto flex-1' : 'overflow-auto'}>
          <Table className="w-full table-fixed">
            <TableHeader>
              {table.getHeaderGroups().map((headerGroup) => (
                <TableRow key={headerGroup.id}>
                  {headerGroup.headers.map((header) => (
                    <TableHead 
                      key={header.id} 
                      className="text-center px-2 py-2"
                      style={{ width: header.id === 'actions' ? '80px' : 'auto' }}
                    >
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
                  <TableRow
                    key={row.id}
                    data-state={row.getIsSelected() && "selected"}
                  >
                    {row.getVisibleCells().map((cell) => (
                      <TableCell 
                        key={cell.id} 
                        className="text-center whitespace-nowrap px-4 py-2"
                      >
                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                      </TableCell>
                    ))}
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell 
                    colSpan={columns.length} 
                    className="h-24 text-center"
                  >
                    No results.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
      </div>

      {/* Pagination */}
      <div className="flex justify-center items-center py-4 gap-4">
        <Button
          size="sm"
          variant="outline"
          onClick={() => table.previousPage()}
          disabled={!table.getCanPreviousPage()}
          className="hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors cursor-pointer disabled:opacity-50"
        >
          Previous
        </Button>
        <span className="text-sm text-gray-600 dark:text-gray-300">
          Page <strong>{pageIndex + 1}</strong> of <strong>{pageCount || 1}</strong>
        </span>
        <Button
          size="sm"
          variant="outline"
          onClick={() => table.nextPage()}
          disabled={!table.getCanNextPage()}
          className="hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors cursor-pointer disabled:opacity-50"
        >
          Next
        </Button>
      </div>

      {/* Edit Modal */}
      {editingRow && renderRowEditForm && (
        <Dialog open={!!editingRow} onOpenChange={() => setEditingRow(null)}>
          <DialogContent className="sm:max-w-[500px] bg-white dark:bg-gray-800 rounded-md shadow-lg p-6 z-50 text-gray-900 dark:text-gray-100">
            {renderRowEditForm(editingRow, handleSaveRow, () => setEditingRow(null))}
          </DialogContent>
        </Dialog>
      )}
    </div>
  )
}
