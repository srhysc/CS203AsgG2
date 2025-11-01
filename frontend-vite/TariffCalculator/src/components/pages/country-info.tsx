
// import React, { useEffect, useMemo, useState } from "react";
// import axios from "axios";
// import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
// import { Combobox } from "@/components/ui/combobox";
// import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

// type Country = {
//   name: string;
//   iso3n: number;
//   vatRate?: number;
// };

// type VatRate = {
//   rate: number;
//   date: string; // ISO string
// };

// const API_BASE = import.meta.env.VITE_API_URL || "";

// // Try multiple possible endpoints for VAT history
// async function fetchVatHistory(iso3n: number): Promise<VatRate[]> {
//   const candidates = [
//     `/countries/${iso3n}/vat`,              // preferred
//     `/countries/${iso3n}/vatRates`,         // alt
//     `/vat/history?iso3n=${iso3n}`,          // alt
//     `/vat?iso3n=${iso3n}`                   // alt
//   ];
//   for (const path of candidates) {
//     try {
//       const { data } = await axios.get<VatRate[]>(API_BASE + path);
//       if (Array.isArray(data)) return data;
//     } catch (_err) {
//       // try next candidate
//     }
//   }
//   throw new Error("No VAT history endpoint responded for iso3n=" + iso3n);
// }

// export default function CountryInfoPage() {
//   const [countries, setCountries] = useState<Country[]>([]);
//   const [selectedIso3n, setSelectedIso3n] = useState<string>("");
//   const [vat, setVat] = useState<VatRate[] | null>(null);
//   const [loading, setLoading] = useState<boolean>(false);
//   const [error, setError] = useState<string>("");

//   // load country list
//   useEffect(() => {
//     let cancelled = false;
//     const run = async () => {
//       setError("");
//       try {
//         const { data } = await axios.get<Country[]>(API_BASE + "/countries");
//         if (!cancelled) setCountries(data ?? []);
//       } catch (e:any) {
//         if (!cancelled) setError("Failed to load countries. Check VITE_API_URL and /countries.");
//         console.error(e);
//       }
//     };
//     run();
//     return () => { cancelled = true; };
//   }, []);

//   const options = useMemo(() => countries.map(c => ({
//     label: `${c.name} (${String(c.iso3n).padStart(3,"0")})`,
//     value: String(c.iso3n)
//   })), [countries]);

//   // when user selects a country, fetch its VAT history
//   useEffect(() => {
//     const iso = Number(selectedIso3n);
//     if (!iso) { setVat(null); return; }
//     let cancelled = false;
//     setLoading(true);
//     setError("");
//     fetchVatHistory(iso)
//       .then(list => !cancelled && setVat(list))
//       .catch(err => {
//         console.error(err);
//         if (!cancelled) setError("Could not fetch VAT history for the selected country.");
//       })
//       .finally(() => !cancelled && setLoading(false));
//     return () => { cancelled = true; };
//   }, [selectedIso3n]);

//   return (
//     <div className="max-w-6xl mx-auto p-6 space-y-8">
//       <Card className="bg-slate-900/40 border-slate-700">
//         <CardHeader>
//           <CardTitle className="text-xl">Select a Country</CardTitle>
//         </CardHeader>
//         <CardContent className="space-y-4">
//           <Combobox
//             options={options}
//             value={selectedIso3n}
//             onChange={(v) => setSelectedIso3n(v)}
//             placeholder="Search country..."
//             widthClass="w-full md:w-96"
//           />
//           {error && <div className="rounded-md border border-red-600 bg-red-900/40 px-3 py-2 text-red-200">{error}</div>}
//         </CardContent>
//       </Card>

//       <Card className="bg-slate-900/40 border-slate-700">
//         <CardHeader>
//           <CardTitle className="text-xl">VAT Rates</CardTitle>
//         </CardHeader>
//         <CardContent>
//           {!selectedIso3n && <p className="text-slate-300">Choose a country to view VAT history.</p>}
//           {loading && <p className="text-slate-300">Loading…</p>}
//           {vat && vat.length === 0 && <p className="text-slate-300">No VAT history found.</p>}
//           {vat && vat.length > 0 && (
//             <div className="overflow-x-auto">
//               <Table>
//                 <TableHeader>
//                   <TableRow>
//                     <TableHead>Date</TableHead>
//                     <TableHead>VAT %</TableHead>
//                   </TableRow>
//                 </TableHeader>
//                 <TableBody>
//                   {vat.map((row, idx) => (
//                     <TableRow key={idx}>
//                       <TableCell>{new Date(row.date).toLocaleDateString()}</TableCell>
//                       <TableCell>{row.rate}</TableCell>
//                     </TableRow>
//                   ))}
//                 </TableBody>
//               </Table>
//             </div>
//           )}
//         </CardContent>
//       </Card>
//     </div>
//   );
// }import React, { useEffect, useState } from "react";
import React, { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Globe, Search, Loader2, BarChart2 } from "lucide-react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

// Types
interface Country {
  name: string;
  iso3n: number;
  vatRate?: number;
}

interface VatRate {
  rate: number;
  date: string;
}

const API_BASE = import.meta.env.VITE_API_URL || "";

// VAT Rate Chart Component
function VatRateChart({ rates }: { rates: VatRate[] }) {
  return (
    <div className="w-full h-[300px] mb-6 p-4 bg-slate-900/50 rounded-lg border border-white/10">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          data={rates}
          margin={{ top: 10, right: 30, left: 10, bottom: 10 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#ffffff20" />
          <XAxis 
            dataKey="date" 
            stroke="#94a3b8"
            tick={{ fill: '#94a3b8' }}
          />
          <YAxis 
            stroke="#94a3b8"
            tick={{ fill: '#94a3b8' }}
            label={{ 
              value: 'VAT Rate (%)', 
              angle: -90, 
              position: 'insideLeft',
              fill: '#94a3b8'
            }}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: '#1e1b4b',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              borderRadius: '6px',
            }}
            labelStyle={{ color: '#94a3b8' }}
            itemStyle={{ color: '#dcff1a' }}
          />
          <Line
            type="monotone"
            dataKey="rate"
            stroke="#dcff1a"
            strokeWidth={2}
            dot={{ fill: '#dcff1a', strokeWidth: 2 }}
            activeDot={{ r: 8 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}

// Try multiple endpoints for VAT history
async function fetchVatHistory(iso3n: number): Promise<VatRate[]> {
  const candidates = [
    `/countries/${iso3n}/vat`,
    `/countries/${iso3n}/vatRates`,
    `/vat/history?iso3n=${iso3n}`,
    `/vat?iso3n=${iso3n}`
  ];
  
  for (const path of candidates) {
    try {
      const { data } = await axios.get<VatRate[]>(API_BASE + path);
      if (Array.isArray(data)) return data;
    } catch (_err) {
      continue;
    }
  }
  throw new Error(`No VAT history endpoint responded for iso3n=${iso3n}`);
}

export default function CountryInfoPage() {
  const [countries, setCountries] = useState<Country[]>([]);
  const [selectedCountry, setSelectedCountry] = useState<string>("");
  const [dateString, setDateString] = useState<string>("");
  const [vatRates, setVatRates] = useState<VatRate[] | null>(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchCountries = async () => {
      setLoading(true);
      try {
        const { data } = await axios.get<Country[]>(`${API_BASE}/countries`);
        setCountries(data || []);
      } catch (err) {
        setError("Failed to load countries. Please try again later.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchCountries();
  }, []);

  useEffect(() => {
    if (!selectedCountry) {
      setVatRates(null);
      return;
    }

    const country = countries.find(c => c.name === selectedCountry);
    if (!country?.iso3n) return;

    const fetchRates = async () => {
      setLoading(true);
      setError("");
      try {
        const data = await fetchVatHistory(country.iso3n);
        setVatRates(data);
      } catch (err) {
        setError("Failed to fetch VAT rates");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchRates();
  }, [selectedCountry]);

  const filteredRates = vatRates?.filter(rate => {
    if (!dateString) return true;
    return rate.date <= dateString;
  });

  const currentRate = dateString && filteredRates ? 
    filteredRates[filteredRates.length - 1] : null;

  const selectedCountryData = countries.find(c => c.name === selectedCountry);

  return (
    <div className="flex-1 w-full min-h-screen">
      <div className="w-full max-w-7xl mx-auto px-4 py-8 space-y-8">
        {/* Header */}
        <div className="text-center space-y-4">
          <h1 className="text-5xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
            Country VAT Rates
          </h1>
          <p className="text-xl text-gray-400">
            View VAT rates and their history
          </p>
        </div>

        {/* Search Section */}
        <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
          <CardHeader>
            <CardTitle className="text-2xl text-white">Search Countries</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex flex-col md:flex-row gap-6">
              {/* Country Selection */}
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Select Country
                </label>
                <Popover open={open} onOpenChange={setOpen}>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      role="combobox"
                      aria-expanded={open}
                      className="w-full justify-between bg-slate-800/50 border-white/10 text-white h-11 text-lg"
                    >
                      {selectedCountry || "Select a country..."}
                      <Search className="ml-2 h-5 w-5 shrink-0 opacity-50" />
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-[400px] p-0 bg-slate-900 border-white/10">
                    <Command>
                      <CommandInput 
                        placeholder="Search countries..." 
                        className="h-11 text-lg text-white"
                      />
                      <CommandEmpty className="text-lg py-4 text-gray-400">
                        No country found.
                      </CommandEmpty>
                      <CommandGroup>
                        {countries.map((country) => (
                          <CommandItem
                            key={country.iso3n}
                            value={country.name}
                            onSelect={() => {
                              setSelectedCountry(country.name);
                              setOpen(false);
                            }}
                            className="cursor-pointer text-lg py-3 text-white hover:bg-white/10"
                          >
                            <Globe className="mr-3 h-5 w-5 text-[#dcff1a]" />
                            {country.name} ({String(country.iso3n).padStart(3, "0")})
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    </Command>
                  </PopoverContent>
                </Popover>
              </div>

              {/* Date Filter */}
              <div className="w-full md:w-[240px]">
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Filter by Date
                </label>
                <Input
                  type="date"
                  value={dateString}
                  onChange={(e) => setDateString(e.target.value)}
                  className="w-full bg-slate-800/50 border-white/10 text-white h-11 text-lg"
                />
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Loading State */}
        {loading && (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="h-8 w-8 animate-spin text-[#dcff1a]" />
          </div>
        )}

        {/* Results Section */}
        {selectedCountryData && vatRates && !loading && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Details Card */}
            <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
              <CardHeader className="p-6">
                <CardTitle className="text-3xl text-white mb-6">
                  {selectedCountryData.name}
                </CardTitle>
                <div className="space-y-6">
                  <div className="flex items-center gap-4">
                    <Globe className="w-6 h-6 text-[#dcff1a]" />
                    <div>
                      <p className="text-sm text-gray-400">ISO Code</p>
                      <p className="text-lg text-gray-300">
                        {String(selectedCountryData.iso3n).padStart(3, "0")}
                      </p>
                    </div>
                  </div>
                  {currentRate && (
                    <div className="flex items-center gap-4">
                      <BarChart2 className="w-6 h-6 text-[#dcff1a]" />
                      <div>
                        <p className="text-sm text-gray-400">Current VAT Rate</p>
                        <p className="text-lg text-[#dcff1a]">{currentRate.rate}%</p>
                      </div>
                    </div>
                  )}
                </div>
              </CardHeader>
            </Card>

            {/* VAT History Card */}
            <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
              <CardHeader className="p-6">
                <CardTitle className="text-3xl text-white mb-6">
                  VAT Rate History
                </CardTitle>
                {filteredRates && filteredRates.length > 0 ? (
                  <div className="space-y-6">
                    <VatRateChart rates={filteredRates} />
                    <div className="overflow-auto max-h-[300px]">
                      <Table>
                        <TableHeader>
                          <TableRow className="hover:bg-transparent">
                            <TableHead className="text-gray-400 text-lg">Date</TableHead>
                            <TableHead className="text-gray-400 text-lg">VAT Rate</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {filteredRates.map((rate, idx) => (
                            <TableRow key={idx} className="hover:bg-white/5">
                              <TableCell className="text-gray-300 text-lg">
                                {rate.date}
                              </TableCell>
                              <TableCell className="text-[#dcff1a] text-lg font-medium">
                                {rate.rate}%
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </div>
                  </div>
                ) : (
                  <p className="text-gray-400 text-lg">
                    No VAT rate data available for the selected period
                  </p>
                )}
              </CardHeader>
            </Card>
          </div>
        )}

        {/* Error Display */}
        {error && (
          <div className="text-red-400 p-6 rounded-lg bg-red-900/20 border border-red-900">
            <p className="text-lg">{error}</p>
          </div>
        )}
      </div>
    </div>
  );
}