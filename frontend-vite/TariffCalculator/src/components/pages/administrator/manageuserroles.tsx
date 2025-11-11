"use client"

import * as React from "react"
import { useAuth } from "@clerk/clerk-react"
import { Toaster, toast } from "@/components/ui/sonner"
import { DataTable } from "@/components/ui/datatable"
import { TableSkeleton } from "@/components/ui/tableskeleton"
import { EditUserRoleForm } from "@/components/ui/edituserroleform"
import { userColumns } from "@/components/tablecolumns/manageusercol"
import type { User } from "@/components/tablecolumns/manageusercol"

function isEqual(obj1: any, obj2: any): boolean {
  return Object.entries(obj1).every(([key, value]) => obj2[key] === value)
}

export default function ManageUsersPage() {
  const { getToken } = useAuth()
  const [users, setUsers] = React.useState<User[]>([])
  const [loading, setLoading] = React.useState(true)

  React.useEffect(() => {
    const fetchUsers = async () => {
      setLoading(true)
      try {
        const backend = "http://localhost:8080"
        const token = await getToken()

        const res = await fetch(`${backend}/api/users`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        if (!res.ok) throw new Error("Failed to fetch users")
        const data = await res.json()

        const formatted = data.map((user: any) => ({
          id: user.id,
          username: user.username,
          email: user.email,
          currentRole: user.role,
        }))

        setUsers(formatted)
      } catch (error) {
        console.error("Error fetching users:", error)
        toast.error("Failed to load users.")
      } finally {
        setLoading(false)
      }
    }

    fetchUsers()
  }, [getToken])

  const handleSaveUser = async (updatedUser: User) => {
    const original = users.find(u => u.id === updatedUser.id)
    if (original && original.currentRole === updatedUser.currentRole) {
      toast.info("No changes detected.")
      return
    }

    try {
      const backend = "http://localhost:8080"
      const token = await getToken()

      const res = await fetch(`${backend}/api/users/${updatedUser.id}/role`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updatedUser.currentRole),
      })

      if (!res.ok) {
        const errorText = await res.text()
        throw new Error(errorText || "Failed to update role")
      }

      toast.success("User role updated successfully!")
      setUsers(prev =>
        prev.map(u => {
          if (u.id === updatedUser.id) {
            return {
              ...updatedUser,
              currentRole: updatedUser.currentRole  
            }
          }
          return u
        })
      )
    } catch (error) {
      console.error(error)
      toast.error(
        error instanceof Error ? error.message : "Failed to update user role."
      )
    }
  }

  return (
    <div className="h-screen w-screen overflow-hidden flex flex-col text-gray-900 dark:text-gray-100 transition-colors">
      <Toaster />
      <a href="/administrator" className="btn-slate absolute top-6 right-6">Back</a>

      <div className="relative flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold text-center w-full">Manage User Roles</h1>
      </div>

      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 rounded-lg p-6 shadow-lg backdrop-blur-sm transition-colors">
        {loading ? (
          <TableSkeleton columns={userColumns} />
        ) : (
          <DataTable
            columns={userColumns}
            data={users}
            setData={setUsers}
            filterPlaceholder="Search..."
            renderRowEditForm={(row, onSave, onCancel) => (
              <EditUserRoleForm
                defaultValues={row}
                currentUserName="Admin User"
                onCancel={onCancel}
                onSubmit={values => {
                  onSave(values)
                  handleSaveUser(values)
                }}
              />
            )}
          />
        )}
      </div>
    </div>
  )
}
