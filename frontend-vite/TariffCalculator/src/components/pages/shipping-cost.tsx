"use client";
import { useEffect, useState, useMemo } from "react";
import axios, { isAxiosError } from "axios";
import { useAuth } from "@clerk/clerk-react";
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

// Only allow these units
const ALLOWED_UNITS = ["barrel", "ton"];

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
  const { isLoaded, isSignedIn, getToken } = useAuth();

  const [countries, setCountries] = useState<Country[]>([]);
  const [origin, setOrigin] = useState<string>("");
  const [destination, setDestination] = useState<string>("");
  const [originOpen, setOriginOpen] = useState(false);
  const [destOpen, setDestOpen] = useState(false);
  const [unit, setUnit] = useState<string>("");
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [shippingData, setShippingData] = useState<ShippingFeeResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [hasSearched, setHasSearched] = useState(false);

  // Fetch countries with Clerk token
  useEffect(() => {
    if (!isLoaded || !isSignedIn) return;
    getToken().then(token => {
      axios.get<Country[]>(`${API_BASE}/countries`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(res => setCountries(res.data))
        .catch(() => setError("Failed to load countries"));
    });
  }, [isLoaded, isSignedIn, getToken]);

  // Search function
  const handleSearch = async () => {
    if (!isLoaded || !isSignedIn) return;
    if (!origin || !destination) {
      setError("Please select both countries");
      return;
    }
    setLoading(true);
    setError("");
    setHasSearched(true);

    try {
      const token = await getToken();
      const params = new URLSearchParams();
      if (selectedDate) params.append('date', selectedDate.toISOString().split('T')[0]);
      if (unit) params.append('unit', unit);
      const queryString = params.toString();
      const url = `${API_BASE}/shipping-fees/${origin}/${destination}/cost${queryString ? '?' + queryString : ''}`;

      const response = await axios.get<ShippingFeeEntry[] | { message: string }>(url, {
        headers: { Authorization: `Bearer ${token}` }
      });

      if (!Array.isArray(response.data)) {
        setError("Unexpected response from server.");
        setShippingData(null);
        return;
      }

      // Only keep allowed units
      const validEntries = response.data.map((entry: ShippingFeeEntry) => ({
        ...entry,
        costs: Object.fromEntries(
          Object.entries(entry.costs).filter(([k]) => ALLOWED_UNITS.includes(k))
        )
      })).filter(entry => Object.keys(entry.costs).length > 0);

      const originCountry = countries.find(c => c.iso3 === origin);
      const destCountry = countries.find(c => c.iso3 === destination);

      setShippingData({
        country1Name: originCountry?.name || origin,
        country2Name: destCountry?.name || destination,
        country1Iso3: origin,
        country2Iso3: destination,
        country1IsoNumeric: originCountry?.code || "",
        country2IsoNumeric: destCountry?.code || "",
        shippingFees: validEntries
      });
    } catch (err: unknown) {
      if (isAxiosError(err)) {
        setError(err.response?.data?.message || "Failed to fetch shipping costs");
      } else {
        setError("Failed to fetch shipping costs");
      }
      setShippingData(null);
    } finally {
      setLoading(false);
    }
  };

  // Clear function
  const handleClear = () => {
    setOrigin("");
    setDestination("");
    setUnit("");
    setSelectedDate(null);
    setShippingData(null);
    setError("");
    setHasSearched(false);
  };

  // Prepare unit options - only allowed units
  const unitOptions = useMemo(() => {
    return ALLOWED_UNITS.filter(u =>
      shippingData?.shippingFees.some(entry => entry.costs[u])
    );
  }, [shippingData]);

  // Prepare filtered cost history
  const filteredCosts = useMemo(() => {
    if (!shippingData || !Array.isArray(shippingData.shippingFees)) return [];
    const entries = selectedDate
      ? shippingData.shippingFees.filter(e => new Date(e.date) <= selectedDate)
      : shippingData.shippingFees;

    const mapEntry = (entry: ShippingFeeEntry): { date: string; cost: number; unit: string } | null => {
      if (unit) {
        const costDetails = entry.costs[unit];
        if (!costDetails) return null;
        return {
          date: entry.date,
          cost: costDetails.costPerUnit,
          unit: costDetails.unit,
        };
      }

      const firstUnit = ALLOWED_UNITS.find(u => entry.costs[u]);
      if (!firstUnit) return null;

      const costDetails = entry.costs[firstUnit];
      return {
        date: entry.date,
        cost: costDetails.costPerUnit,
        unit: costDetails.unit,
      };
    };

    return entries
      .map(mapEntry)
      .filter((entry): entry is { date: string; cost: number; unit: string } => !!entry && entry.cost > 0);
  }, [shippingData, selectedDate, unit]);

  // Prepare country dropdown options
  const countryOptions = useMemo(() => {
    return countries.map(c => ({
      label: `${c.name} (${c.iso3})`,
      value: c.iso3,
    }));
  }, [countries]);

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
          You must be signed in to view shipping costs.
        </div>
      </div>
    );
  }

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
        <CardContent className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
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
                    {destination ? countries.find(c => c.iso3 === destination)?.name : "Select country 2..."}
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
              <label className="text-sm font-medium text-gray-400 mb-2 block">
                Filter by Date <span className="text-xs text-gray-500">(optional)</span>
              </label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    className="w-full h-10 px-3 py-2 text-base flex items-center justify-between
                      bg-slate-800/50 border-white/10 text-white 
                      hover:bg-slate-700 hover:border-[#dcff1a] transition-colors"
                  >
                    {selectedDate?.toLocaleDateString() || "All dates"}
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
              <label className="text-sm font-medium text-gray-400 mb-2 block">
                Unit <span className="text-xs text-gray-500">(optional)</span>
              </label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    className="w-full h-10 px-3 py-2 text-base flex items-center justify-between
                      bg-slate-800/50 border-white/10 text-white 
                      hover:bg-slate-700 hover:border-[#dcff1a] transition-colors"
                  >
                    {unit || "All units"}
                    <BarChart2 className="ml-2 h-4 w-4 opacity-50" />
                  </Button>
                </PopoverTrigger>
                <PopoverContent align="start" className="p-0 bg-slate-900 border-white/10">
                  <Command>
                    <CommandInput placeholder="Search unit..." className="text-white" />
                    <CommandEmpty className="text-gray-400">No unit found.</CommandEmpty>
                    <CommandGroup>
                      <CommandItem
                        value=""
                        onSelect={() => setUnit("")}
                        className="text-white hover:bg-white/10"
                      >
                        All units
                      </CommandItem>
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
          </div>

          {/* Action Buttons */}
          <div className="flex gap-4 justify-end">
            <Button
              variant="outline"
              onClick={handleClear}
              className="px-8 bg-slate-800/50 border-white/10 text-white hover:bg-slate-700"
            >
              Clear
            </Button>
            <Button
              onClick={handleSearch}
              disabled={!origin || !destination || loading}
              className="px-8 bg-[#dcff1a] text-slate-900 hover:bg-[#dcff1a]/90 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Searching...
                </>
              ) : (
                <>
                  <Search className="mr-2 h-4 w-4" />
                  Search
                </>
              )}
            </Button>
          </div>

          {/* Error Display */}
          {error && (
            <div className="rounded-md border border-red-600 bg-red-900/40 px-4 py-3 text-red-200">
              {error}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Loading State */}
      {loading && (
        <div className="flex items-center justify-center h-[400px]">
          <Loader2 className="h-8 w-8 animate-spin text-[#dcff1a]" />
          <span className="ml-4 text-gray-300 text-lg">Loading shipping costs...</span>
        </div>
      )}

      {/* Empty State */}
      {!loading && !shippingData && hasSearched && (
        <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
          <CardContent className="flex flex-col items-center justify-center py-16">
            <Ship className="h-16 w-16 text-gray-600 mb-4" />
            <p className="text-xl text-gray-400">No shipping data found for this route</p>
            <p className="text-sm text-gray-500 mt-2">Try searching for a different country pair</p>
          </CardContent>
        </Card>
      )}

      {/* Results */}
      {shippingData && !loading && (
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
              {shippingData && shippingData.shippingFees.length > 0 && (
                <div className="p-4 bg-slate-800/50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <BarChart2 className="h-5 w-5 text-[#dcff1a]" />
                    <div className="flex-1">
                      <p className="text-sm text-gray-400 mb-2">Latest Costs</p>
                      {unit ? (
                        filteredCosts.length > 0 && (
                          <p className="text-2xl font-bold text-[#dcff1a]">
                            ${filteredCosts[filteredCosts.length - 1].cost.toFixed(2)} / {filteredCosts[filteredCosts.length - 1].unit}
                          </p>
                        )
                      ) : (
                        <div className="space-y-1">
                          {(() => {
                            const latestEntry = shippingData.shippingFees
                              .filter(e => !selectedDate || new Date(e.date) <= selectedDate)
                              .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())[0];
                            return latestEntry && ALLOWED_UNITS.filter(u => latestEntry.costs[u]).map((key) => (
                              <div key={key} className="flex items-baseline gap-2">
                                <span className="text-xl font-bold text-[#dcff1a]">
                                  ${latestEntry.costs[key].costPerUnit.toFixed(2)}
                                </span>
                                <span className="text-sm text-gray-400">/ {latestEntry.costs[key].unit}</span>
                              </div>
                            ));
                          })()}
                        </div>
                      )}
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
                          {!unit && <TableHead className="text-gray-400">All Units</TableHead>}
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {unit ? (
                          filteredCosts.map((point, idx) => (
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
                          ))
                        ) : (
                          shippingData?.shippingFees
                            .filter(entry => !selectedDate || new Date(entry.date) <= selectedDate)
                            .map((entry, idx) => (
                              <TableRow key={idx} className="hover:bg-white/5">
                                <TableCell className="text-gray-300">
                                  {new Date(entry.date).toLocaleDateString()}
                                </TableCell>
                                <TableCell className="text-gray-300" colSpan={2}>
                                  <div className="space-y-1">
                                    {ALLOWED_UNITS.filter(u => entry.costs[u]).map((unitKey) => (
                                      <div key={unitKey} className="flex justify-between">
                                        <span className="text-gray-400">{entry.costs[unitKey].unit}:</span>
                                        <span className="text-[#dcff1a] font-medium">
                                          ${entry.costs[unitKey].costPerUnit.toFixed(2)}
                                        </span>
                                      </div>
                                    ))}
                                  </div>
                                </TableCell>
                              </TableRow>
                            ))
                        )}
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
    </div>
  );
}
