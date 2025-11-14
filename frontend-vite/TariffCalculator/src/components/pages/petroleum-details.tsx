// }"use client";
import { useEffect, useMemo, useState } from "react";
import axios, { isAxiosError } from "axios";
import { useAuth } from "@clerk/clerk-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { Loader2, Search, Droplet, BarChart2, Calendar, ChevronLeft, ChevronRight } from "lucide-react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";

// Types
interface PetroleumPrice {
  date: string;
  price: number;
  unit?: string;
}
interface Petroleum {
  name: string;
  hsCode: string;
  prices: PetroleumPrice[];
}

type RawPetroleum = Omit<Petroleum, "hsCode"> & { hsCode: string | number };
type RawPetroleumPrice = {
  date: string | number | Date;
  avgPricePerUnitUsd?: number;
  price?: number;
  unit?: string;
};

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
              h-10 w-10 p-0 font-normal text-base cursor-pointer
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

function PriceChart({ prices }: { prices: PetroleumPrice[] }) {
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

const API_BASE = import.meta.env.VITE_API_URL || "";

export default function PetroleumDetailsPage() {
  const { isLoaded, isSignedIn, getToken } = useAuth();
  const [petroleumList, setPetroleumList] = useState<Petroleum[]>([]);
  const [selectedPetroleum, setSelectedPetroleum] = useState<string>("");
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [priceHistory, setPriceHistory] = useState<PetroleumPrice[] | null>(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [searchClicked, setSearchClicked] = useState(false);

  // Load petroleum list on mount (with Clerk auth, like refineryInfoPage)
  useEffect(() => {
    if (!isLoaded) return;
    if (!isSignedIn) {
      setError("You must be signed in to view petroleum details.");
      setPetroleumList([]);
      return;
    }
    setLoading(true);
    setError("");
    getToken().then(token => {
      console.log("Petroleum List - Token:", token);
      axios.get<RawPetroleum[]>(`${API_BASE}/petroleum`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(res => {
          console.log("Petroleum List Response:", res.data);
          const mapped = (res.data ?? []).map((p) => ({
            ...p,
            hsCode: String(p.hsCode),
          }));
          setPetroleumList(mapped);
        })
        .catch(err => {
          console.error("Petroleum List Error:", err);
          if (isAxiosError(err) && err.response?.status === 403) {
            setError("Access denied (403). Please check your login or permissions.");
          } else {
            setError("Failed to load petroleum list. Please try again later.");
          }
          setPetroleumList([]);
        })
        .finally(() => setLoading(false));
    });
  }, [isLoaded, isSignedIn, getToken]);

  // Search handler
  const handleSearch = async () => {
    setSearchClicked(true);
    if (!isLoaded || !isSignedIn || !selectedPetroleum) return;
    setLoading(true);
    setError("");
    const petroleum = petroleumList.find(p => p.hsCode === selectedPetroleum);
    if (!petroleum?.hsCode) {
      setError("Invalid petroleum selection");
      setLoading(false);
      return;
    }
    try {
      const token = await getToken();
      console.log("Search - Token:", token);
      console.log("Search - Petroleum:", petroleum);
      const url = `${API_BASE}/petroleum/${petroleum.hsCode}`;
      console.log("Search - URL:", url);
      const res = await axios.get<{ prices?: RawPetroleumPrice[] }>(url, { headers: { Authorization: `Bearer ${token}` } });
      console.log("Search - Response:", res.data);
      const backendPrices = res.data.prices || [];
      console.log("Search - Backend Prices:", backendPrices);
      const mappedPrices: PetroleumPrice[] = backendPrices.map((pt) => ({
        date: typeof pt.date === "string"
          ? pt.date
          : pt.date instanceof Date
            ? pt.date.toISOString().split("T")[0]
            : pt.date
              ? new Date(pt.date).toISOString().split("T")[0]
              : new Date().toISOString().split("T")[0],
        price: typeof pt.avgPricePerUnitUsd === "number"
          ? pt.avgPricePerUnitUsd
          : typeof pt.price === "number"
            ? pt.price
            : 0,
        unit: pt.unit ?? "unit"
      }));
      console.log("Search - Mapped Prices:", mappedPrices);
      setPriceHistory(mappedPrices);
    } catch (err: unknown) {
      console.error("Search - Error:", err);
      if (isAxiosError(err) && err.response?.status === 403) {
        setError("Access denied (403). Please check your login or permissions.");
      } else {
        setError("Failed to fetch price history");
      }
      setPriceHistory([]);
    } finally {
      setLoading(false);
    }
  };

  // Clear handler
  const handleClear = () => {
    setSelectedPetroleum("");
    setSelectedDate(null);
    setPriceHistory(null);
    setError("");
    setSearchClicked(false);
  };

  const filteredPrices = useMemo(() => {
    if (!priceHistory) return [];
    if (!selectedDate) return priceHistory;
    return priceHistory.filter(price => new Date(price.date) <= selectedDate);
  }, [priceHistory, selectedDate]);

  const currentPrice = filteredPrices.length > 0 && typeof filteredPrices[filteredPrices.length - 1].price === "number"
    ? filteredPrices[filteredPrices.length - 1]
    : null;

  const selectedPetroleumData = petroleumList.find(p => p.hsCode === selectedPetroleum);

  if (!isLoaded) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-[#dcff1a]" />
        <span className="ml-4 text-gray-300 text-lg">Loading authentication…</span>
      </div>
    );
  }

  if (!isSignedIn) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-red-400 p-6 rounded-lg bg-red-900/20 border border-red-900 text-lg">
          You must be signed in to view petroleum details.
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 w-full min-h-screen">
      <div className="w-full max-w-7xl mx-auto px-4 py-8 space-y-8">
        <div className="text-center space-y-4">
          <h1 className="text-5xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
            Petroleum Details
          </h1>
          <p className="text-xl text-gray-400">
            View petroleum types and their price history
          </p>
        </div>

        <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
          <CardHeader>
            <CardTitle className="text-2xl text-white">Search Petroleum</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex flex-col md:flex-row gap-6">
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
                      {selectedPetroleumData
                        ? `${selectedPetroleumData.name} (HS ${selectedPetroleumData.hsCode})`
                        : "Select a petroleum type..."}
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
                            value={petroleum.hsCode}
                            onSelect={() => {
                              setSelectedPetroleum(petroleum.hsCode);
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
            </div>
            <div className="flex justify-end pt-4 gap-4">
              <Button
                variant="outline"
                className="border border-[#dcff1a] text-[#dcff1a] px-6 py-2 rounded-lg cursor-pointer"
                onClick={handleClear}
              >
                Clear
              </Button>
              <Button
                variant="default"
                className="bg-[#dcff1a] text-slate-900 font-bold px-6 py-2 rounded-lg cursor-pointer"
                onClick={handleSearch}
                disabled={!selectedPetroleum || loading}
              >
                Search
              </Button>
            </div>
          </CardContent>
        </Card>

        {loading && (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="h-8 w-8 animate-spin text-[#dcff1a]" />
          </div>
        )}

        {searchClicked && selectedPetroleum && priceHistory && !loading && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
              <CardHeader className="p-6">
                <CardTitle className="text-3xl text-white mb-6">
                  {selectedPetroleumData?.name}
                </CardTitle>
                <div className="space-y-6">
                  <div className="flex items-center gap-4">
                    <Droplet className="w-6 h-6 text-[#dcff1a]" />
                    <div>
                      <p className="text-sm text-gray-400">HS Code</p>
                      <p className="text-lg text-gray-300">
                        {selectedPetroleumData?.hsCode ?? ""}
                      </p>
                    </div>
                  </div>
                  {currentPrice && typeof currentPrice.price === "number" ? (
                    <div className="flex items-center gap-4">
                      <BarChart2 className="w-6 h-6 text-[#dcff1a]" />
                      <div>
                        <p className="text-sm text-gray-400">Latest Price</p>
                        <p className="text-lg text-[#dcff1a]">
                          ${currentPrice.price.toFixed(2)} / unit
                        </p>
                      </div>
                    </div>
                  ) : (
                    <div className="flex items-center gap-4">
                      <BarChart2 className="w-6 h-6 text-[#dcff1a]" />
                      <div>
                        <p className="text-sm text-gray-400">Latest Price</p>
                        <p className="text-lg text-[#dcff1a]">
                          N/A
                        </p>
                      </div>
                    </div>
                  )}
                </div>
              </CardHeader>
            </Card>

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
                                {new Date(point.date).toLocaleDateString()}
                              </TableCell>
                              <TableCell className="text-[#dcff1a] text-lg font-medium">
                                {typeof point.price === "number"
                                  ? `$${point.price.toFixed(2)}`
                                  : "N/A"}
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

        {error && (
          <div className="text-red-400 p-6 rounded-lg bg-red-900/20 border border-red-900">
            <p className="text-lg">{error}</p>
          </div>
        )}</div>
    </div>
  );
}
