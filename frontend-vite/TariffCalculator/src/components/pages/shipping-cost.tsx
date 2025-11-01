
// import React, { useEffect, useMemo, useState } from "react";
// import axios from "axios";
// import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
// import { Combobox } from "@/components/ui/combobox";
// import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

// type Country = {
//   name: string;
//   iso3n: number;
// };

// type ShippingPoint = {
//   date: string;     // ISO string
//   cost: number;     // shipping cost
//   currency?: string;
// };

// const API_BASE = import.meta.env.VITE_API_URL || "";

// // Query the backend for shipping history between two ISO3n codes.
// // Tries several common endpoint shapes and returns the first successful array.
// async function fetchShippingHistory(originIso3n: number, destIso3n: number): Promise<ShippingPoint[]> {
//   const o = String(originIso3n).padStart(3, "0");
//   const d = String(destIso3n).padStart(3, "0");

//   const candidates = [
//     `${API_BASE}/shipping/costs?origin=${o}&destination=${d}`,
//     `${API_BASE}/shipping/history?origin=${o}&destination=${d}`,
//     `${API_BASE}/shipping/${o}/${d}/history`,
//     `${API_BASE}/routes/${o}/${d}/shipping-costs`,
//   ];

//   for (const url of candidates) {
//     try {
//       const { data } = await axios.get<ShippingPoint[]>(url);
//       if (Array.isArray(data)) return data;
//     } catch (_err) {
//       // continue to next candidate
//     }
//   }
//   throw new Error("No shipping history endpoint responded for origin=" + o + " destination=" + d);
// }

// export default function ShippingCostPage() {
//   const [countries, setCountries] = useState<Country[]>([]);
//   const [origin, setOrigin] = useState<string>("");
//   const [destination, setDestination] = useState<string>("");
//   const [series, setSeries] = useState<ShippingPoint[] | null>(null);
//   const [loading, setLoading] = useState<boolean>(false);
//   const [error, setError] = useState<string>("");

//   // Load country list from backend
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

//   const countryOptions = useMemo(() => countries.map((c) => ({
//     label: `${c.name} (${String(c.iso3n).padStart(3, "0")})`,
//     value: String(c.iso3n),
//   })), [countries]);

//   // When both origin & destination are selected, fetch history
//   useEffect(() => {
//     const o = Number(origin);
//     const d = Number(destination);
//     if (!o || !d) { setSeries(null); return; }

//     let cancelled = false;
//     setLoading(true);
//     setError("");

//     fetchShippingHistory(o, d)
//       .then((data) => { if (!cancelled) setSeries(data); })
//       .catch((err) => {
//         console.error(err);
//         if (!cancelled) setError("Could not fetch shipping history for the selected route.");
//       })
//       .finally(() => { if (!cancelled) setLoading(false); });

//     return () => { cancelled = true; };
//   }, [origin, destination]);

//   return (
//     <div className="max-w-6xl mx-auto p-6 space-y-8">
//       <Card className="bg-slate-900/40 border-slate-700">
//         <CardHeader>
//           <CardTitle className="text-xl">Select Route</CardTitle>
//         </CardHeader>
//         <CardContent className="space-y-4">
//           <div className="flex flex-col md:flex-row gap-3">
//             <div className="flex-1">
//               <p className="mb-1 text-sm text-slate-300">Origin</p>
//               <Combobox
//                 options={countryOptions}
//                 value={origin}
//                 onChange={(v) => setOrigin(v)}
//                 placeholder="Search origin country…"
//                 widthClass="w-full"
//               />
//             </div>
//             <div className="flex-1">
//               <p className="mb-1 text-sm text-slate-300">Destination</p>
//               <Combobox
//                 options={countryOptions}
//                 value={destination}
//                 onChange={(v) => setDestination(v)}
//                 placeholder="Search destination country…"
//                 widthClass="w-full"
//               />
//             </div>
//           </div>
//           {error && <div className="rounded-md border border-red-600 bg-red-900/40 px-3 py-2 text-red-200">{error}</div>}
//         </CardContent>
//       </Card>

//       <Card className="bg-slate-900/40 border-slate-700">
//         <CardHeader>
//           <CardTitle className="text-xl">Historical Shipping Costs</CardTitle>
//         </CardHeader>
//         <CardContent>
//           {(!origin || !destination) && (
//             <p className="text-slate-300">Choose both origin and destination to view history.</p>
//           )}
//           {loading && <p className="text-slate-300">Loading…</p>}
//           {series && series.length === 0 && <p className="text-slate-300">No history found.</p>}
//           {series && series.length > 0 && (
//             <div className="overflow-x-auto">
//               <Table>
//                 <TableHeader>
//                   <TableRow>
//                     <TableHead>Date</TableHead>
//                     <TableHead>Cost</TableHead>
//                     <TableHead>Currency</TableHead>
//                   </TableRow>
//                 </TableHeader>
//                 <TableBody>
//                   {series.map((row, idx) => (
//                     <TableRow key={idx}>
//                       <TableCell>{new Date(row.date).toLocaleDateString()}</TableCell>
//                       <TableCell>{row.cost}</TableCell>
//                       <TableCell>{row.currency ?? "-"}</TableCell>
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
import { Ship, Search, Loader2, BarChart2 } from "lucide-react";
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
}

interface ShippingPoint {
  date: string;
  cost: number;
  currency?: string;
}

// Animations
const ShipAnimations = () => (
  <style>{`
    @keyframes ship-progress {
      0% { transform: scaleX(0); transform-origin: left; }
      100% { transform: scaleX(1); transform-origin: left; }
    }
    @keyframes ship-bounce {
      0%, 100% { transform: translateY(0); }
      50% { transform: translateY(-4px); }
    }
  `}</style>
);

// Components
const RouteVisualization = ({ origin, destination }: { origin: string; destination: string }) => (
  <div className="w-full py-12 bg-slate-900/50 rounded-lg border border-white/10">
    <ShipAnimations />
    <div className="flex items-center justify-center gap-8">
      <div className="text-xl font-medium text-white px-6 py-3 bg-slate-800/50 rounded-lg">
        {origin}
      </div>
      <div className="relative w-48 h-2 bg-slate-800">
        <div 
          className="absolute inset-0 bg-[#dcff1a]"
          style={{ animation: 'ship-progress 2s infinite' }}
        />
        <div className="absolute -top-3 -right-3">
          <Ship 
            className="w-6 h-6 text-[#dcff1a]"
            style={{ animation: 'ship-bounce 1s ease-in-out infinite' }}
          />
        </div>
      </div>
      <div className="text-xl font-medium text-white px-6 py-3 bg-slate-800/50 rounded-lg">
        {destination}
      </div>
    </div>
  </div>
);

const CostHistoryChart = ({ costs }: { costs: ShippingPoint[] }) => (
  <div className="w-full h-[300px] mb-6 p-4 bg-slate-900/50 rounded-lg border border-white/10">
    <ResponsiveContainer width="100%" height="100%">
      <LineChart data={costs} margin={{ top: 10, right: 30, left: 10, bottom: 10 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="#ffffff20" />
        <XAxis dataKey="date" stroke="#94a3b8" tick={{ fill: '#94a3b8' }} />
        <YAxis stroke="#94a3b8" tick={{ fill: '#94a3b8' }} />
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
          dataKey="cost"
          stroke="#dcff1a"
          strokeWidth={2}
          dot={{ fill: '#dcff1a' }}
          activeDot={{ r: 8 }}
        />
      </LineChart>
    </ResponsiveContainer>
  </div>
);

const CountrySelect = ({
  label,
  value,
  onChange,
  isOpen,
  onOpenChange,
  countries,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  countries: Country[];
}) => (
  <div className="space-y-2">
    <label className="text-sm font-medium text-gray-400">{label}</label>
    <Popover open={isOpen} onOpenChange={onOpenChange}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          className="w-full justify-between bg-slate-800/50 border-white/10"
        >
          {value || `Select ${label.toLowerCase()}...`}
          <Search className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-[300px] p-0 bg-slate-900 border-white/10">
        <Command>
          <CommandInput placeholder="Search..." />
          <CommandEmpty>Not found.</CommandEmpty>
          <CommandGroup>
            {countries.map((country) => (
              <CommandItem
                key={country.iso3n}
                value={country.name}
                onSelect={() => {
                  onChange(country.name);
                  onOpenChange(false);
                }}
              >
                {country.name}
              </CommandItem>
            ))}
          </CommandGroup>
        </Command>
      </PopoverContent>
    </Popover>
  </div>
);

// API Functions
const API_BASE = import.meta.env.VITE_API_URL || "";

async function fetchShippingHistory(originIso3n: number, destIso3n: number): Promise<ShippingPoint[]> {
  const o = String(originIso3n).padStart(3, "0");
  const d = String(destIso3n).padStart(3, "0");

  const candidates = [
    `/shipping/costs?origin=${o}&destination=${d}`,
    `/shipping/history?origin=${o}&destination=${d}`,
    `/shipping/${o}/${d}/history`,
    `/routes/${o}/${d}/shipping-costs`,
  ];

  for (const path of candidates) {
    try {
      const { data } = await axios.get<ShippingPoint[]>(API_BASE + path);
      if (Array.isArray(data)) return data;
    } catch (_err) {
      continue;
    }
  }
  throw new Error(`No shipping history endpoint responded for origin=${o} destination=${d}`);
}

// Main Component
export default function ShippingCostPage() {
  const [countries, setCountries] = useState<Country[]>([]);
  const [origin, setOrigin] = useState<string>("");
  const [destination, setDestination] = useState<string>("");
  const [dateString, setDateString] = useState<string>("");
  const [shippingHistory, setShippingHistory] = useState<ShippingPoint[] | null>(null);
  const [originOpen, setOriginOpen] = useState(false);
  const [destOpen, setDestOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchCountries = async () => {
      try {
        const { data } = await axios.get<Country[]>(`${API_BASE}/countries`);
        setCountries(data || []);
      } catch (err) {
        setError("Failed to load countries");
      }
    };
    fetchCountries();
  }, []);

  useEffect(() => {
    if (!origin || !destination) {
      setShippingHistory(null);
      return;
    }

    const originCountry = countries.find(c => c.name === origin);
    const destCountry = countries.find(c => c.name === destination);

    if (!originCountry?.iso3n || !destCountry?.iso3n) return;

    const fetchCosts = async () => {
      setLoading(true);
      try {
        const data = await fetchShippingHistory(originCountry.iso3n, destCountry.iso3n);
        setShippingHistory(data);
      } catch (err) {
        setError("Failed to fetch shipping costs");
      } finally {
        setLoading(false);
      }
    };

    fetchCosts();
  }, [origin, destination]);

  const filteredHistory = shippingHistory?.filter(point => {
    if (!dateString) return true;
    return point.date <= dateString;
  });

  const currentCost = dateString && filteredHistory ? 
    filteredHistory[filteredHistory.length - 1] : null;

  return (
    <div className="flex-1 space-y-8 p-8">
      {/* Header */}
      <div className="text-center space-y-2">
        <h1 className="text-5xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
          Shipping Costs
        </h1>
        <p className="text-xl text-gray-400">
          Calculate shipping costs between countries
        </p>
      </div>

      {/* Search Controls */}
      <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
        <CardHeader>
          <CardTitle>Search Route</CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <CountrySelect
            label="Origin Country"
            value={origin}
            onChange={setOrigin}
            isOpen={originOpen}
            onOpenChange={setOriginOpen}
            countries={countries}
          />
          <CountrySelect
            label="Destination Country"
            value={destination}
            onChange={setDestination}
            isOpen={destOpen}
            onOpenChange={setDestOpen}
            countries={countries}
          />
          <div className="space-y-2">
            <label className="text-sm font-medium text-gray-400">Filter by Date</label>
            <Input
              type="date"
              value={dateString}
              onChange={(e) => setDateString(e.target.value)}
              className="bg-slate-800/50 border-white/10"
            />
          </div>
        </CardContent>
      </Card>

      {/* Loading State */}
      {loading && (
        <div className="flex items-center justify-center h-[400px]">
          <Loader2 className="h-8 w-8 animate-spin text-[#dcff1a]" />
        </div>
      )}

      {/* Results */}
      {origin && destination && shippingHistory && !loading && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
            <CardHeader>
              <CardTitle>Route Details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              <RouteVisualization origin={origin} destination={destination} />
              {currentCost && (
                <div className="p-4 bg-slate-800/50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <BarChart2 className="h-5 w-5 text-[#dcff1a]" />
                    <div>
                      <p className="text-sm text-gray-400">Current Cost</p>
                      <p className="text-2xl font-bold text-[#dcff1a]">
                        ${currentCost.cost.toFixed(2)} {currentCost.currency || 'USD'}
                      </p>
                    </div>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
            <CardHeader>
              <CardTitle>Cost History</CardTitle>
            </CardHeader>
            <CardContent>
              {shippingHistory.length > 0 ? (
                <>
                  <CostHistoryChart costs={shippingHistory} />
                  <div className="overflow-auto max-h-[300px]">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>Date</TableHead>
                          <TableHead>Cost</TableHead>
                          <TableHead>Currency</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {shippingHistory.map((point, idx) => (
                          <TableRow key={idx}>
                            <TableCell>{point.date}</TableCell>
                            <TableCell>${point.cost.toFixed(2)}</TableCell>
                            <TableCell>{point.currency || 'USD'}</TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                </>
              ) : (
                <p className="text-gray-400">No cost history available</p>
              )}
            </CardContent>
          </Card>
        </div>
      )}

      {/* Error Display */}
      {error && (
        <div className="rounded-md border border-red-600 bg-red-900/40 px-3 py-2 text-red-200">
          {error}
        </div>
      )}
    </div>
  );
}