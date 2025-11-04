
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
// }import React, { useEffect, useState } from "react";"use client";
import React, { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { Loader2, Search, Calendar, ChevronLeft, ChevronRight, Ship, BarChart2 } from "lucide-react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";

// Types
interface Country {
  name: string;
  iso3: string;
  code: string;
}

interface ShippingFeeEntry {
  date: string;
  costs: {
    [unit: string]: {
      costPerUnit: number;
      unit: string;
    };
  };
}

interface ShippingFeeResponse {
  country1Name: string;
  country2Name: string;
  country1Iso3: string;
  country2Iso3: string;
  country1IsoNumeric: string;
  country2IsoNumeric: string;
  shippingFees: ShippingFeeEntry[];
}

// Calendar Component
const CalendarView = ({ date, onDateChange }: { date: Date | null, onDateChange: (date: Date) => void }) => {
  const [viewDate, setViewDate] = useState(date || new Date());
  const firstDay = new Date(viewDate.getFullYear(), viewDate.getMonth(), 1);
  const lastDay = new Date(viewDate.getFullYear(), viewDate.getMonth() + 1, 0);
  const daysInMonth = lastDay.getDate();
  const startDay = firstDay.getDay();
  const days = Array.from({ length: 42 }, (_, i) => {
    const dayNumber = i - startDay + 1;
    if (dayNumber < 1 || dayNumber > daysInMonth) return null;
    return new Date(viewDate.getFullYear(), viewDate.getMonth(), dayNumber);
  });
  return (
    <div className="p-4 bg-slate-800 rounded-lg border border-slate-700 w-[350px]">
      <div className="flex items-center justify-between mb-4">
        <Button variant="ghost" size="icon" onClick={() => setViewDate(new Date(viewDate.getFullYear(), viewDate.getMonth() - 1))}>
          <ChevronLeft className="h-5 w-5 text-gray-100" />
        </Button>
        <div className="text-lg font-semibold text-gray-100">
          {viewDate.toLocaleString('default', { month: 'long', year: 'numeric' })}
        </div>
        <Button variant="ghost" size="icon" onClick={() => setViewDate(new Date(viewDate.getFullYear(), viewDate.getMonth() + 1))}>
          <ChevronRight className="h-5 w-5 text-gray-100" />
        </Button>
      </div>
      <div className="grid grid-cols-7 gap-1 mb-2">
        {['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'].map((day) => (
          <div key={day} className="text-[#dcff1a] font-medium text-center py-2 text-sm">{day}</div>
        ))}
      </div>
      <div className="grid grid-cols-7 gap-1">
        {days.map((day, i) => (
          <Button
            key={i}
            variant="ghost"
            disabled={!day}
            onClick={() => day && onDateChange(day)}
            className={`
              h-10 w-10 p-0 font-normal text-base
              ${!day ? 'invisible' : ''}
              ${day?.toDateString() === date?.toDateString() 
                ? 'bg-[#dcff1a] text-slate-900 hover:bg-[#dcff1a]/90' 
                : 'text-gray-100 hover:bg-slate-700 hover:text-[#dcff1a]'}
            `}
          >
            {day?.getDate()}
          </Button>
        ))}
      </div>
    </div>
  );
};

// Chart
const CostHistoryChart = ({ costs, unit }: { costs: { date: string; cost: number; unit: string }[], unit: string }) => (
  <div className="w-full h-[300px] mb-6 p-4 bg-slate-900/50 rounded-lg border border-white/10">
    <ResponsiveContainer width="100%" height="100%">
      <LineChart data={costs} margin={{ top: 10, right: 30, left: 10, bottom: 10 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="#ffffff20" />
        <XAxis dataKey="date" stroke="#94a3b8" tick={{ fill: '#94a3b8' }} />
        <YAxis stroke="#94a3b8" tick={{ fill: '#94a3b8' }} label={{ value: unit, angle: -90, position: 'insideLeft', fill: '#94a3b8' }} />
        <Tooltip
          contentStyle={{
            backgroundColor: '#1e1b4b',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            borderRadius: '6px',
          }}
          labelStyle={{ color: '#94a3b8' }}
          itemStyle={{ color: '#dcff1a' }}
          formatter={(value) => [`$${value}`, unit]}
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

const API_BASE = import.meta.env.VITE_API_URL || "";

export default function ShippingCostPage() {
  const [countries, setCountries] = useState<Country[]>([]);
  const [origin, setOrigin] = useState<string>("");
  const [destination, setDestination] = useState<string>("");
  const [originOpen, setOriginOpen] = useState(false);
  const [destOpen, setDestOpen] = useState(false);
  const [unit, setUnit] = useState<string>("barrel");
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [shippingData, setShippingData] = useState<ShippingFeeResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Fetch countries
  useEffect(() => {
    axios.get<Country[]>(`${API_BASE}/countries`)
      .then(res => setCountries(res.data))
      .catch(() => setError("Failed to load countries"));
  }, []);

  // Fetch shipping fees when origin/destination changes
  useEffect(() => {
    if (!origin || !destination) {
      setShippingData(null);
      return;
    }
    setLoading(true);
    setError("");
    axios.get<ShippingFeeResponse>(`${API_BASE}/shipping-fees/${origin}/${destination}`)
      .then(res => setShippingData(res.data))
      .catch(() => setError("Failed to fetch shipping costs"))
      .finally(() => setLoading(false));
  }, [origin, destination]);

  // Prepare unit options
  const unitOptions = useMemo(() => {
    if (!shippingData || !shippingData.shippingFees.length) return [];
    const firstEntry = shippingData.shippingFees[0];
    return Object.keys(firstEntry.costs);
  }, [shippingData]);

  // Prepare filtered cost history
  const filteredCosts = useMemo(() => {
    if (!shippingData) return [];
    let entries = shippingData.shippingFees;
    if (selectedDate) {
      entries = entries.filter(e => new Date(e.date) <= selectedDate);
    }
    return entries
      .map(e => ({
        date: e.date,
        cost: e.costs[unit]?.costPerUnit ?? 0,
        unit: e.costs[unit]?.unit ?? unit,
      }))
      .filter(e => e.cost > 0);
  }, [shippingData, selectedDate, unit]);

  // Prepare country dropdown options
  const countryOptions = useMemo(() => countries.map(c => ({
    label: `${c.name} (${c.iso3})`,
    value: c.iso3,
  })), [countries]);

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
        <CardContent className="grid grid-cols-1 md:grid-cols-4 gap-6">
          {/* Origin */}
          <div>
            <label className="text-sm font-medium text-gray-400 mb-2 block">Country 1</label>
            <Popover open={originOpen} onOpenChange={setOriginOpen}>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  role="combobox"
                  className="w-full justify-between bg-slate-800/50 border-white/10 text-white"
                >
                  {origin ? countries.find(c => c.iso3 === origin)?.name : "Select country 1..."}
                  <Search className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-[300px] p-0 bg-slate-900 border-white/10">
                <Command>
                  <CommandInput placeholder="Search..." className="text-white" />
                  <CommandEmpty className="text-gray-400">Not found.</CommandEmpty>
                  <CommandGroup>
                    {countryOptions.map(option => (
                      <CommandItem
                        key={option.value}
                        value={option.value}
                        onSelect={() => {
                          setOrigin(option.value);
                          setOriginOpen(false);
                        }}
                        className="text-white hover:bg-white/10"
                      >
                        {option.label}
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </Command>
              </PopoverContent>
            </Popover>
          </div>
          {/* Destination */}
          <div>
            <label className="text-sm font-medium text-gray-400 mb-2 block">Country 2</label>
            <Popover open={destOpen} onOpenChange={setDestOpen}>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  role="combobox"
                  className="w-full justify-between bg-slate-800/50 border-white/10 text-white"
                >
                  {destination ? countries.find(c => c.iso3 === destination)?.name : "Select country 2  ..."}
                  <Search className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-[300px] p-0 bg-slate-900 border-white/10">
                <Command>
                  <CommandInput placeholder="Search..." className="text-white" />
                  <CommandEmpty className="text-gray-400">Not found.</CommandEmpty>
                  <CommandGroup>
                    {countryOptions.map(option => (
                      <CommandItem
                        key={option.value}
                        value={option.value}
                        onSelect={() => {
                          setDestination(option.value);
                          setDestOpen(false);
                        }}
                        className="text-white hover:bg-white/10"
                      >
                        {option.label}
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </Command>
              </PopoverContent>
            </Popover>
          </div>
          {/* Date */}
          <div>
            <label className="text-sm font-medium text-gray-400 mb-2 block">Filter by Date</label>
            <Popover>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  className="w-full h-10 px-3 py-2 text-base flex items-center justify-between
                    bg-slate-800/50 border-white/10 text-white 
                    hover:bg-slate-700 hover:border-[#dcff1a] transition-colors"
                >
                  {selectedDate?.toLocaleDateString() || "Pick a date"}
                  <Calendar className="ml-2 h-4 w-4 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent align="start" className="p-0">
                <CalendarView
                  date={selectedDate}
                  onDateChange={setSelectedDate}
                />
              </PopoverContent>
            </Popover>
          </div>
          {/* Unit */}
          <div>
            <label className="text-sm font-medium text-gray-400 mb-2 block">Unit</label>
            <Popover>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  className="w-full h-10 px-3 py-2 text-base flex items-center justify-between
                    bg-slate-800/50 border-white/10 text-white 
                    hover:bg-slate-700 hover:border-[#dcff1a] transition-colors"
                  disabled={unitOptions.length === 0}
                >
                  {unit}
                  <BarChart2 className="ml-2 h-4 w-4 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent align="start" className="p-0">
                <Command>
                  <CommandInput placeholder="Search unit..." className="text-white" />
                  <CommandEmpty className="text-gray-400">No unit found.</CommandEmpty>
                  <CommandGroup>
                    {unitOptions.map(u => (
                      <CommandItem
                        key={u}
                        value={u}
                        onSelect={() => setUnit(u)}
                        className="text-white hover:bg-white/10"
                      >
                        {u}
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </Command>
              </PopoverContent>
            </Popover>
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
      {origin && destination && shippingData && !loading && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
            <CardHeader>
              <CardTitle>Route Details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="flex items-center justify-center gap-8 py-8">
                <div className="text-xl font-medium text-white px-6 py-3 bg-slate-800/50 rounded-lg">
                  {countries.find(c => c.iso3 === origin)?.name}
                </div>
                <div className="relative w-48 h-2 bg-slate-800">
                  <div className="absolute inset-0 bg-[#dcff1a]" />
                  <Ship className="absolute -top-3 -right-3 w-6 h-6 text-[#dcff1a]" />
                </div>
                <div className="text-xl font-medium text-white px-6 py-3 bg-slate-800/50 rounded-lg">
                  {countries.find(c => c.iso3 === destination)?.name}
                </div>
              </div>
              {filteredCosts.length > 0 && (
                <div className="p-4 bg-slate-800/50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <BarChart2 className="h-5 w-5 text-[#dcff1a]" />
                    <div>
                      <p className="text-sm text-gray-400">Current Cost</p>
                      <p className="text-2xl font-bold text-[#dcff1a]">
                        ${filteredCosts[filteredCosts.length - 1].cost.toFixed(2)} {filteredCosts[filteredCosts.length - 1].unit}
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
              {filteredCosts.length > 0 ? (
                <>
                  <CostHistoryChart costs={filteredCosts} unit={unit} />
                  <div className="overflow-auto max-h-[300px]">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead className="text-gray-400">Date</TableHead>
                          <TableHead className="text-gray-400">Cost</TableHead>
                          <TableHead className="text-gray-400">Unit</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {filteredCosts.map((point, idx) => (
                          <TableRow key={idx} className="hover:bg-white/5">
                            <TableCell className="text-gray-300">
                              {new Date(point.date).toLocaleDateString()}
                            </TableCell>
                            <TableCell className="text-[#dcff1a] font-medium">
                              ${point.cost.toFixed(2)}
                            </TableCell>
                            <TableCell className="text-gray-300">
                              {point.unit}
                            </TableCell>
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