"use client"

import * as React from "react"
import { useAuth } from "@clerk/clerk-react"
import { Toaster, toast } from "@/components/ui/sonner"
import { DataTable } from "@/components/ui/datatable"
import { shippingFeeColumns } from "@/components/tablecolumns/editshippingfeescol"
import { EditShippingFeeForm } from "@/components/ui/editshippingfeesform"
import type { ShippingFee } from "@/components/tablecolumns/editshippingfeescol"
import { TableSkeleton } from "@/components/ui/tableskeleton"
import { Link } from "react-router-dom"; 


const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080"

type FlattenedShippingFee = {
  country1: { name: string; iso3: string }
  country2: { name: string; iso3: string }
  date?: string
  ton?: number | null
  barrel?: number | null
  mmbtu?: number | null
  lastUpdated?: string
}

export default function EditShippingFeesPage() {
  const { getToken } = useAuth()
  const [tableData, setTableData] = React.useState<ShippingFee[]>([])
  const [loading, setLoading] = React.useState(true)

  React.useEffect(() => {
    const fetchShippingFees = async () => {
      setLoading(true)
      try {
        // Using the flattened endpoint
        const token = await getToken()
        const res = await fetch(`${API_BASE}/shipping-fees/cost/all`, {
          headers: { Authorization: `Bearer ${token}` },
        })

        if (!res.ok) throw new Error("Failed to fetch shipping fees")

        const data: FlattenedShippingFee[] = await res.json()
        
        console.log("Raw API response:", data) // Debug log

        // Backend now returns flattened data, so we can map directly
        const formatted = data.map((entry, index) => ({
          id: `${entry.country1.iso3}-${entry.country2.iso3}-${entry.date}-${index}`,
          originCountry: entry.country1.name,
          originCountryIso3: entry.country1.iso3,
          destinationCountry: entry.country2.name,
          destinationCountryIso3: entry.country2.iso3,
          costPerTon: entry.ton != null ? Number(entry.ton) : 0,
          costPerBarrel: entry.barrel != null ? Number(entry.barrel) : 0,
          costPerMMBtu: entry.mmbtu != null ? Number(entry.mmbtu) : 0,
          lastUpdated: entry.lastUpdated || entry.date || "" 
        }))

        setTableData(formatted)
      } catch (error) {
        console.error("Error fetching shipping fees:", error)
        toast.error("Failed to load shipping fees.")
      } finally {
        setLoading(false)
      }
    }

    fetchShippingFees()
  }, [getToken])

  const handleSaveShippingFee = async (newFee: ShippingFee) => {
    const original = tableData.find(f => f.id === newFee.id)

    if (
      original &&
      original.costPerTon === newFee.costPerTon &&
      original.costPerBarrel === newFee.costPerBarrel &&
      original.costPerMMBtu === newFee.costPerMMBtu
    ) {
      toast.info("No changes detected.")
      return
    }

    try {
      const backend = `${API_BASE}/shipping-fees`
      const token = await getToken()
      
      const response = await fetch(backend, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          country1Iso3: newFee.originCountryIso3,
          country2Iso3: newFee.destinationCountryIso3,
          country1Name: newFee.originCountry,
          country2Name: newFee.destinationCountry,
          shippingFees: [
            {
              date: new Date().toISOString().split("T")[0],
              costs: {
                ton: {
                  cost_per_unit: Number(newFee.costPerTon),
                  unit: "ton"
                },
                barrel: {
                  cost_per_unit: Number(newFee.costPerBarrel),
                  unit: "barrel"
                },
                mmbtu: { 
                  cost_per_unit: Number(newFee.costPerMMBtu), 
                  unit: "mmbtu" }

              }
            }
          ]
        }),
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || "Failed to save shipping fee")
      }

      // Refetch updated data using the flattened endpoint
      
      const res = await fetch(`${API_BASE}/shipping-fees/cost/all`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      
      const data: FlattenedShippingFee[] = await res.json()
      
      const formatted = data.map((entry, index) => ({
        id: `${entry.country1.iso3}-${entry.country2.iso3}-${entry.date}-${index}`,
        originCountry: entry.country1.name,
        originCountryIso3: entry.country1.iso3,
        destinationCountry: entry.country2.name,
        destinationCountryIso3: entry.country2.iso3,
        costPerTon: entry.ton != null ? Number(entry.ton) : 0,
        costPerBarrel: entry.barrel != null ? Number(entry.barrel) : 0,
        costPerMMBtu: entry.mmbtu != null ? Number(entry.mmbtu) : 0,
        lastUpdated: entry.lastUpdated || entry.date || ""
      }))

      setTableData(formatted)
      toast.success("Shipping fee added successfully!")
    } catch (error) {
      console.error(error)
      toast.error(
        error instanceof Error ? error.message : "Failed to update shipping fee."
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
        <h1 className="text-3xl font-bold text-center w-full">
          Edit Shipping Fees
        </h1>
      </div>
      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 p-6 rounded-lg shadow-lg backdrop-blur-sm transition-colors">
        {loading ? (
          <TableSkeleton columns={shippingFeeColumns} />
        ) : (
          <DataTable
            columns={shippingFeeColumns}
            data={tableData}
            setData={setTableData}
            filterPlaceholder="Search..."
            renderRowEditForm={(row, onSave, onCancel) => (
              <EditShippingFeeForm
                defaultValues={row}
                onCancel={onCancel}
                onSubmit={(values) => {
                console.log("PUSHING VALUES - COST PER TON:", values.costPerTon, "MBTU: ", values.costPerMMBtu, "PERBARREL: ", values.costPerBarrel )
                  onSave(values)
                  handleSaveShippingFee(values)
                }}
              />
            )}
          />
        )}
      </div>
    </div>
  )
}
