// src/components/pages/RefineryInfoPage.tsx
import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Combobox } from "@/components/ui/combobox";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

type Refinery = {
  id: number;
  name: string;
  country: string;
  established: string; // ISO date
  active: boolean;
};

type RefineryPrice = {
  date: string; // ISO date
  price: number;
};

const API_BASE = import.meta.env.VITE_API_URL || "";

// Try multiple possible endpoints for refinery historical prices
async function fetchRefineryPrices(refineryId: number): Promise<RefineryPrice[]> {
  const candidates = [
    `/refineries/${refineryId}/prices`,
    `/refineries/${refineryId}/historicalPrices`,
    `/prices?refineryId=${refineryId}`
  ];
  for (const path of candidates) {
    try {
      const { data } = await axios.get<RefineryPrice[]>(API_BASE + path);
      if (Array.isArray(data)) return data;
    } catch (_err) {
      // try next candidate
    }
  }
  throw new Error("No refinery prices endpoint responded for refineryId=" + refineryId);
}

export default function RefineryInfoPage() {
  const [refineries, setRefineries] = useState<Refinery[]>([]);
  const [selectedRefineryId, setSelectedRefineryId] = useState<string>("");
  const [prices, setPrices] = useState<RefineryPrice[] | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  // Load refinery list
  useEffect(() => {
    let cancelled = false;
    const run = async () => {
      setError("");
      try {
        const { data } = await axios.get<Refinery[]>(API_BASE + "/refineries");
        if (!cancelled) setRefineries(data ?? []);
      } catch (e: any) {
        if (!cancelled) setError("Failed to load refineries. Check VITE_API_URL and /refineries.");
        console.error(e);
      }
    };
    run();
    return () => { cancelled = true; };
  }, []);

  const options = useMemo(() => refineries.map(r => ({
    label: `${r.name} (${r.country})`,
    value: String(r.id)
  })), [refineries]);

  // Fetch historical prices when a refinery is selected
  useEffect(() => {
    const id = Number(selectedRefineryId);
    if (!id) { setPrices(null); return; }
    let cancelled = false;
    setLoading(true);
    setError("");
    fetchRefineryPrices(id)
      .then(list => !cancelled && setPrices(list))
      .catch(err => {
        console.error(err);
        if (!cancelled) setError("Could not fetch historical prices for the selected refinery.");
      })
      .finally(() => !cancelled && setLoading(false));
    return () => { cancelled = true; };
  }, [selectedRefineryId]);

  // Find selected refinery details
  const selectedRefinery = refineries.find(r => String(r.id) === selectedRefineryId);

  return (
    <div className="max-w-6xl mx-auto p-6 space-y-8">
      <Card className="bg-slate-900/40 border-slate-700">
        <CardHeader>
          <CardTitle className="text-xl">Select a Refinery</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <Combobox
            options={options}
            value={selectedRefineryId}
            onChange={(v) => setSelectedRefineryId(v)}
            placeholder="Search refinery..."
            widthClass="w-full md:w-96"
          />
          {error && <div className="rounded-md border border-red-600 bg-red-900/40 px-3 py-2 text-red-200">{error}</div>}
        </CardContent>
      </Card>

      {selectedRefinery && (
        <Card className="bg-slate-900/40 border-slate-700">
          <CardHeader>
            <CardTitle className="text-xl">{selectedRefinery.name} Info</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <p><strong>Country:</strong> {selectedRefinery.country}</p>
            <p><strong>Established:</strong> {new Date(selectedRefinery.established).toLocaleDateString()}</p>
            <p><strong>Status:</strong> {selectedRefinery.active ? "Active" : "Inactive"}</p>

            {loading && <p className="text-slate-300">Loading historical prices…</p>}
            {prices && prices.length === 0 && <p className="text-slate-300">No historical prices found.</p>}
            {prices && prices.length > 0 && (
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Date</TableHead>
                      <TableHead>Price (USD)</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {prices.map((row, idx) => (
                      <TableRow key={idx}>
                        <TableCell>{new Date(row.date).toLocaleDateString()}</TableCell>
                        <TableCell>{row.price}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  );
}
