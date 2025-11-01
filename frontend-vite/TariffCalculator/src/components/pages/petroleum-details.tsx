
// import React, { useEffect, useMemo, useState } from "react";
// import axios from "axios";
// import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
// import { Combobox } from "@/components/ui/combobox";
// import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

// type Petroleum = {
//   name: string;
//   hsCode: number;
//   pricePerUnit?: number;
// };

// type PricePoint = {
//   date: string;   // ISO date string
//   price: number;  // per unit in your backend currency
// };

// const API_BASE = import.meta.env.VITE_API_URL || "";

// // Try a few common endpoints for historical price series
// async function fetchPriceHistory(hsCode: number): Promise<PricePoint[]> {
//   const codes = [String(hsCode), String(hsCode).padStart(6, "0")];
//   const candidates = [
//     (code:string) => `/petroleum/${code}/prices`,       // preferred
//     (code:string) => `/petroleum/${code}/history`,      // alt
//     (code:string) => `/petroleum/prices?hsCode=${code}`,// alt
//     (code:string) => `/prices/petroleum?hs=${code}`,    // alt
//   ];
//   for (const formatter of candidates) {
//     for (const code of codes) {
//       try {
//         const { data } = await axios.get<PricePoint[]>(API_BASE + formatter(code));
//         if (Array.isArray(data)) return data;
//       } catch (_err) {
//         // continue
//       }
//     }
//   }
//   throw new Error("No petroleum price endpoint responded for hsCode=" + hsCode);
// }

// export default function PetroleumDetailsPage() {
//   const [list, setList] = useState<Petroleum[]>([]);
//   const [selectedHS, setSelectedHS] = useState<string>("");
//   const [series, setSeries] = useState<PricePoint[] | null>(null);
//   const [loading, setLoading] = useState<boolean>(false);
//   const [error, setError] = useState<string>("");

//   // load petroleum list
//   useEffect(() => {
//     let cancelled = false;
//     const run = async () => {
//       setError("");
//       try {
//         const { data } = await axios.get<Petroleum[]>(API_BASE + "/petroleum");
//         if (!cancelled) setList(data ?? []);
//       } catch (e:any) {
//         if (!cancelled) setError("Failed to load petroleum list. Check VITE_API_URL and /petroleum.");
//         console.error(e);
//       }
//     };
//     run();
//     return () => { cancelled = true; };
//   }, []);

//   const options = useMemo(() => list.map(p => ({
//     label: `${p.name} (HS ${String(p.hsCode).padStart(6,"0")})`,
//     value: String(p.hsCode)
//   })), [list]);

//   // when user selects a petroleum item, fetch its price history
//   useEffect(() => {
//     const hs = Number(selectedHS);
//     if (!hs) { setSeries(null); return; }
//     let cancelled = false;
//     setLoading(true);
//     setError("");
//     fetchPriceHistory(hs)
//       .then(data => !cancelled && setSeries(data))
//       .catch(err => {
//         console.error(err);
//         if (!cancelled) setError("Could not fetch price history for the selected petroleum item.");
//       })
//       .finally(() => !cancelled && setLoading(false));
//     return () => { cancelled = true; };
//   }, [selectedHS]);

//   return (
//     <div className="max-w-6xl mx-auto p-6 space-y-8">
//       <Card className="bg-slate-900/40 border-slate-700">
//         <CardHeader>
//           <CardTitle className="text-xl">Select Petroleum</CardTitle>
//         </CardHeader>
//         <CardContent className="space-y-4">
//           <Combobox
//             options={options}
//             value={selectedHS}
//             onChange={(v) => setSelectedHS(v)}
//             placeholder="Search petroleum..."
//             widthClass="w-full md:w-96"
//           />
//           {error && <div className="rounded-md border border-red-600 bg-red-900/40 px-3 py-2 text-red-200">{error}</div>}
//         </CardContent>
//       </Card>

//       <Card className="bg-slate-900/40 border-slate-700">
//         <CardHeader>
//           <CardTitle className="text-xl">Historical Prices</CardTitle>
//         </CardHeader>
//         <CardContent>
//           {!selectedHS && <p className="text-slate-300">Choose a petroleum type to view historical prices.</p>}
//           {loading && <p className="text-slate-300">Loading…</p>}
//           {series && series.length === 0 && <p className="text-slate-300">No price history found.</p>}
//           {series && series.length > 0 && (
//             <div className="overflow-x-auto">
//               <Table>
//                 <TableHeader>
//                   <TableRow>
//                     <TableHead>Date</TableHead>
//                     <TableHead>Price / Unit</TableHead>
//                   </TableRow>
//                 </TableHeader>
//                 <TableBody>
//                   {series.map((pt, idx) => (
//                     <TableRow key={idx}>
//                       <TableCell>{new Date(pt.date).toLocaleDateString()}</TableCell>
//                       <TableCell>{pt.price}</TableCell>
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
// }
import React, { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Droplet, Search, Loader2, BarChart2 } from "lucide-react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

// Types
interface Petroleum {
  name: string;
  hsCode: number;
  pricePerUnit?: number;
}

interface PricePoint {
  date: string;
  price: number;
}

const API_BASE = import.meta.env.VITE_API_URL || "";

// Try multiple endpoints for historical prices
async function fetchPriceHistory(hsCode: number): Promise<PricePoint[]> {
  const codes = [String(hsCode), String(hsCode).padStart(6, "0")];
  const candidates = [
    (code: string) => `/petroleum/${code}/prices`,
    (code: string) => `/petroleum/${code}/history`,
    (code: string) => `/petroleum/prices?hsCode=${code}`,
    (code: string) => `/prices/petroleum?hs=${code}`,
  ];

  for (const formatter of candidates) {
    for (const code of codes) {
      try {
        const { data } = await axios.get<PricePoint[]>(API_BASE + formatter(code));
        if (Array.isArray(data)) return data;
      } catch (_err) {
        continue;
      }
    }
  }
  throw new Error(`No petroleum price endpoint responded for hsCode=${hsCode}`);
}

// Price Chart Component
function PriceChart({ prices }: { prices: PricePoint[] }) {
  return (
    <div className="w-full h-[300px] mb-6 p-4 bg-slate-900/50 rounded-lg border border-white/10">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          data={prices}
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
              value: 'Price per Unit (USD)', 
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
            dataKey="price"
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

export default function PetroleumDetailsPage() {
  const [petroleumList, setPetroleumList] = useState<Petroleum[]>([]);
  const [selectedPetroleum, setSelectedPetroleum] = useState<string>("");
  const [dateString, setDateString] = useState<string>("");
  const [priceHistory, setPriceHistory] = useState<PricePoint[] | null>(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchPetroleum = async () => {
      setLoading(true);
      try {
        const { data } = await axios.get<Petroleum[]>(`${API_BASE}/petroleum`);
        setPetroleumList(data || []);
      } catch (err) {
        setError("Failed to load petroleum list. Please try again later.");
        console.error("Error fetching petroleum list:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchPetroleum();
  }, []);

  useEffect(() => {
    if (!selectedPetroleum) {
      setPriceHistory(null);
      return;
    }

    const fetchPrices = async () => {
      setLoading(true);
      setError("");
      const petroleum = petroleumList.find(p => p.name === selectedPetroleum);
      
      if (!petroleum?.hsCode) {
        setError("Invalid petroleum selection");
        setLoading(false);
        return;
      }

      try {
        const data = await fetchPriceHistory(petroleum.hsCode);
        setPriceHistory(data);
      } catch (err) {
        setError("Failed to fetch price history");
        console.error("Error fetching prices:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchPrices();
  }, [selectedPetroleum, petroleumList]);

  const filteredPrices = priceHistory?.filter(price => {
    if (!dateString) return true;
    return price.date <= dateString;
  });

  const currentPrice = dateString && filteredPrices ? 
    filteredPrices[filteredPrices.length - 1]?.price : 
    null;

  const selectedPetroleumData = petroleumList.find(p => p.name === selectedPetroleum);

  return (
    <div className="flex-1 w-full min-h-screen">
      <div className="w-full max-w-7xl mx-auto px-4 py-8 space-y-8">
        {/* Header */}
        <div className="text-center space-y-4">
          <h1 className="text-5xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
            Petroleum Details
          </h1>
          <p className="text-xl text-gray-400">
            View petroleum types and their price history
          </p>
        </div>

        {/* Search Section */}
        <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
          <CardHeader>
            <CardTitle className="text-2xl text-white">Search Petroleum</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex flex-col md:flex-row gap-6">
              {/* Petroleum Selection */}
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Select Petroleum Type
                </label>
                <Popover open={open} onOpenChange={setOpen}>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      role="combobox"
                      aria-expanded={open}
                      className="w-full justify-between bg-slate-800/50 border-white/10 text-white h-11 text-lg"
                    >
                      {selectedPetroleum || "Select a petroleum type..."}
                      <Search className="ml-2 h-5 w-5 shrink-0 opacity-50" />
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-[400px] p-0 bg-slate-900 border-white/10">
                    <Command>
                      <CommandInput 
                        placeholder="Search petroleum types..." 
                        className="h-11 text-lg text-white"
                      />
                      <CommandEmpty className="text-lg py-4 text-gray-400">
                        No petroleum type found.
                      </CommandEmpty>
                      <CommandGroup>
                        {petroleumList.map((petroleum) => (
                          <CommandItem
                            key={petroleum.hsCode}
                            value={petroleum.name}
                            onSelect={() => {
                              setSelectedPetroleum(petroleum.name);
                              setOpen(false);
                            }}
                            className="cursor-pointer text-lg py-3 text-white hover:bg-white/10"
                          >
                            <Droplet className="mr-3 h-5 w-5 text-[#dcff1a]" />
                            {petroleum.name} (HS {petroleum.hsCode})
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
        {selectedPetroleumData && filteredPrices && !loading && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Details Card */}
            <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
              <CardHeader className="p-6">
                <CardTitle className="text-3xl text-white mb-6">
                  {selectedPetroleumData.name}
                </CardTitle>
                <div className="space-y-6">
                  <div className="flex items-center gap-4">
                    <Droplet className="w-6 h-6 text-[#dcff1a]" />
                    <div>
                      <p className="text-sm text-gray-400">HS Code</p>
                      <p className="text-lg text-gray-300">{selectedPetroleumData.hsCode}</p>
                    </div>
                  </div>
                  {currentPrice && (
                    <div className="flex items-center gap-4">
                      <BarChart2 className="w-6 h-6 text-[#dcff1a]" />
                      <div>
                        <p className="text-sm text-gray-400">Current Price</p>
                        <p className="text-lg text-[#dcff1a]">${currentPrice.toFixed(2)} / unit</p>
                      </div>
                    </div>
                  )}
                </div>
              </CardHeader>
            </Card>

            {/* Price History Card */}
            <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
              <CardHeader className="p-6">
                <CardTitle className="text-3xl text-white mb-6">
                  Price History
                </CardTitle>
                {filteredPrices.length > 0 ? (
                  <div className="space-y-6">
                    <PriceChart prices={filteredPrices} />
                    <div className="overflow-auto max-h-[300px]">
                      <Table>
                        <TableHeader>
                          <TableRow className="hover:bg-transparent">
                            <TableHead className="text-gray-400 text-lg">Date</TableHead>
                            <TableHead className="text-gray-400 text-lg">Price per Unit (USD)</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {filteredPrices.map((point) => (
                            <TableRow key={point.date} className="hover:bg-white/5">
                              <TableCell className="text-gray-300 text-lg">
                                {point.date}
                              </TableCell>
                              <TableCell className="text-[#dcff1a] text-lg font-medium">
                                ${point.price.toFixed(2)}
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </div>
                  </div>
                ) : (
                  <p className="text-gray-400 text-lg">
                    No price data available for the selected period
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