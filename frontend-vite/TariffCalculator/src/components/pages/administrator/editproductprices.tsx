"use client"

import * as React from "react"
import { DataTable } from "@/components/ui/datatable"
import type { ProductPrice } from "@components/tablecolumns/editproductpricescol"
import { productPriceColumns } from "@/components/tablecolumns/editproductpricescol"
import { EditProductPriceForm } from "@/components/ui/editproductpricesform"
import { Toaster, toast } from "@/components/ui/sonner"

const initialProductPriceData: ProductPrice[] = [
  {
    id: "1",
    productCode: "P001",
    productName: "Premium High Quality Industrial Lubricant (5L Container)",
    price: 49.99,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin"
  },
  {
    id: "2",
    productCode: "P002",
    productName: "Standard Machinery Cleaning Solvent (20L Drum)",
    price: 80.50,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin"
  }
]

function isEqual(obj1: any, obj2: any): boolean {
  return Object.entries(obj1).every(([key, value]) => obj2[key] === value)
}

export default function EditProductPricesPage() {
  const [productPrices, setProductPrice] = React.useState(initialProductPriceData)

  const handleSaveProductPrice = async (updatedPrice: ProductPrice) => {
    const original = productPrices.find(p => p.id === updatedPrice.id)
    if (original && isEqual(original, updatedPrice)) {
      toast.info("No changes detected.")
      return
    }

    try {
      console.log("Saving product price:", updatedPrice)
      setProductPrice(prev =>
        prev.map(p => (p.id === updatedPrice.id ? updatedPrice : p))
      )
      toast.success("Product price updated successfully!")
    } catch (error) {
      console.error(error)
      toast.error("Failed to update product price.")
    }
  }

  return (
    <div className="h-screen w-screen overflow-hidden flex flex-col text-gray-900 dark:text-gray-100 transition-colors">
      <Toaster />
      <a href="/administrator" className="btn-slate absolute top-6 right-6">Back</a>

      <h1 className="text-3xl font-bold text-center mb-6">Edit Product Prices</h1>

      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 p-6 rounded-lg shadow-lg backdrop-blur-sm">
        <DataTable
          columns={productPriceColumns}
          data={productPrices}
          setData={setProductPrice}
          filterPlaceholder="Search products..."
          renderRowEditForm={(row, onSave, onCancel) => (
            <EditProductPriceForm
              defaultValues={row}
              currentUserName="Admin User"
              onCancel={onCancel}
              onSubmit={(values) => {
                onSave(values)
                handleSaveProductPrice(values)
              }}
            />
          )}
        />
      </div>
    </div>
  )
}
