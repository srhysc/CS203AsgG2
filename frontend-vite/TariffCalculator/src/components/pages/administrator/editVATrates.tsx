"use client"

import * as React from "react"
import { useAuth } from "@clerk/clerk-react"
import { Toaster, toast } from "@/components/ui/sonner"
import { DataTable } from "@/components/ui/datatable"
import { VATRateColumns } from "@/components/tablecolumns/editVATratescol"
import { EditVATRateForm } from "@/components/ui/editVATratesform"
import type { VATRate } from "@/components/tablecolumns/editVATratescol"
import { TableSkeleton } from "@/components/ui/tableskeleton"

function isEqual(obj1: any, obj2: any): boolean {
  return Object.entries(obj1).every(([key, value]) => obj2[key] === value)
}

export default function EditVATRatesPage() {
  const { getToken } = useAuth()
  const [tableData, setTableData] = React.useState<VATRate[]>([])
  const [loading, setLoading] = React.useState(true)

  React.useEffect(() => {
    const fetchVATRates = async () => {
      setLoading(true)
      try {
        const backend = "http://localhost:8080"
        const token = await getToken()

        const countriesRes = await fetch(`${backend}/countries/vat-rates-latest`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        if (!countriesRes.ok) throw new Error("Failed to fetch VAT rates")
        const data = await countriesRes.json()

        // const formatted = data.map((item: any) => ({
        //   id: item.country,
        //   country: item.country,
        //   vatRate: item.vatRate,
        //   lastUpdated: item.lastUpdated,
        // }))
        const formatted = data.map((item: any) => {
      // Check both possible property names and use appropriate one
      const rateValue = typeof item.rate !== 'undefined' ? item.rate : 
                       typeof item.vatRate !== 'undefined' ? item.vatRate : 0

      return {
        id: item.country,
        country: item.country,
        vatRate: rateValue,
        lastUpdated: item.lastUpdated,
      }
    })

        setTableData(formatted)

      } catch (error) {
        console.error("Error fetching VAT rates:", error)
        toast.error("Failed to load VAT rates.")
      } finally {
        setLoading(false)
      }
    }

    fetchVATRates()
  }, [getToken])

  const handleSaveVATRate = async (updatedVATRate: VATRate) => {
    const original = tableData.find(a => a.id === updatedVATRate.id)
    if (original && isEqual(original, updatedVATRate)) {
      toast.info("No changes detected.")
      return
    }

    try {
      console.log("Saving VAT rate:", updatedVATRate)
      const backend = "http://localhost:8080"
      const token = await getToken()

      const response = await fetch(
        `${backend}/countries/${updatedVATRate.country}/vat-ratenew`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            rate: updatedVATRate.vatRate,
            date: updatedVATRate.lastUpdated,
          }),
        }
      )

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || "Failed to save VAT rate")
      }

      setTableData(prev =>
        prev.map(a => (a.id === updatedVATRate.id ? updatedVATRate : a))
      )
      toast.success("VAT rate updated successfully!")
    } catch (error) {
      console.error(error)
      toast.error(
        error instanceof Error ? error.message : "Failed to update VAT rate."
      )
    }
  }

  return (
    <div className="h-screen w-screen overflow-hidden flex flex-col text-gray-900 dark:text-gray-100 transition-colors">
      <Toaster />
      <a href="/administrator" className="btn-slate absolute top-6 right-6">
        Back
      </a>

      <div className="relative flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold text-center w-full">Edit VAT rate</h1>
      </div>

      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 rounded-lg p-6 shadow-lg backdrop-blur-sm transition-colors">
        {loading ? (
          <TableSkeleton columns={VATRateColumns} />
           ) : (
          <DataTable
            columns={VATRateColumns}
            data={tableData}
            setData={setTableData}
            filterPlaceholder="Search..."
            renderRowEditForm={(row, onSave, onCancel) => (
              <EditVATRateForm
                defaultValues={row}
                currentUserName="Admin User"
                onCancel={onCancel}
                onSubmit={values => {
                  onSave(values)
                  handleSaveVATRate(values)
                }}
              />
            )}
          />
        )}
      </div>
    </div>
  )
}
