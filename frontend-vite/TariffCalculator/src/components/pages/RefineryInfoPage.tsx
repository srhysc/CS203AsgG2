// // src/components/pages/RefineryInfoPage.tsx
// import React, { useEffect, useMemo, useState } from "react";
// import axios from "axios";
// import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
// import { Combobox } from "@/components/ui/combobox";
// import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

// type Refinery = {
//   id: number;
//   name: string;
//   country: string;
//   established: string; // ISO date
//   active: boolean;
// };

// type RefineryPrice = {
//   date: string; // ISO date
//   price: number;
// };

// const API_BASE = import.meta.env.VITE_API_URL || "";

// // Try multiple possible endpoints for refinery historical prices
// async function fetchRefineryPrices(refineryId: number): Promise<RefineryPrice[]> {
//   const candidates = [
//     `/refineries/${refineryId}/prices`,
//     `/refineries/${refineryId}/historicalPrices`,
//     `/prices?refineryId=${refineryId}`
//   ];
//   for (const path of candidates) {
//     try {
//       const { data } = await axios.get<RefineryPrice[]>(API_BASE + path);
//       if (Array.isArray(data)) return data;
//     } catch (_err) {
//       // try next candidate
//     }
//   }
//   throw new Error("No refinery prices endpoint responded for refineryId=" + refineryId);
// }

// export default function RefineryInfoPage() {
//   const [refineries, setRefineries] = useState<Refinery[]>([]);
//   const [selectedRefineryId, setSelectedRefineryId] = useState<string>("");
//   const [prices, setPrices] = useState<RefineryPrice[] | null>(null);
//   const [loading, setLoading] = useState<boolean>(false);
//   const [error, setError] = useState<string>("");

//   // Load refinery list
//   useEffect(() => {
//     let cancelled = false;
//     const run = async () => {
//       setError("");
//       try {
//         const { data } = await axios.get<Refinery[]>(API_BASE + "/refineries");
//         if (!cancelled) setRefineries(data ?? []);
//       } catch (e: any) {
//         if (!cancelled) setError("Failed to load refineries. Check VITE_API_URL and /refineries.");
//         console.error(e);
//       }
//     };
//     run();
//     return () => { cancelled = true; };
//   }, []);

//   const options = useMemo(() => refineries.map(r => ({
//     label: `${r.name} (${r.country})`,
//     value: String(r.id)
//   })), [refineries]);

//   // Fetch historical prices when a refinery is selected
//   useEffect(() => {
//     const id = Number(selectedRefineryId);
//     if (!id) { setPrices(null); return; }
//     let cancelled = false;
//     setLoading(true);
//     setError("");
//     fetchRefineryPrices(id)
//       .then(list => !cancelled && setPrices(list))
//       .catch(err => {
//         console.error(err);
//         if (!cancelled) setError("Could not fetch historical prices for the selected refinery.");
//       })
//       .finally(() => !cancelled && setLoading(false));
//     return () => { cancelled = true; };
//   }, [selectedRefineryId]);

//   // Find selected refinery details
//   const selectedRefinery = refineries.find(r => String(r.id) === selectedRefineryId);

//   return (
//     <div className="max-w-6xl mx-auto p-6 space-y-8">
//       <Card className="bg-slate-900/40 border-slate-700">
//         <CardHeader>
//           <CardTitle className="text-xl">Select a Refinery</CardTitle>
//         </CardHeader>
//         <CardContent className="space-y-4">
//           <Combobox
//             options={options}
//             value={selectedRefineryId}
//             onChange={(v) => setSelectedRefineryId(v)}
//             placeholder="Search refinery..."
//             widthClass="w-full md:w-96"
//           />
//           {error && <div className="rounded-md border border-red-600 bg-red-900/40 px-3 py-2 text-red-200">{error}</div>}
//         </CardContent>
//       </Card>

//       {selectedRefinery && (
//         <Card className="bg-slate-900/40 border-slate-700">
//           <CardHeader>
//             <CardTitle className="text-xl">{selectedRefinery.name} Info</CardTitle>
//           </CardHeader>
//           <CardContent className="space-y-4">
//             <p><strong>Country:</strong> {selectedRefinery.country}</p>
//             <p><strong>Established:</strong> {new Date(selectedRefinery.established).toLocaleDateString()}</p>
//             <p><strong>Status:</strong> {selectedRefinery.active ? "Active" : "Inactive"}</p>

//             {loading && <p className="text-slate-300">Loading historical prices…</p>}
//             {prices && prices.length === 0 && <p className="text-slate-300">No historical prices found.</p>}
//             {prices && prices.length > 0 && (
//               <div className="overflow-x-auto">
//                 <Table>
//                   <TableHeader>
//                     <TableRow>
//                       <TableHead>Date</TableHead>
//                       <TableHead>Price (USD)</TableHead>
//                     </TableRow>
//                   </TableHeader>
//                   <TableBody>
//                     {prices.map((row, idx) => (
//                       <TableRow key={idx}>
//                         <TableCell>{new Date(row.date).toLocaleDateString()}</TableCell>
//                         <TableCell>{row.price}</TableCell>
//                       </TableRow>
//                     ))}
//                   </TableBody>
//                 </Table>
//               </div>
//             )}
//           </CardContent>
//         </Card>
//       )}
//     </div>
//   );
// }import React, { useEffect, useState } from "react";
import React, { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { Calendar, ChevronLeft, ChevronRight, Building2, Globe, Activity, Loader2, Search } from "lucide-react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";

// Types
interface CostDetail {
  cost_per_unit: number;
  unit: string;
}
interface EstimatedCost {
  date: string;
  costs: Record<string, CostDetail>;
}
interface Refinery {
  name: string;
  company: string;
  location: string;
  operational_from: number;
  operational_to: number | null;
  can_refine_any: boolean;
  estimated_costs: EstimatedCost[];
  countryIso3: string;
  countryIsoNumeric: string;
  countryName: string;
}
interface CountryData {
  iso3: string;
  iso_numeric: string;
  refineries: Refinery[];
}
interface DropdownOption {
  label: string;
  value: string;
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

// Interactive Price Chart
const PriceChart = ({ costs }: { costs: { date: string; cost_per_unit: number; unit: string }[] }) => (
  <div className="w-full h-[300px] mb-6 p-4 bg-slate-900/50 rounded-lg border border-white/10">
    <ResponsiveContainer width="100%" height="100%">
      <LineChart
        data={costs}
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
            value: 'Cost per Unit (USD)',
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
          formatter={(value, name, props) => [`$${value}`, props.payload.unit]}
        />
        <Line
          type="monotone"
          dataKey="cost_per_unit"
          stroke="#dcff1a"
          strokeWidth={2}
          dot={{ fill: '#dcff1a', strokeWidth: 2 }}
          activeDot={{ r: 8 }}
        />
      </LineChart>
    </ResponsiveContainer>
  </div>
);

// Main Page
export default function RefineryInfoPage() {
  const [refineries, setRefineries] = useState<Refinery[]>([]);
  const [selectedRefinery, setSelectedRefinery] = useState<string>("");
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [unit, setUnit] = useState<string>("barrel");

  // Fetch refineries from backend
  useEffect(() => {
    setLoading(true);
    axios.get<Record<string, CountryData>>(`${import.meta.env.VITE_API_URL}/refineries`)
      .then(res => {
        // Flatten country/refinery structure
        const refineryList: Refinery[] = [];
        Object.entries(res.data).forEach(([countryName, countryData]) => {
          countryData.refineries.forEach(refinery => {
            refinery.countryName = countryName;
            refineryList.push(refinery);
          });
        });
        setRefineries(refineryList);
      })
      .catch(err => {
        setError("Failed to load refineries. Please try again later.");
        console.error(err);
      })
      .finally(() => setLoading(false));
  }, []);

  // Dropdown options
  const options: DropdownOption[] = useMemo(() => refineries.map(r => ({
    label: `${r.name} (${r.countryName})`,
    value: r.name + "|" + r.countryIso3
  })), [refineries]);

  // Find selected refinery
  const refineryData = useMemo(() => {
    if (!selectedRefinery) return undefined;
    const [name, iso3] = selectedRefinery.split("|");
    return refineries.find(r => r.name === name && r.countryIso3 === iso3);
  }, [selectedRefinery, refineries]);

  // Filter costs by date and unit
  const filteredCosts = useMemo(() => {
    if (!refineryData) return [];
    let costs: { date: string; cost_per_unit: number; unit: string }[] = [];
    refineryData.estimated_costs.forEach(costEntry => {
      if (!selectedDate || new Date(costEntry.date) <= selectedDate) {
        if (costEntry.costs[unit]) {
          costs.push({
            date: costEntry.date,
            cost_per_unit: costEntry.costs[unit].cost_per_unit,
            unit: costEntry.costs[unit].unit
          });
        }
      }
    });
    // Sort by date ascending
    costs.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
    return costs;
  }, [refineryData, selectedDate, unit]);

  // Available units for dropdown
  const availableUnits = useMemo(() => {
    if (!refineryData || refineryData.estimated_costs.length === 0) return [];
    const first = refineryData.estimated_costs[0];
    return Object.keys(first.costs);
  }, [refineryData]);

  return (
    <div className="flex-1 w-full min-h-screen">
      <div className="w-full max-w-7xl mx-auto px-4 py-8 space-y-8">
        {/* Header */}
        <div className="text-center space-y-4">
          <h1 className="text-5xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
            Refinery Information
          </h1>
          <p className="text-xl text-gray-400">
            Explore global refineries and their cost trends
          </p>
        </div>

        {/* Search Section */}
        <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
          <CardHeader>
            <CardTitle className="text-2xl text-white">Search Refineries</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex flex-col md:flex-row gap-6">
              {/* Refinery Selection */}
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Select Refinery
                </label>
                <Popover open={open} onOpenChange={setOpen}>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      role="combobox"
                      aria-expanded={open}
                      className="w-full justify-between bg-slate-800/50 border-white/10 text-white h-11 text-base"
                    >
                      {refineryData ? `${refineryData.name} (${refineryData.countryName})` : "Select a refinery..."}
                      <Search className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-[400px] p-0 bg-slate-900 border-white/10">
                    <Command>
                      <CommandInput
                        placeholder="Search refineries..."
                        className="h-11 text-base text-white"
                      />
                      <CommandEmpty className="text-base py-4 text-gray-400">
                        No refinery found.
                      </CommandEmpty>
                      <CommandGroup>
                        {options.map((option) => (
                          <CommandItem
                            key={option.value}
                            value={option.value}
                            onSelect={() => {
                              setSelectedRefinery(option.value);
                              setOpen(false);
                            }}
                            className="cursor-pointer text-base py-3 text-white hover:bg-white/10"
                          >
                            <Building2 className="mr-3 h-5 w-5 text-[#dcff1a]" />
                            {option.label}
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
                <Popover>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      className="w-full h-11 px-3 py-2 text-lg flex items-center justify-between
                        bg-slate-800/50 border-white/10 text-white 
                        hover:bg-slate-700 hover:border-[#dcff1a] transition-colors"
                    >
                      {selectedDate?.toLocaleDateString() || "Pick a date"}
                      <Calendar className="ml-2 h-5 w-5 opacity-50" />
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

              {/* Unit Filter */}
              <div className="w-full md:w-[200px]">
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Unit
                </label>
                <Popover>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      className="w-full h-11 px-3 py-2 text-lg flex items-center justify-between
          bg-slate-800/50 border-white/10 text-white 
          hover:bg-slate-700 hover:border-[#dcff1a] transition-colors"
                    >
                      {unit ? unit : "Select unit"}
                      <Activity className="ml-2 h-5 w-5 opacity-50" />
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-[200px] p-0 bg-slate-900 border-white/10">
                    <Command>
                      <CommandInput
                        placeholder="Search units..."
                        className="h-11 text-base text-white"
                      />
                      <CommandEmpty className="text-base py-4 text-gray-400">
                        No unit found.
                      </CommandEmpty>
                      <CommandGroup>
                        {availableUnits.map(u => (
                          <CommandItem
                            key={u}
                            value={u}
                            onSelect={() => {
                              setUnit(u);
                            }}
                            className="cursor-pointer text-base py-3 text-white hover:bg-white/10"
                          >
                            <Activity className="mr-3 h-5 w-5 text-[#dcff1a]" />
                            {u}
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    </Command>
                  </PopoverContent>
                </Popover>
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
        {refineryData && !loading && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Refinery Details */}
            <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
              <CardHeader className="p-6">
                <CardTitle className="text-3xl text-white mb-6">
                  {refineryData.name}
                </CardTitle>
                <div className="space-y-6">
                  <div className="flex items-center gap-4">
                    <Building2 className="w-6 h-6 text-[#dcff1a]" />
                    <div>
                      <p className="text-sm text-gray-400">Company</p>
                      <p className="text-lg text-gray-300">{refineryData.company}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <Globe className="w-6 h-6 text-[#dcff1a]" />
                    <div>
                      <p className="text-sm text-gray-400">Location</p>
                      <p className="text-lg text-gray-300">{refineryData.location}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <Activity className="w-6 h-6 text-[#dcff1a]" />
                    <div>
                      <p className="text-sm text-gray-400">Refining Capability</p>
                      <p className="text-lg text-emerald-400">
                        {refineryData.can_refine_any ?
                          "Full Refining Capability" :
                          "Limited Refining Capability"}
                      </p>
                    </div>
                  </div>
                </div>
              </CardHeader>
            </Card>

            {/* Cost History */}
            <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
              <CardHeader className="p-6">
                <CardTitle className="text-3xl text-white mb-6">
                  Cost History ({unit})
                </CardTitle>
                {filteredCosts && filteredCosts.length > 0 ? (
                  <div className="space-y-6">
                    <PriceChart costs={filteredCosts} />
                    <div className="overflow-auto max-h-[300px]">
                      <Table>
                        <TableHeader>
                          <TableRow className="hover:bg-transparent">
                            <TableHead className="text-gray-400 text-lg">Date</TableHead>
                            <TableHead className="text-gray-400 text-lg">Cost per Unit (USD)</TableHead>
                            <TableHead className="text-gray-400 text-lg">Unit</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {filteredCosts.map((cost) => (
                            <TableRow key={cost.date} className="hover:bg-white/5">
                              <TableCell className="text-gray-300 text-base">
                                {new Date(cost.date).toLocaleDateString()}
                              </TableCell>
                              <TableCell className="text-[#dcff1a] text-base font-medium">
                                ${cost.cost_per_unit.toFixed(2)}
                              </TableCell>
                              <TableCell className="text-gray-300 text-base">
                                {cost.unit}
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </div>
                  </div>
                ) : (
                  <p className="text-gray-400 text-lg">
                    No cost data available for the selected period and unit.
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