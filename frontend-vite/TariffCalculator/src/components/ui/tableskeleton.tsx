import type { ColumnDef } from "@tanstack/react-table"

type TableSkeletonProps<T> = {
  columns: ColumnDef<T>[]
  rowCount?: number
  showFilter?: boolean
  showColumnToggle?: boolean
  showPagination?: boolean
  tableHeight?: string
}

export function TableSkeleton<T extends Record<string, unknown>>({
  columns,
  rowCount = 8,
  showFilter = true,
  showColumnToggle = true,
  showPagination = true,
  tableHeight = "auto"
}: TableSkeletonProps<T>) {
  return (
    <div className="w-full">
      {/* Search + Column visibility */}
      {(showFilter || showColumnToggle) && (
        <div className="flex items-center py-4 gap-2">
          {showFilter && (
            <div className="h-10 w-full max-w-sm bg-gray-300 dark:bg-gray-700 rounded-md animate-pulse" />
          )}
          {showColumnToggle && (
            <div className="h-10 w-28 ml-auto bg-gray-300 dark:bg-gray-700 rounded-md animate-pulse" />
          )}
        </div>
      )}

      {/* Table */}
      <div 
        className="overflow-hidden rounded-md border"
        style={{ 
          height: tableHeight,
          display: tableHeight !== 'auto' ? 'flex' : 'block',
          flexDirection: 'column'
        }}
      >
        <div className={tableHeight !== 'auto' ? 'overflow-auto flex-1' : ''}>
          <table className="w-full table-fixed">
            <thead className="bg-muted">
              <tr className="border-b">
                {columns.map((col, i) => (
                  <th 
                    key={col.id || i}
                    className="px-2 py-3 text-center"
                    style={{ 
                      width: col.id === 'actions' ? '80px' : `${100 / columns.length}%`,
                      minWidth: col.id === 'actions' ? '80px' : '150px'
                    }}
                  >
                    <div 
                      className="h-4 bg-gray-300 dark:bg-gray-700 rounded animate-pulse mx-auto" 
                      style={{ width: '80px' }}
                    />
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {Array.from({ length: rowCount }).map((_, rowIndex) => (
                <tr key={rowIndex} className="border-b">
                  {columns.map((col, colIndex) => (
                    <td 
                      key={col.id || colIndex}
                      className="px-4 py-3 text-center"
                    >
                      <div 
                        className="h-4 bg-gray-300 dark:bg-gray-700 rounded animate-pulse mx-auto" 
                        style={{ 
                          width: col.id === 'actions' 
                            ? '32px' 
                            : `${60 + (colIndex * 15) % 80}%` 
                        }} 
                      />
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pagination */}
      {showPagination && (
        <div className="flex justify-center items-center py-4 gap-4">
          <div className="h-9 w-20 bg-gray-300 dark:bg-gray-700 rounded-md animate-pulse" />
          <div className="h-5 w-32 bg-gray-300 dark:bg-gray-700 rounded animate-pulse" />
          <div className="h-9 w-16 bg-gray-300 dark:bg-gray-700 rounded-md animate-pulse" />
        </div>
      )}
    </div>
  )
}
