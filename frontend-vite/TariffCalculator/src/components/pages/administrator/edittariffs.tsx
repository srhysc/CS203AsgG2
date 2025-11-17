"use client"

import * as React from "react"
import { useAuth } from "@clerk/clerk-react"
import { Toaster, toast } from "@/components/ui/sonner"
import { DataTable } from "@/components/ui/datatable"
import { tariffColumns } from "@/components/tablecolumns/edittariffscol"
import { EditTariffForm } from "@/components/ui/edittariffform"
import type { Tariff } from "@/components/tablecolumns/edittariffscol"
import { TableSkeleton } from "@/components/ui/tableskeleton"
import { Link } from "react-router-dom"

const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080"

// Backend DTO shape
type MfnRateResponse = {
  countryIso3: string
  mfnAve: number
  year: number
}

export default function EditTariffsPage() {
  const { getToken } = useAuth()
  const [tableData, setTableData] = React.useState<Tariff[]>([])
  const [loading, setLoading] = React.useState(true)

  // Fetch all MFN rates
  React.useEffect(() => {
    const fetchTariffs = async () => {
      setLoading(true)
      try {
        const token = await getToken()
        const res = await fetch(`${API_BASE}/wits/mfnrates`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        if (!res.ok) throw new Error("Failed to fetch MFN rates")

        const data: MfnRateResponse[] = await res.json()
        const formatted: Tariff[] = data.map(item => ({
          id: `${item.countryIso3}-${item.year}`,
          importingCountry: item.countryIso3,
          tariffRate: item.mfnAve,
          lastUpdated: `${item.year}`,
        }))
        setTableData(formatted)
      } catch (error) {
        console.error("Error fetching MFN rates:", error)
        toast.error("Failed to load MFN rates.")
      } finally {
        setLoading(false)
      }
    }
    fetchTariffs()
  }, [getToken])

  // Save MFN rate
  const handleSaveTariff = async (updatedTariff: Tariff) => {
    const original = tableData.find(a => a.id === updatedTariff.id)
    if (original && original.tariffRate === updatedTariff.tariffRate) {
      toast.info("No changes detected.")
      return
    }

    try {
      console.log("Saving MFN rate:", updatedTariff)
      const token = await getToken()

      const response = await fetch(`${API_BASE}/wits/mfnrate`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          countryIso3: updatedTariff.importingCountry,
          mfnAve: updatedTariff.tariffRate,
          year: updatedTariff.lastUpdated
            ? new Date(updatedTariff.lastUpdated).getFullYear()
            : new Date().getFullYear() // fallback if undefined
        }),
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || "Failed to save MFN rate")
      }

      // Refetch after save
      const res = await fetch(`${API_BASE}/wits/mfnrates`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      if (!res.ok) throw new Error("Failed to fetch updated MFN rates")
      const data: MfnRateResponse[] = await res.json()
      const formatted: Tariff[] = data.map(item => ({
        id: `${item.countryIso3}-${item.year}`,
        importingCountry: item.countryIso3,
        tariffRate: item.mfnAve,
        lastUpdated: `${item.year}`,
      }))
      setTableData(formatted)
      toast.success("MFN rate updated successfully!")
    } catch (error) {
      console.error(error)
      toast.error(
        error instanceof Error ? error.message : "Failed to update MFN rate."
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
        <h1 className="text-3xl font-bold text-center w-full">Edit MFN Tariffs</h1>
      </div>

      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 rounded-lg p-6 shadow-lg backdrop-blur-sm transition-colors">
        {loading ? (
          <TableSkeleton columns={tariffColumns} />
        ) : (
          <DataTable
            columns={tariffColumns}
            data={tableData}
            setData={setTableData}
            filterPlaceholder="Search..."
            renderRowEditForm={(row, onSave, onCancel) => (
              <EditTariffForm
                defaultValues={row}
                onCancel={onCancel}
                onSubmit={values => {
                  onSave(values)
                  handleSaveTariff(values)
                }}
              />
            )}
          />
        )}
      </div>
    </div>
  )
}
