"use client"

import * as React from "react"
import { DataTable } from "@/components/ui/datatable"
import type { Tariff } from "@/components/tablecolumns/edittariffscol"
import { tariffColumns } from "@/components/tablecolumns/edittariffscol"
import { EditTariffForm } from "@/components/ui/edittariffform"

const initialTariffData: Tariff[] = [
  {
    id: "1",
    productCode: "PET001",
    exportingCountry: "Saudi Arabia",
    importingCountry: "Singapore",
    tariffRate: 0.05,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "2",
    productCode: "PET002",
    exportingCountry: "USA",
    importingCountry: "Malaysia",
    tariffRate: 0.08,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "3",
    productCode: "PET003",
    exportingCountry: "Qatar",
    importingCountry: "Japan",
    tariffRate: 0.04,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "4",
    productCode: "PET004",
    exportingCountry: "Russia",
    importingCountry: "Germany",
    tariffRate: 0.06,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "5",
    productCode: "PET005",
    exportingCountry: "UAE",
    importingCountry: "China",
    tariffRate: 0.07,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "1",
    productCode: "PET001",
    exportingCountry: "Saudi Arabia",
    importingCountry: "Singapore",
    tariffRate: 0.05,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "2",
    productCode: "PET002",
    exportingCountry: "USA",
    importingCountry: "Malaysia",
    tariffRate: 0.08,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "3",
    productCode: "PET003",
    exportingCountry: "Qatar",
    importingCountry: "Japan",
    tariffRate: 0.04,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "4",
    productCode: "PET004",
    exportingCountry: "Russia",
    importingCountry: "Germany",
    tariffRate: 0.06,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "5",
    productCode: "PET005",
    exportingCountry: "UAE",
    importingCountry: "China",
    tariffRate: 0.07,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "1",
    productCode: "PET001",
    exportingCountry: "Saudi Arabia",
    importingCountry: "Singapore",
    tariffRate: 0.05,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "2",
    productCode: "PET002",
    exportingCountry: "USA",
    importingCountry: "Malaysia",
    tariffRate: 0.08,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "3",
    productCode: "PET003",
    exportingCountry: "Qatar",
    importingCountry: "Japan",
    tariffRate: 0.04,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "4",
    productCode: "PET004",
    exportingCountry: "Russia",
    importingCountry: "Germany",
    tariffRate: 0.06,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
  {
    id: "5",
    productCode: "PET005",
    exportingCountry: "UAE",
    importingCountry: "China",
    tariffRate: 0.07,
    lastUpdated: "2025-09-01",
    updatedBy: "Admin",
  },
]

// to be connected to database
const COUNTRY_OPTIONS = [
  { label: "Saudi Arabia", value: "Saudi Arabia" },
  { label: "Singapore", value: "Singapore" },
  { label: "USA", value: "USA" },
  { label: "Malaysia", value: "Malaysia" },
  { label: "Qatar", value: "Qatar" },
  { label: "Japan", value: "Japan" },
  { label: "Russia", value: "Russia" },
  { label: "Germany", value: "Germany" },
  { label: "UAE", value: "UAE" },
  { label: "China", value: "China" },
]



export default function EditTariffsPage() {
  const [tariffData, setTariffData] = React.useState<Tariff[]>(initialTariffData)

  const handleSaveTariff = async (updatedTariff: Tariff) => {
    try {
      // TODO: Add Firebase integration here
      // Example:
      // await addDoc(collection(db, "tariffs"), updatedTariff);
      
      console.log("Saving tariff to Firebase:", updatedTariff)
      
      // Update local state
      setTariffData(prev => 
        prev.map(t => t.id === updatedTariff.id ? updatedTariff : t)
      )
      
      // to add dialog message of success
    } catch (error) {
      console.error("Error updating tariff:", error)
      // to add dialog message of success
    }
    
  }

  return (
    <div className="p-6 min-h-screen text-gray-900 dark:text-gray-100 transition-colors overflow-hidden">
      <h1 className="text-3xl font-bold mb-6">Edit Tariffs</h1>

      <div className="flex-justify center mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 rounded-lg p-6 shadow-lg backdrop-blur-sm transition-colors">

        <DataTable
          columns={tariffColumns}
          data={tariffData}
          setData={setTariffData}
          filterPlaceholder="Search tariffs..."
          renderRowEditForm={(row, onSave, onCancel) => (
            <EditTariffForm
              defaultValues={row}
              currentUserName="Admin User" // TODO: Replace with actual auth user from context/session
              countryOptions={COUNTRY_OPTIONS}
              onCancel={onCancel}
              onSubmit={onSave}
            />
          )}
        />
      </div>
    </div>
  )
}