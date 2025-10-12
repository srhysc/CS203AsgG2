"use client"
import { DataTable } from "@/components/ui/datatable"
import type { Tariff } from "@/components/columns/edittariffscol"
import { tariffColumns } from "@/components/columns/edittariffscol"

const tariffData: Tariff[] = [
  {
    tariffCode: "PET001",
    tariffName: "Crude Oil Basic Tariff",
    exportingCountry: "Saudi Arabia",
    importingCountry: "Singapore",
    tariffRate: 0.05,
    status: "active",
    effectiveFrom: "2025-01-01",
    effectiveTo: "2025-12-31",
  },
  {
    tariffCode: "PET002",
    tariffName: "Refined Petroleum Products",
    exportingCountry: "USA",
    importingCountry: "Malaysia",
    tariffRate: 0.08,
    status: "pending",
    effectiveFrom: "2025-06-01",
    effectiveTo: "2025-12-31",
  },
  {
    tariffCode: "PET003",
    tariffName: "Natural Gas Tariff",
    exportingCountry: "Qatar",
    importingCountry: "Japan",
    tariffRate: 0.04,
    status: "active",
    effectiveFrom: "2025-03-01",
    effectiveTo: "2025-12-31",
  },
  {
    tariffCode: "PET004",
    tariffName: "Crude Oil Special Tariff",
    exportingCountry: "Russia",
    importingCountry: "Germany",
    tariffRate: 0.06,
    status: "inactive",
    effectiveFrom: "2024-01-01",
    effectiveTo: "2024-12-31",
  },
]



export default function EditTariffsPage() {
  return (
    <div className="p-6 min-h-screen text-gray-900 dark:text-gray-100 transition-colors">
      <h1 className="text-2xl font-bold mb-4">Edit Tariffs</h1>

      <div className="mx-auto max-w-6xl bg-white/90 dark:bg-gray-800/80 rounded-md p-4 shadow-md backdrop-blur-sm transition-colors">
        <DataTable
          columns={tariffColumns}
          data={tariffData}
          filterPlaceholder="Search tariffs..."
        />
      </div>
    </div>
  )
}
