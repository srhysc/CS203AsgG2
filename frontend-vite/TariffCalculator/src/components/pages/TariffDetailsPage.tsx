"use client";
import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "@clerk/clerk-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { Loader2, Search, BarChart2, Calendar, ChevronLeft, ChevronRight, Droplet, Globe } from "lucide-react";

const API_BASE = import.meta.env.VITE_API_URL || "";

// Calendar picker (copy from your other files)
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

export default function TariffDetailsPage() {
  const { isLoaded, isSignedIn, getToken } = useAuth();
  const [countries, setCountries] = useState<{ name: string; iso3: string }[]>([]);
  const [petroleumList, setPetroleumList] = useState<{ name: string; hsCode: string }[]>([]);
  const [importer, setImporter] = useState<string>("");
  const [exporter, setExporter] = useState<string>("");
  const [hs6, setHs6] = useState<string>("");
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [tariff, setTariff] = useState<{
    rate: number;
    basis: string;
    note: string;
  } | null>(null);
  const [openImporter, setOpenImporter] = useState(false);
  const [openExporter, setOpenExporter] = useState(false);
  const [openPetroleum, setOpenPetroleum] = useState(false);

  // Fetch countries and petroleum list on mount
  useEffect(() => {
    if (!isLoaded || !isSignedIn) return;
    getToken().then(token => {
      axios.get(`${API_BASE}/countries`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(res => setCountries(res.data ?? []))
        .catch(() => setCountries([]));
      axios.get(`${API_BASE}/petroleum`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(res => setPetroleumList(res.data ?? []))
        .catch(() => setPetroleumList([]));
    });
  }, [isLoaded, isSignedIn, getToken]);

  const handleSearch = async () => {
    setError("");
    setTariff(null);
    if (!importer || !exporter || !hs6 || !selectedDate) {
      setError("Please fill in all fields.");
      return;
    }
    setLoading(true);
    try {
      const token = await getToken();
      const dateStr = selectedDate.toISOString().split("T")[0];
      const url = `${API_BASE}/wits/tariff?importer=${importer}&exporter=${exporter}&hs6=${hs6}&date=${dateStr}`;
      const res = await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTariff({
        rate: res.data.ratePercent,
        basis: res.data.basis,
        note: res.data.sourceNote
      });
    } catch (err: any) {
      setError("Failed to fetch tariff rate.");
      setTariff(null);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setImporter("");
    setExporter("");
    setHs6("");
    setSelectedDate(null);
    setTariff(null);
    setError("");
  };

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
          You must be signed in to view tariff details.
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 w-full min-h-screen">
      <div className="w-full max-w-3xl mx-auto px-4 py-8 space-y-8">
        <div className="text-center space-y-4">
          <h1 className="text-5xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
            Tariff Details
          </h1>
          <p className="text-xl text-gray-400">
            View tariff rates for any country pair and HS code
          </p>
        </div>

        <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
          <CardHeader>
            <CardTitle className="text-2xl text-white">Search Tariff Rate</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex flex-col md:flex-row gap-6">
              {/* Importer Country Dropdown */}
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Importer Country
                </label>
                <Popover open={openImporter} onOpenChange={setOpenImporter}>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      role="combobox"
                      aria-expanded={openImporter}
                      className="w-full justify-between bg-slate-800/50 border-white/10 text-white h-11 text-lg"
                    >
                      {importer
                        ? countries.find(c => c.iso3 === importer)?.name || importer
                        : "Select importer country..."}
                      <Globe className="ml-2 h-5 w-5 shrink-0 opacity-50" />
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
                            key={country.iso3}
                            value={country.iso3}
                            onSelect={() => {
                              setImporter(country.iso3);
                              setOpenImporter(false);
                            }}
                            className="cursor-pointer text-lg py-3 text-white hover:bg-white/10"
                          >
                            <Globe className="mr-3 h-5 w-5 text-[#dcff1a]" />
                            {country.name} ({country.iso3})
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    </Command>
                  </PopoverContent>
                </Popover>
              </div>
              {/* Exporter Country Dropdown */}
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Exporter Country
                </label>
                <Popover open={openExporter} onOpenChange={setOpenExporter}>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      role="combobox"
                      aria-expanded={openExporter}
                      className="w-full justify-between bg-slate-800/50 border-white/10 text-white h-11 text-lg"
                    >
                      {exporter
                        ? countries.find(c => c.iso3 === exporter)?.name || exporter
                        : "Select exporter country..."}
                      <Globe className="ml-2 h-5 w-5 shrink-0 opacity-50" />
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
                            key={country.iso3}
                            value={country.iso3}
                            onSelect={() => {
                              setExporter(country.iso3);
                              setOpenExporter(false);
                            }}
                            className="cursor-pointer text-lg py-3 text-white hover:bg-white/10"
                          >
                            <Globe className="mr-3 h-5 w-5 text-[#dcff1a]" />
                            {country.name} ({country.iso3})
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    </Command>
                  </PopoverContent>
                </Popover>
              </div>
            </div>
            {/* Petroleum Dropdown */}
            <div className="flex-1 mt-6">
              <label className="block text-sm font-medium text-gray-400 mb-2">
                Petroleum Type (HS code)
              </label>
              <Popover open={openPetroleum} onOpenChange={setOpenPetroleum}>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    role="combobox"
                    aria-expanded={openPetroleum}
                    className="w-full justify-between bg-slate-800/50 border-white/10 text-white h-11 text-lg"
                  >
                    {hs6
                      ? petroleumList.find(p => p.hsCode === hs6)?.name || hs6
                      : "Select petroleum type..."}
                    <Droplet className="ml-2 h-5 w-5 shrink-0 opacity-50" />
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
                            setHs6(petroleum.hsCode);
                            setOpenPetroleum(false);
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
            {/* Calendar Picker */}
            <div className="flex-1 mt-6">
              <label className="block text-sm font-medium text-gray-400 mb-2">
                Select Date
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
                disabled={loading}
              >
                Search
              </Button>
            </div>
            {loading && (
              <div className="flex items-center justify-center py-6">
                <Loader2 className="h-6 w-6 animate-spin text-[#dcff1a]" />
              </div>
            )}
            {error && (
              <div className="text-red-400 p-4 rounded-lg bg-red-900/20 border border-red-900">
                {error}
              </div>
            )}
            {tariff && (
              <Card className="bg-slate-900/40 border-slate-700 mt-6">
                <CardHeader>
                  <CardTitle className="text-xl text-[#dcff1a]">Tariff Rate</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="flex flex-col gap-2 text-lg text-white">
                    <div className="flex items-center gap-2">
                      <BarChart2 className="w-5 h-5 text-[#dcff1a]" />
                      <span>
                        Rate: <span className="font-bold text-[#dcff1a]">{tariff.rate}%</span>
                      </span>
                    </div>
                    <div>
                      Basis: <span className="font-bold">{tariff.basis}</span>
                    </div>
                    <div>
                      Source: <span className="font-bold">{tariff.note}</span>
                    </div>
                  </div>
                </CardContent>
              </Card>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}