"use client"

import * as React from "react"
import { DataTable } from "@/components/ui/datatable"
import type { User } from "@/components/tablecolumns/manageusercol"
import { userColumns } from "@/components/tablecolumns/manageusercol"
import { EditUserRoleForm } from "@/components/ui/edituserroleform"
import { Toaster, toast } from "@/components/ui/sonner"

const initialUserData: User[] = [
  {
    id: "1",
    username: "john_doe",
    email: "john@example.com",
    previousRole: "user",
    currentRole: "user",
    createdOn: "2024-01-15",
    lastLogin: "2024-11-08",
    updatedBy: undefined,
    updatedAt: undefined
  },
  {
    id: "2",
    username: "jane_smith",
    email: "jane@example.com",
    previousRole: "user",
    currentRole: "admin",
    createdOn: "2024-02-20",
    lastLogin: "2024-11-07",
    updatedBy: "Admin User",
    updatedAt: "2024-03-15"
  },
  {
    id: "3",
    username: "bob_wilson",
    email: "bob@example.com",
    previousRole: "user",
    currentRole: "user",
    createdOn: "2024-03-10",
    lastLogin: "2024-10-15",
    updatedBy: undefined,
    updatedAt: undefined
  }
]

function isEqual(obj1: any, obj2: any): boolean {
  return Object.entries(obj1).every(([key, value]) => obj2[key] === value)
}

export default function ManageUsersPage() {
  const [users, setUsers] = React.useState(initialUserData)

  const handleSaveUser = async (updatedUser: User) => {
    const original = users.find(u => u.id === updatedUser.id)
    if (original && isEqual(original, updatedUser)) {
      toast.info("No changes detected.")
      return
    }

    try {
      console.log("Saving user role:", updatedUser)
      setUsers(prev =>
        prev.map(u => (u.id === updatedUser.id ? updatedUser : u))
      )
      toast.success("User role updated successfully!")
    } catch (error) {
      console.error(error)
      toast.error("Failed to update user role.")
    }
  }

  return (
    <div className="h-screen w-screen overflow-hidden flex flex-col text-gray-900 dark:text-gray-100 transition-colors">
      <Toaster />
      <a href="/administrator" className="btn-slate absolute top-6 right-6">Back</a>
      <div className="relative flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold text-center w-full">Manage User Roles</h1>
      </div>
      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 p-6 rounded-lg shadow-lg backdrop-blur-sm transition-colors">
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
              onSubmit={(values) => {
                onSave(values)
                handleSaveUser(values)
              }}
            />
          )}
        />
      </div>
    </div>
  )
}