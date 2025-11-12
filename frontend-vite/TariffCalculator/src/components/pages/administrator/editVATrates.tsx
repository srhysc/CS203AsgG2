"use client"

import * as React from "react"
import { useAuth } from "@clerk/clerk-react"
import { Toaster, toast } from "@/components/ui/sonner"
import { DataTable } from "@/components/ui/datatable"
import { VATRateColumns } from "@/components/tablecolumns/editVATratescol"
import { EditVATRateForm } from "@/components/ui/editVATratesform"
import type { VATRate } from "@/components/tablecolumns/editVATratescol"
import { TableSkeleton } from "@/components/ui/tableskeleton"
import { Link } from "react-router-dom"; 


const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080"

type VatRateResponse = {
  country: string
  rate: number
  lastUpdated: string
}

function isEqual(obj1: VATRate, obj2: VATRate): boolean {
  return (Object.keys(obj1) as Array<keyof VATRate>).every(
    key => obj2[key] === obj1[key]
  )
}

export default function EditVATRatesPage() {
  const { getToken } = useAuth()
  const [tableData, setTableData] = React.useState<VATRate[]>([])
  const [loading, setLoading] = React.useState(true)

  React.useEffect(() => {
    const fetchVATRates = async () => {
      setLoading(true)
      try {
        const token = await getToken()

        const countriesRes = await fetch(`${API_BASE}/countries/vat-rates-all`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        if (!countriesRes.ok) throw new Error("Failed to fetch VAT rates")
        const data: VatRateResponse[] = await countriesRes.json()

        const formatted: VATRate[] = data.map(item => ({
          id: item.country,
          country: item.country,
          vatRate: item.rate,
          lastUpdated: item.lastUpdated,
        }))

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
    const token = await getToken()

    const response = await fetch(
      `${API_BASE}/countries/${updatedVATRate.country}/vat-ratenew`,
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

    // Refetch the data after successful save
    const countriesRes = await fetch(`${API_BASE}/countries/vat-rates-all`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    if (!countriesRes.ok) throw new Error("Failed to fetch updated VAT rates")
    const data: VatRateResponse[] = await countriesRes.json()

    const formatted: VATRate[] = data.map(item => ({
      id: item.country,
      country: item.country,
      vatRate: item.rate,
      lastUpdated: item.lastUpdated,
    }))

    setTableData(formatted)
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
      <Link to="/administrator" className="btn-slate absolute top-6 right-6">
        Back
      </Link>

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
