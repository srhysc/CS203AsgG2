
import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Combobox } from "@/components/ui/combobox";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

type Country = {
  name: string;
  iso3n: number;
  vatRate?: number;
};

type VatRate = {
  rate: number;
  date: string; // ISO string
};

const API_BASE = import.meta.env.VITE_API_URL || "";

// Try multiple possible endpoints for VAT history
async function fetchVatHistory(iso3n: number): Promise<VatRate[]> {
  const candidates = [
    `/countries/${iso3n}/vat`,              // preferred
    `/countries/${iso3n}/vatRates`,         // alt
    `/vat/history?iso3n=${iso3n}`,          // alt
    `/vat?iso3n=${iso3n}`                   // alt
  ];
  for (const path of candidates) {
    try {
      const { data } = await axios.get<VatRate[]>(API_BASE + path);
      if (Array.isArray(data)) return data;
    } catch (_err) {
      // try next candidate
    }
  }
  throw new Error("No VAT history endpoint responded for iso3n=" + iso3n);
}

export default function CountryInfoPage() {
  const [countries, setCountries] = useState<Country[]>([]);
  const [selectedIso3n, setSelectedIso3n] = useState<string>("");
  const [vat, setVat] = useState<VatRate[] | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  // load country list
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

  const options = useMemo(() => countries.map(c => ({
    label: `${c.name} (${String(c.iso3n).padStart(3,"0")})`,
    value: String(c.iso3n)
  })), [countries]);

  // when user selects a country, fetch its VAT history
  useEffect(() => {
    const iso = Number(selectedIso3n);
    if (!iso) { setVat(null); return; }
    let cancelled = false;
    setLoading(true);
    setError("");
    fetchVatHistory(iso)
      .then(list => !cancelled && setVat(list))
      .catch(err => {
        console.error(err);
        if (!cancelled) setError("Could not fetch VAT history for the selected country.");
      })
      .finally(() => !cancelled && setLoading(false));
    return () => { cancelled = true; };
  }, [selectedIso3n]);

  return (
    <div className="max-w-6xl mx-auto p-6 space-y-8">
      <Card className="bg-slate-900/40 border-slate-700">
        <CardHeader>
          <CardTitle className="text-xl">Select a Country</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <Combobox
            options={options}
            value={selectedIso3n}
            onChange={(v) => setSelectedIso3n(v)}
            placeholder="Search country..."
            widthClass="w-full md:w-96"
          />
          {error && <div className="rounded-md border border-red-600 bg-red-900/40 px-3 py-2 text-red-200">{error}</div>}
        </CardContent>
      </Card>

      <Card className="bg-slate-900/40 border-slate-700">
        <CardHeader>
          <CardTitle className="text-xl">VAT Rates</CardTitle>
        </CardHeader>
        <CardContent>
          {!selectedIso3n && <p className="text-slate-300">Choose a country to view VAT history.</p>}
          {loading && <p className="text-slate-300">Loading…</p>}
          {vat && vat.length === 0 && <p className="text-slate-300">No VAT history found.</p>}
          {vat && vat.length > 0 && (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Date</TableHead>
                    <TableHead>VAT %</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {vat.map((row, idx) => (
                    <TableRow key={idx}>
                      <TableCell>{new Date(row.date).toLocaleDateString()}</TableCell>
                      <TableCell>{row.rate}</TableCell>
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
