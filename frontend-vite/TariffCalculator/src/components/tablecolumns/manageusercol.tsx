import type { ColumnDef } from "@tanstack/react-table"
import { ArrowUpDown } from "lucide-react"
import { Button } from "@/components/ui/button"

export type User = {
  id: string
  username: string
  email: string
  currentRole: "USER" | "ADMIN"
}

const roleBadgeClasses = (role: string) => {
  const normalizedRole = role.toUpperCase()
  return `capitalize px-3 py-1 rounded-full text-sm font-medium tracking-wide
   shadow-sm transition-colors duration-150
   ${normalizedRole === "ADMIN"
      ? "bg-purple-200 text-purple-800 dark:bg-purple-700 dark:text-purple-100"
      : "bg-yellow-200 text-yellow-800 dark:bg-yellow-700 dark:text-yellow-100"}`
}



export const userColumns: ColumnDef<User>[] = [
  {
    id: "username",
    accessorKey: "username",
    header: ({ column }) => (
      <Button 
        variant="ghost" 
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Username
        <ArrowUpDown className="h-4 w-4" />
      </Button>
    ),
    meta: { label: "Username" }
  },
  {
    id: "email",
    accessorKey: "email",
    header: ({ column }) => (
      <Button 
        variant="ghost" 
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Email
        <ArrowUpDown className="h-4 w-4" />
      </Button>
    ),
    meta: { label: "Email" }
  },
  {
    id: "currentRole",
    accessorKey: "currentRole",
    header: ({ column }) => (
      <Button 
        variant="ghost" 
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Current Role
        <ArrowUpDown className="h-4 w-4" />
      </Button>
    ),
    cell: ({ row }) => {
      const role = row.getValue("currentRole") as string
      return <span className={roleBadgeClasses(role)}>{role}</span>
    },
    meta: { label: "Current Role" }
  },
  {
    id: "actions",
    header: "Edit",
    cell: () => null,
    enableHiding: false,
    meta: { label: "Actions" }
  }
]
