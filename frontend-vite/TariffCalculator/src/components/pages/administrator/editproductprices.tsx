"use client"

import * as React from "react"
import { useAuth } from "@clerk/clerk-react"
import { Toaster, toast } from "@/components/ui/sonner"
import { DataTable } from "@/components/ui/datatable"
import type { ProductPrice } from "@/components/tablecolumns/editproductpricescol"
import { productPriceColumns } from "@/components/tablecolumns/editproductpricescol"
import { EditProductPriceForm } from "@/components/ui/editproductpricesform"
import { TableSkeleton } from "@/components/ui/tableskeleton"
import { Link } from "react-router-dom"; 


const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080"

type PetroleumResponse = {
  hsCode: string
  name: string
  price: number
  date: string
  unit: string
}

export default function EditProductPricesPage() {
  const { getToken } = useAuth()
  const [productPrices, setProductPrices] = React.useState<ProductPrice[]>([])
  const [loading, setLoading] = React.useState(true)

  React.useEffect(() => {
    const fetchProductPrices = async () => {
      setLoading(true)
      try {
        const token = await getToken()

        const productRes = await fetch(`${API_BASE}/petroleum/price-all`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        if (!productRes.ok) throw new Error("Failed to fetch petroleum data")
        const products: PetroleumResponse[] = await productRes.json()

        const formatted: ProductPrice[] = products.map(p => ({
          id: `${p.hsCode}-${p.date}`, 
          productCode: p.hsCode,
          productName: p.name,
          price: p.price,
          lastUpdated: p.date,
          unit: p.unit,
        }))

        setProductPrices(formatted)
      } catch (error) {
        console.error("Error fetching product prices:", error)
        toast.error("Failed to load product prices.")
      } finally {
        setLoading(false)
      }
    }

    fetchProductPrices()
  }, [getToken])

  const handleSaveProductPrice = async (updatedPrice: ProductPrice) => {
  const original = productPrices.find(p => p.id === updatedPrice.id)
  if (original && original.price === updatedPrice.price) {
    toast.info("No changes detected.")
    return
  }
    try {
      const token = await getToken()

      const originalProduct = productPrices.find(p => p.id === updatedPrice.id)

      const requestBody = {
        date: updatedPrice.lastUpdated,
        avgPricePerUnitUsd: updatedPrice.price,
        unit: originalProduct?.unit || "USD per ton"
      }

      const response = await fetch(`${API_BASE}/petroleum/${updatedPrice.productCode}/price-new`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(requestBody),
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || "Failed to save product price")
      }

      // Refresh the data after successful update
      const productRes = await fetch(`${API_BASE}/petroleum/price-all`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      
      if (!productRes.ok) throw new Error("Failed to fetch updated petroleum data")
      const products: PetroleumResponse[] = await productRes.json()
      
      const formatted: ProductPrice[] = products.map(p => ({
        id: `${p.hsCode}-${p.date}`,
        productCode: p.hsCode,
        productName: p.name,
        price: p.price,
        lastUpdated: p.date,
        unit: p.unit,
      }))

      setProductPrices(formatted)
      toast.success("Product price updated successfully!")
    } catch (error) {
      console.error(error)
      toast.error(
        error instanceof Error ? error.message : "Failed to update product price."
      )
    }
  }

  return (
    <div className="h-screen w-screen overflow-hidden flex flex-col text-gray-900 dark:text-gray-100 transition-colors">
      <Toaster />
      <Link to="/administrator" className="btn-slate absolute top-6 right-6">Back</Link>

      <h1 className="text-3xl font-bold text-center mb-6">Edit Product Prices</h1>

      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 p-6 rounded-lg shadow-lg backdrop-blur-sm">
        {loading ? (
          <TableSkeleton columns={productPriceColumns} />
        ) : (
          <DataTable
            columns={productPriceColumns}
            data={productPrices}
            setData={setProductPrices}
            filterPlaceholder="Search by product name or code..."
            renderRowEditForm={(row, onSave, onCancel) => (
              <EditProductPriceForm
                defaultValues={row}
                onCancel={onCancel}
                onSubmit={(values) => {
                  onSave(values)
                  handleSaveProductPrice(values)
                }}
              />
            )}
          />
        )}
      </div>
    </div>
  )
}