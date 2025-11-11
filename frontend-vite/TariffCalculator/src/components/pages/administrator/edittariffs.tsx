"use client"

import * as React from "react"
import { useAuth } from "@clerk/clerk-react"
import { Toaster, toast } from "@/components/ui/sonner"
import { DataTable } from "@/components/ui/datatable"
import { tariffColumns } from "@/components/tablecolumns/edittariffscol"
import type { Tariff } from "@/components/tablecolumns/edittariffscol"

const COUNTRY_OPTIONS = [
  { label: "Saudi Arabia (SAU)", value: "SAU" },
  { label: "Singapore (SGP)", value: "SGP" },
  { label: "USA (USA)", value: "USA" },
  { label: "Malaysia (MYS)", value: "MYS" },
  { label: "Qatar (QAT)", value: "QAT" },
  { label: "Japan (JPN)", value: "JPN" },
  { label: "Russia (RUS)", value: "RUS" },
  { label: "Germany (DEU)", value: "DEU" },
  { label: "UAE (ARE)", value: "ARE" },
  { label: "China (CHN)", value: "CHN" },
]

function EditTariffForm({
  defaultValues,
  countryOptions,
  onCancel,
  onSubmit,
}: {
  defaultValues: Tariff
  countryOptions: { label: string; value: string }[]
  onCancel: () => void
  onSubmit: (values: Tariff) => Promise<void>
}) {
  const [form, setForm] = React.useState<Tariff>(defaultValues)

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <form
        className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg flex flex-col gap-4 min-w-[320px]"
        onSubmit={async e => {
          e.preventDefault()
          await onSubmit(form)
        }}
      >
        <label>
          Country Code
          <select
            value={form.importingCountry}
            onChange={e => setForm({ ...form, importingCountry: e.target.value })}
            className="w-full p-2 rounded border"
            required
          >
            <option value="">Select country</option>
            {countryOptions.map(opt => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </label>
        <label>
          Tariff Rate
          <input
            type="number"
            step="0.01"
            value={form.tariffRate}
            onChange={e => setForm({ ...form, tariffRate: Number(e.target.value) })}
            className="w-full p-2 rounded border"
            required
          />
        </label>
        <label>
          Year
          <input
            type="number"
            value={form.lastUpdated ? new Date(form.lastUpdated).getFullYear() : new Date().getFullYear()}
            onChange={e => setForm({ ...form, lastUpdated: `${e.target.value}-01-01` })}
            className="w-full p-2 rounded border"
            required
          />
        </label>
        <div className="flex gap-2 justify-end">
          <button type="button" className="btn-slate" onClick={onCancel}>Cancel</button>
          <button type="submit" className="btn-slate">Save</button>
        </div>
      </form>
    </div>
  )
}

export default function EditTariffsPage() {
  const { getToken } = useAuth()
  const [tableData, setTableData] = React.useState<Tariff[]>([])
  const [loading, setLoading] = React.useState(true)
  const [editingTariff, setEditingTariff] = React.useState<Tariff | null>(null)

  React.useEffect(() => {
    const fetchTariffs = async () => {
      setLoading(true)
      try {
        const backend = `${import.meta.env.VITE_API_URL}/wits/mfnrates`
        const token = await getToken()
        const res = await fetch(backend, {
          headers: { Authorization: `Bearer ${token}` },
        })

        if (!res.ok) throw new Error("Failed to fetch MFN rates")

        const data = await res.json()
        // Map backend fields to table columns
        const formatted = data.map((entry: any, index: number) => ({
          id: `${entry.countryIso3}-${entry.year}-${index}`,
          importingCountry: entry.countryIso3,
          tariffRate: entry.mfnAve / 100,
          lastUpdated: entry.year ? `${entry.year}-01-01` : "",
        }))
        setTableData(formatted)
      } catch (error) {
        toast.error("Failed to load MFN rates.")
        setTableData([])
      } finally {
        setLoading(false)
      }
    }

    fetchTariffs()
  }, [getToken])

  const handleSaveTariff = async (newTariff: Tariff) => {
    try {
      const backend = `${import.meta.env.VITE_API_URL}/wits/mfnrate`
      const token = await getToken()

      const payload = {
        countryIso3: newTariff.importingCountry,
        mfnAve: Number(newTariff.tariffRate),
        year: newTariff.lastUpdated
          ? new Date(newTariff.lastUpdated).getFullYear()
          : new Date().getFullYear(),
      }

      const response = await fetch(backend, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || "Failed to save MFN rate")
      }

      // Update tableData locally for instant feedback
      setTableData(prev => {
        const existsIdx = prev.findIndex(
          t => t.importingCountry === newTariff.importingCountry &&
               t.lastUpdated === newTariff.lastUpdated
        )
        if (existsIdx !== -1) {
          const updated = [...prev]
          updated[existsIdx] = { ...newTariff, id: prev[existsIdx].id }
          return updated
        } else {
          return [...prev, { ...newTariff, id: `${newTariff.importingCountry}-${newTariff.lastUpdated}-${prev.length}` }]
        }
      })
      setEditingTariff(null)
      toast.success("MFN rate added/updated successfully!")
    } catch (error) {
      toast.error(
        error instanceof Error ? error.message : "Failed to update MFN rate."
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
        <h1 className="text-3xl font-bold text-center w-full">
          Edit MFN Tariffs
        </h1>
      </div>
      <div className="mx-auto max-w-7xl bg-white/90 dark:bg-gray-800/80 rounded-lg p-6 shadow-lg backdrop-blur-sm transition-colors">
        {loading ? (
          <div className="text-center py-8">Loading...</div>
        ) : (
          <DataTable
            columns={tariffColumns}
            data={tableData}
            setData={setTableData}
            filterPlaceholder="Search..."
            renderRowEditForm={(row, onSave, onCancel) => (
              <EditTariffForm
                defaultValues={row}
                countryOptions={COUNTRY_OPTIONS}
                onCancel={onCancel}
                onSubmit={async (values) => {
                  onSave(values)
                  await handleSaveTariff(values)
                }}
              />
            )}
          />
        )}
        {editingTariff && (
          <EditTariffForm
            defaultValues={editingTariff}
            countryOptions={COUNTRY_OPTIONS}
            onCancel={() => setEditingTariff(null)}
            onSubmit={handleSaveTariff}
          />
        )}
      </div>
    </div>
  )
}
