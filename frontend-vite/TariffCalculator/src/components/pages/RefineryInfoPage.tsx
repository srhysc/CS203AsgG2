import React, { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { useAuth } from "@clerk/clerk-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { Calendar, ChevronLeft, ChevronRight, Building2, Globe, Activity, Loader2 } from "lucide-react";
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
  id: number;
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

export default function RefineryInfoPage() {
  const { isLoaded, isSignedIn, getToken } = useAuth();

  const [refineries, setRefineries] = useState<Refinery[]>([]);
  const [selectedRefinery, setSelectedRefinery] = useState<string>("");
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [unit, setUnit] = useState<string>("barrel");
  const [searchClicked, setSearchClicked] = useState(false);

  // Clear all inputs
  const handleClear = () => {
    setSelectedRefinery("");
    setSelectedDate(null);
    setUnit("barrel");
    setSearchClicked(false);
  };

  // Fetch refineries from backend with Clerk token
  useEffect(() => {
    if (!isLoaded) return;
    if (!isSignedIn) {
      setError("You must be signed in to view refinery information.");
      setRefineries([]);
      return;
    }
    setLoading(true);
    setError("");
    getToken().then(token => {
      axios.get<Refinery[]>(
        `${import.meta.env.VITE_API_URL}/refineries`,
        { headers: { Authorization: `Bearer ${token}` } }
      )
        .then(res => {
          setRefineries(res.data ?? []);
        })
        .catch(err => {
          setError("Failed to load refineries. Please try again later.");
          setRefineries([]);
          console.error(err);
        })
        .finally(() => setLoading(false));
    });
  }, [isLoaded, isSignedIn, getToken]);

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

  // Filter costs by date and unit (only after search button is clicked)
  const filteredCosts = useMemo(() => {
    if (!searchClicked || !refineryData || !Array.isArray(refineryData.estimated_costs)) return [];
    const costs: { date: string; cost_per_unit: number; unit: string }[] = [];
    refineryData.estimated_costs.forEach(costEntry => {
      if (!selectedDate || new Date(costEntry.date) <= selectedDate) {
        if (costEntry.costs && costEntry.costs[unit]) {
          costs.push({
            date: costEntry.date,
            cost_per_unit: costEntry.costs[unit].cost_per_unit,
            unit: costEntry.costs[unit].unit
          });
        }
      }
    });
    costs.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
    // If a date is selected and costs exist, only show the latest cost on/before that date
    if (selectedDate && costs.length > 0) {
      const latest = costs.reduce((a, b) => new Date(a.date) > new Date(b.date) ? a : b);
      return [latest];
    }
    return costs;
  }, [refineryData, selectedDate, unit, searchClicked]);

  // Available units for dropdown (defensive check added)
  const availableUnits = useMemo(() => {
    if (!refineryData || !Array.isArray(refineryData.estimated_costs) || refineryData.estimated_costs.length === 0) return [];
    const first = refineryData.estimated_costs[0];
    return first && first.costs ? Object.keys(first.costs) : [];
  }, [refineryData]);

  // Show loading if Clerk is not loaded
  if (!isLoaded) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-[#dcff1a]" />
        <span className="ml-4 text-gray-300 text-lg">Loading authentication…</span>
      </div>
    );
  }

  // Show error if not signed in
  if (!isSignedIn) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-red-400 p-6 rounded-lg bg-red-900/20 border border-red-900 text-lg">
          You must be signed in to view refinery information.
        </div>
      </div>
    );
  }

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
                      className="w-full justify-between bg-slate-800/50 border-white/10 text-white h-11 text-base cursor-pointer"
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
                  Filter by Date (optional)
                </label>
                <Popover>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      className="w-full h-11 px-3 py-2 text-lg flex items-center justify-between
                        bg-slate-800/50 border-white/10 text-white 
                        hover:bg-slate-700 hover:border-[#dcff1a] transition-colors cursor-pointer"
                    >
                      {selectedDate?.toLocaleDateString() || "Pick a date (optional)"}
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
                        hover:bg-slate-700 hover:border-[#dcff1a] transition-colors cursor-pointer"
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
            {/* Search & Clear Buttons */}
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
                onClick={() => setSearchClicked(true)}
                disabled={!selectedRefinery || !unit}
              >
                Search
              </Button>
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
        {refineryData && !loading && searchClicked && (
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
                {filteredCosts.length === 1 && selectedDate ? (
                  // Show only the single row, no graph/table
                  <div className="space-y-6">
                    <div className="flex flex-col items-start">
                      <div className="text-lg text-gray-400 mb-2">Date</div>
                      <div className="text-gray-300 text-base mb-4">
                        {new Date(filteredCosts[0].date).toLocaleDateString()}
                      </div>
                      <div className="text-lg text-gray-400 mb-2">Cost per Unit (USD)</div>
                      <div className="text-[#dcff1a] text-base font-medium mb-4">
                        ${filteredCosts[0].cost_per_unit.toFixed(2)}
                      </div>
                      <div className="text-lg text-gray-400 mb-2">Unit</div>
                      <div className="text-gray-300 text-base">
                        {filteredCosts[0].unit}
                      </div>
                    </div>
                  </div>
                ) : filteredCosts.length > 0 ? (
                  // Show graph and table for historical costs
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
                            <TableRow key={cost.date} className="hover:bg-white/5 cursor-pointer">
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
