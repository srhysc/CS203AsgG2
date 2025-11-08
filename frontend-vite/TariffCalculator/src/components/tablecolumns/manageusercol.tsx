import type { ColumnDef } from "@tanstack/react-table"
import { ArrowUpDown } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"

export type User = {
  id: string
  username: string
  email: string
  previousRole: "user" | "admin"
  currentRole: "user" | "admin"
  createdOn: string
  lastLogin?: string
  updatedAt?: string
}

const roleBadgeClasses = (role: string) =>
  `capitalize px-3 py-1 rounded-full text-sm font-medium tracking-wide
   shadow-sm transition-colors duration-150
   ${role === "admin"
      ? "bg-purple-200 text-purple-800 dark:bg-purple-700 dark:text-purple-100"
      : "bg-yellow-200 text-yellow-800 dark:bg-yellow-700 dark:text-yellow-100"}`



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
    id: "previousRole",
    accessorKey: "previousRole",
    header: ({ column }) => (
      <Button 
        variant="ghost" 
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Previous Role
        <ArrowUpDown className="h-4 w-4" />
      </Button>
    ),
    cell: ({ row }) => {
      const role = row.getValue("previousRole") as string
      return <span className={roleBadgeClasses(role)}>{role}</span>
    },
    meta: { label: "Previous Role" }
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
    id: "createdOn",
    accessorKey: "createdOn",
    header: ({ column }) => (
      <Button 
        variant="ghost" 
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Created On
        <ArrowUpDown className="h-4 w-4" />
      </Button>
    ),
    meta: { label: "Created On" }
  },
  {
    id: "lastLogin",
    accessorKey: "lastLogin",
    header: ({ column }) => (
      <Button 
        variant="ghost" 
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Last Login
        <ArrowUpDown className="h-4 w-4" />
      </Button>
    ),
    cell: ({ row }) => {
      const date = row.getValue("lastLogin") as string | undefined
      return date || <span className="text-gray-400">Never</span>
    },
    meta: { label: "Last Login" }
  },
  {
    id: "updatedAt",
    accessorKey: "updatedAt",
    header: ({ column }) => (
      <Button 
        variant="ghost" 
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Updated At
        <ArrowUpDown className="h-4 w-4" />
      </Button>
    ),
    cell: ({ row }) => {
      const date = row.getValue("updatedAt") as string | undefined
      return date || <span className="text-gray-400">-</span>
    },
    meta: { label: "Updated At" }
  },
  {
    id: "actions",
    header: "Edit",
    cell: () => null,
    enableHiding: false,
    meta: { label: "Actions" }
  }
]