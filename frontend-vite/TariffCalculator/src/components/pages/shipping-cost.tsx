
import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Combobox } from "@/components/ui/combobox";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

type Country = {
  name: string;
  iso3n: number;
};

type ShippingPoint = {
  date: string;     // ISO string
  cost: number;     // shipping cost
  currency?: string;
};

const API_BASE = import.meta.env.VITE_API_URL || "";

// Query the backend for shipping history between two ISO3n codes.
// Tries several common endpoint shapes and returns the first successful array.
async function fetchShippingHistory(originIso3n: number, destIso3n: number): Promise<ShippingPoint[]> {
  const o = String(originIso3n).padStart(3, "0");
  const d = String(destIso3n).padStart(3, "0");

  const candidates = [
    `${API_BASE}/shipping/costs?origin=${o}&destination=${d}`,
    `${API_BASE}/shipping/history?origin=${o}&destination=${d}`,
    `${API_BASE}/shipping/${o}/${d}/history`,
    `${API_BASE}/routes/${o}/${d}/shipping-costs`,
  ];

  for (const url of candidates) {
    try {
      const { data } = await axios.get<ShippingPoint[]>(url);
      if (Array.isArray(data)) return data;
    } catch (_err) {
      // continue to next candidate
    }
  }
  throw new Error("No shipping history endpoint responded for origin=" + o + " destination=" + d);
}

export default function ShippingCostPage() {
  const [countries, setCountries] = useState<Country[]>([]);
  const [origin, setOrigin] = useState<string>("");
  const [destination, setDestination] = useState<string>("");
  const [series, setSeries] = useState<ShippingPoint[] | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  // Load country list from backend
  useEffect(() => {
    let cancelled = false;
    const run = async () => {
      setError("");
      try {
        const { data } = await axios.get<Country[]>(API_BASE + "/countries");
        if (!cancelled) setCountries(data ?? []);
      } catch (e:any) {
        if (!cancelled) setError("Failed to load countries. Check VITE_API_URL and /countries.");
        console.error(e);
      }
    };
    run();
    return () => { cancelled = true; };
  }, []);

  const countryOptions = useMemo(() => countries.map((c) => ({
    label: `${c.name} (${String(c.iso3n).padStart(3, "0")})`,
    value: String(c.iso3n),
  })), [countries]);

  // When both origin & destination are selected, fetch history
  useEffect(() => {
    const o = Number(origin);
    const d = Number(destination);
    if (!o || !d) { setSeries(null); return; }

    let cancelled = false;
    setLoading(true);
    setError("");

    fetchShippingHistory(o, d)
      .then((data) => { if (!cancelled) setSeries(data); })
      .catch((err) => {
        console.error(err);
        if (!cancelled) setError("Could not fetch shipping history for the selected route.");
      })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [origin, destination]);

  return (
    <div className="max-w-6xl mx-auto p-6 space-y-8">
      <Card className="bg-slate-900/40 border-slate-700">
        <CardHeader>
          <CardTitle className="text-xl">Select Route</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex flex-col md:flex-row gap-3">
            <div className="flex-1">
              <p className="mb-1 text-sm text-slate-300">Origin</p>
              <Combobox
                options={countryOptions}
                value={origin}
                onChange={(v) => setOrigin(v)}
                placeholder="Search origin country…"
                widthClass="w-full"
              />
            </div>
            <div className="flex-1">
              <p className="mb-1 text-sm text-slate-300">Destination</p>
              <Combobox
                options={countryOptions}
                value={destination}
                onChange={(v) => setDestination(v)}
                placeholder="Search destination country…"
                widthClass="w-full"
              />
            </div>
          </div>
          {error && <div className="rounded-md border border-red-600 bg-red-900/40 px-3 py-2 text-red-200">{error}</div>}
        </CardContent>
      </Card>

      <Card className="bg-slate-900/40 border-slate-700">
        <CardHeader>
          <CardTitle className="text-xl">Historical Shipping Costs</CardTitle>
        </CardHeader>
        <CardContent>
          {(!origin || !destination) && (
            <p className="text-slate-300">Choose both origin and destination to view history.</p>
          )}
          {loading && <p className="text-slate-300">Loading…</p>}
          {series && series.length === 0 && <p className="text-slate-300">No history found.</p>}
          {series && series.length > 0 && (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Date</TableHead>
                    <TableHead>Cost</TableHead>
                    <TableHead>Currency</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {series.map((row, idx) => (
                    <TableRow key={idx}>
                      <TableCell>{new Date(row.date).toLocaleDateString()}</TableCell>
                      <TableCell>{row.cost}</TableCell>
                      <TableCell>{row.currency ?? "-"}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
