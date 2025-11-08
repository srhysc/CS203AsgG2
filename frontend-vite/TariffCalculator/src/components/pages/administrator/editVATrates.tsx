"use client"

import * as React from "react"
import { DataTable } from "@/components/ui/datatable"
import type { VATRate } from "@/components/tablecolumns/editVATratescol"
import { VATRateColumns } from "@/components/tablecolumns/editVATratescol"
import { EditVATRateForm } from "@/components/ui/editVATratesform"
import { Toaster, toast } from "@/components/ui/sonner"

const initialVATRateData: VATRate[] = [
  { id: "1", country: "Singapore", vatRate: 0.07, lastUpdated: "2025-09-01" },
  { id: "2", country: "Malaysia", vatRate: 0.06, lastUpdated: "2025-09-01"},
  { id: "3", country: "Japan", vatRate: 0.10, lastUpdated: "2025-09-01" },
]

function isEqual(obj1: any, obj2: any): boolean {
  return Object.entries(obj1).every(([key, value]) => obj2[key] === value)
}

export default function EditVATRatesPage() {
  const [VATRates, setVATRates] = React.useState(initialVATRateData)

  const handleSaveVATRate = async (updatedVATRate: VATRate) => {
    const original = VATRates.find(a => a.id === updatedVATRate.id)

    if (original && isEqual(original, updatedVATRate)) {
      toast.info("No changes detected.")
      return
    }

    try {
      console.log("Saving VAT rate:", updatedVATRate)

      setVATRates(prev =>
        prev.map(a => (a.id === updatedVATRate.id ? updatedVATRate : a))
      )

      toast.success("VAT rate updated successfully!")
    } catch (error) {
      console.error(error)
      toast.error("Failed to update VAT rate.")
    }
  }

  return (
    <div className="h-screen w-screen overflow-hidden flex flex-col text-gray-900 dark:text-gray-100 transition-colors">
      <Toaster />
      <a href="/administrator" className="btn-slate absolute top-6 right-6">Back</a>

      <div className="relative flex items-center justify-between mb-6">
      <h1 className="text-3xl font-bold text-center w-full">Edit VAT rate</h1>
      </div>

      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 rounded-lg p-6 shadow-lg backdrop-blur-sm transition-colors">
        <DataTable
          columns={VATRateColumns}
          data={VATRates}
          setData={setVATRates}
          filterPlaceholder="Search..."
          renderRowEditForm={(row, onSave, onCancel) => (
            <EditVATRateForm
              defaultValues={row}
              currentUserName="Admin User"
              onCancel={onCancel}
              onSubmit={(values) => {
                onSave(values)
                handleSaveVATRate(values)
              }}
            />
          )}
        />
      </div>
    </div>
  )
}
