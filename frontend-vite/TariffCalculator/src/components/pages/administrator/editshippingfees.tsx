"use client"

import * as React from "react"
import { useAuth } from "@clerk/clerk-react"
import { Toaster, toast } from "@/components/ui/sonner"
import { DataTable } from "@/components/ui/datatable"
import { shippingFeeColumns } from "@/components/tablecolumns/editshippingfeescol"
import { EditShippingFeeForm } from "@/components/ui/editshippingfeesform"
import type { ShippingFee } from "@/components/tablecolumns/editshippingfeescol"
import { TableSkeleton } from "@/components/ui/tableskeleton"

export default function EditShippingFeesPage() {
  const { getToken } = useAuth()
  const [tableData, setTableData] = React.useState<ShippingFee[]>([])
  const [loading, setLoading] = React.useState(true)

  React.useEffect(() => {
    const fetchShippingFees = async () => {
      setLoading(true)
      try {
        // Using the flattened endpoint
        const backend = "http://localhost:8080/shipping-fees/cost/all"
        const token = await getToken()
        const res = await fetch(backend, {
          headers: { Authorization: `Bearer ${token}` },
        })

        if (!res.ok) throw new Error("Failed to fetch shipping fees")

        const data = await res.json()
        
        console.log("Raw API response:", data) // Debug log

        // Backend now returns flattened data, so we can map directly
        const formatted = data.map((entry: any, index: number) => ({
          id: `${entry.country1.iso3}-${entry.country2.iso3}-${entry.date}-${index}`,
          originCountry: entry.country1.name,
          originCountryIso3: entry.country1.iso3,
          destinationCountry: entry.country2.name,
          destinationCountryIso3: entry.country2.iso3,
          costPerTon: entry.ton != null ? Number(entry.ton) : 0,
          costPerBarrel: entry.barrel != null ? Number(entry.barrel) : 0,
          costPerMMBtu: entry.MMBtu != null ? Number(entry.MMBtu) : 0,
          lastUpdated: entry.date || ""
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
    try {
      const backend = "http://localhost:8080/shipping-fees"
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
                MMBtu: {
                  cost_per_unit: Number(newFee.costPerMMBtu),
                  unit: "MMBtu"
                }
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
      
      const res = await fetch("http://localhost:8080/shipping-fees/cost/all", {
        headers: { Authorization: `Bearer ${token}` },
      })
      
      const data = await res.json()
      
      const formatted = data.map((entry: any, index: number) => ({
        id: `${entry.country1.iso3}-${entry.country2.iso3}-${entry.date}-${index}`,
        originCountry: entry.country1.name,
        originCountryIso3: entry.country1.iso3,
        destinationCountry: entry.country2.name,
        destinationCountryIso3: entry.country2.iso3,
        costPerTon: entry.ton != null ? Number(entry.ton) : 0,
        costPerBarrel: entry.barrel != null ? Number(entry.barrel) : 0,
        costPerMMBtu: entry.MMBtu != null ? Number(entry.MMBtu) : 0,
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

  function isEqual(obj1: any, obj2: any): boolean {
    return Object.entries(obj1).every(([key, value]) => obj2[key] === value)
  }

  return (
    <div className="h-screen w-screen overflow-hidden flex flex-col text-gray-900 dark:text-gray-100 transition-colors">
      <Toaster />
      <a href="/administrator" className="btn-slate absolute top-6 right-6">
        Back
      </a>
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
                currentUserName="Admin User"
                onCancel={onCancel}
                onSubmit={(values) => {
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