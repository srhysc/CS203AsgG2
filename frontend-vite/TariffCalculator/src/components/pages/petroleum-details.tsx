
import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Combobox } from "@/components/ui/combobox";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

type Petroleum = {
  name: string;
  hsCode: number;
  pricePerUnit?: number;
};

type PricePoint = {
  date: string;   // ISO date string
  price: number;  // per unit in your backend currency
};

const API_BASE = import.meta.env.VITE_API_URL || "";

// Try a few common endpoints for historical price series
async function fetchPriceHistory(hsCode: number): Promise<PricePoint[]> {
  const codes = [String(hsCode), String(hsCode).padStart(6, "0")];
  const candidates = [
    (code:string) => `/petroleum/${code}/prices`,       // preferred
    (code:string) => `/petroleum/${code}/history`,      // alt
    (code:string) => `/petroleum/prices?hsCode=${code}`,// alt
    (code:string) => `/prices/petroleum?hs=${code}`,    // alt
  ];
  for (const formatter of candidates) {
    for (const code of codes) {
      try {
        const { data } = await axios.get<PricePoint[]>(API_BASE + formatter(code));
        if (Array.isArray(data)) return data;
      } catch (_err) {
        // continue
      }
    }
  }
  throw new Error("No petroleum price endpoint responded for hsCode=" + hsCode);
}

export default function PetroleumDetailsPage() {
  const [list, setList] = useState<Petroleum[]>([]);
  const [selectedHS, setSelectedHS] = useState<string>("");
  const [series, setSeries] = useState<PricePoint[] | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  // load petroleum list
  useEffect(() => {
    let cancelled = false;
    const run = async () => {
      setError("");
      try {
        const { data } = await axios.get<Petroleum[]>(API_BASE + "/petroleum");
        if (!cancelled) setList(data ?? []);
      } catch (e:any) {
        if (!cancelled) setError("Failed to load petroleum list. Check VITE_API_URL and /petroleum.");
        console.error(e);
      }
    };
    run();
    return () => { cancelled = true; };
  }, []);

  const options = useMemo(() => list.map(p => ({
    label: `${p.name} (HS ${String(p.hsCode).padStart(6,"0")})`,
    value: String(p.hsCode)
  })), [list]);

  // when user selects a petroleum item, fetch its price history
  useEffect(() => {
    const hs = Number(selectedHS);
    if (!hs) { setSeries(null); return; }
    let cancelled = false;
    setLoading(true);
    setError("");
    fetchPriceHistory(hs)
      .then(data => !cancelled && setSeries(data))
      .catch(err => {
        console.error(err);
        if (!cancelled) setError("Could not fetch price history for the selected petroleum item.");
      })
      .finally(() => !cancelled && setLoading(false));
    return () => { cancelled = true; };
  }, [selectedHS]);

  return (
    <div className="max-w-6xl mx-auto p-6 space-y-8">
      <Card className="bg-slate-900/40 border-slate-700">
        <CardHeader>
          <CardTitle className="text-xl">Select Petroleum</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <Combobox
            options={options}
            value={selectedHS}
            onChange={(v) => setSelectedHS(v)}
            placeholder="Search petroleum..."
            widthClass="w-full md:w-96"
          />
          {error && <div className="rounded-md border border-red-600 bg-red-900/40 px-3 py-2 text-red-200">{error}</div>}
        </CardContent>
      </Card>

      <Card className="bg-slate-900/40 border-slate-700">
        <CardHeader>
          <CardTitle className="text-xl">Historical Prices</CardTitle>
        </CardHeader>
        <CardContent>
          {!selectedHS && <p className="text-slate-300">Choose a petroleum type to view historical prices.</p>}
          {loading && <p className="text-slate-300">Loading…</p>}
          {series && series.length === 0 && <p className="text-slate-300">No price history found.</p>}
          {series && series.length > 0 && (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Date</TableHead>
                    <TableHead>Price / Unit</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {series.map((pt, idx) => (
                    <TableRow key={idx}>
                      <TableCell>{new Date(pt.date).toLocaleDateString()}</TableCell>
                      <TableCell>{pt.price}</TableCell>
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
