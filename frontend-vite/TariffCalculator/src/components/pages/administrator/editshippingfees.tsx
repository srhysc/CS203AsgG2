"use client"

import * as React from "react"
import { DataTable } from "@/components/ui/datatable"
import type { ShippingFee } from "@/components/tablecolumns/editshippingfeescol"
import { shippingFeeColumns } from "@/components/tablecolumns/editshippingfeescol"
import { EditShippingFeeForm } from "@/components/ui/editshippingfeesform"
import { Toaster, toast } from "@/components/ui/sonner"

const initialShippingFeeData: ShippingFee[] = [
  {
    id: "1",
    originCountry: "Saudi Arabia",
    destinationCountry: "Singapore",
    costPerTon: 120,
    costPerBarrel: 75,
    costPerMMBtu: 8,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin"
  },
  {
    id: "2",
    originCountry: "USA",
    destinationCountry: "Malaysia",
    costPerTon: 150,
    costPerBarrel: 85,
    costPerMMBtu: 9,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin"
  }
]

function isEqual(obj1: any, obj2: any): boolean {
  return Object.entries(obj1).every(([key, value]) => obj2[key] === value)
}

export default function EditShippingFeesPage() {
  const [shippingFees, setShippingFees] = React.useState(initialShippingFeeData)

  const handleSaveShippingFee = async (updatedCost: ShippingFee) => {
    const original = shippingFees.find(c => c.id === updatedCost.id)
    if (original && isEqual(original, updatedCost)) {
      toast.info("No changes detected.")
      return
    }

    try {
      console.log("Saving shipping cost:", updatedCost)
      setShippingFees(prev =>
        prev.map(c => (c.id === updatedCost.id ? updatedCost : c))
      )
      toast.success("Shipping cost updated successfully!")
    } catch (error) {
      console.error(error)
      toast.error("Failed to update shipping cost.")
    }
  }

  return (
    <div className="h-screen w-screen overflow-hidden flex flex-col text-gray-900 dark:text-gray-100 transition-colors">
      <Toaster />
      <a href="/administrator" className="btn-slate absolute top-6 right-6">Back</a>
      <div className="relative flex items-center justify-between mb-6">
      <h1 className="text-3xl font-bold text-center w-full">Edit Shipping Fees</h1>
      </div>
      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 p-6 rounded-lg shadow-lg backdrop-blur-sm transition-colors">
        <DataTable
          columns={shippingFeeColumns}
          data={shippingFees}
          setData={setShippingFees}
          filterPlaceholder="Search..."
          renderRowEditForm={(row, onSave, onCancel) => (
            <EditShippingFeeForm
              defaultValues={row}
              currentUserName="Admin User"
              onCancel={onCancel}
              onSubmit={(values) => {
                onSave(values)
                handleSaveShippingFee(values)
              }}
            />
          )}
        />
      </div>
    </div>
  )
}
