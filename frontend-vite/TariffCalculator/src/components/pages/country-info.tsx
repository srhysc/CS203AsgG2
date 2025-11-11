import { useEffect, useState } from "react";
import axios, { isAxiosError } from "axios";
import { useAuth } from "@clerk/clerk-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { Loader2, Search, Globe, Calendar, ChevronLeft, ChevronRight } from "lucide-react";

interface Country {
  name: string;
  iso3n: number;
  code?: string;
}

interface VatRate {
  rate: number;
  date: string; // ISO string
}

const API_BASE = import.meta.env.VITE_API_URL || "";

// Calendar component for date picking
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

export default function CountryInfoPage() {
  const { isLoaded, isSignedIn, getToken } = useAuth();
  const [countries, setCountries] = useState<Country[]>([]);
  const [selectedCountry, setSelectedCountry] = useState<string>("");
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [vatRateResult, setVatRateResult] = useState<VatRate | null>(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [searchClicked, setSearchClicked] = useState(false);

  // Fetch countries with Clerk token
  useEffect(() => {
    if (!isLoaded || !isSignedIn) return;
    setLoading(true);
    setError("");
    getToken().then(token => {
      axios.get<Country[]>(`${API_BASE}/countries`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(res => {
          console.log("Fetched countries from backend:", res.data);
          setCountries(res.data ?? []);
        })
        .catch(err => {
          console.error("Error fetching countries:", err);
          setError("Failed to load countries. Please try again later.");
          setCountries([]);
        })
        .finally(() => setLoading(false));
    });
  }, [isLoaded, isSignedIn, getToken]);

  // Search handler: call backend service to get VAT rate for country and date
  const handleSearch = async () => {
    setSearchClicked(true);
    setError("");
    setVatRateResult(null);

    if (!selectedCountry || !selectedDate) {
      setError("Please select both a country and a date.");
      console.log("Search failed: missing country or date");
      return;
    }

    const country = countries.find(c => c.name === selectedCountry);
    console.log("Selected country object:", country);

    if (!country) {
      setError("Country not found.");
      console.log("Country not found in list.");
      return;
    }

    setLoading(true);
    try {
      const token = await getToken();
      const dateStr = selectedDate.toISOString().split("T")[0];
      // Call the backend endpoint that returns the VAT rate for a country and date
      // Example: GET /countries/{countryName}/vat-rate?date=YYYY-MM-DD
      console.log(`Requesting VAT rate for country: ${country.name}, date: ${dateStr}`);
      const res = await axios.get<VatRate>(
        `${API_BASE}/countries/${encodeURIComponent(country.name)}/vat-rate`,
        {
          params: { date: dateStr },
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      console.log("Backend VAT rate response:", res.data);
      if (res.data && typeof res.data.rate === "number" && res.data.date) {
        setVatRateResult(res.data);
        console.log("Set VAT rate result:", res.data);
      } else {
        setError("No VAT rate found for this date.");
        console.log("No VAT rate found in backend response.");
      }
    } catch (err: unknown) {
      if (isAxiosError(err)) {
        setError(
          err.response?.data?.message ||
          "Failed to fetch VAT rate for this country and date."
        );
      } else {
        setError("Failed to fetch VAT rate for this country and date.");
      }
      console.error("Error fetching VAT rate from backend:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setSelectedCountry("");
    setSelectedDate(null);
    setVatRateResult(null);
    setError("");
    setSearchClicked(false);
    console.log("Cleared all selections and results.");
  };

  if (!isLoaded) {
    console.log("Auth not loaded yet.");
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-[#dcff1a]" />
        <span className="ml-4 text-gray-300 text-lg">Loading authentication…</span>
      </div>
    );
  }

  if (!isSignedIn) {
    console.log("User not signed in.");
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-red-400 p-6 rounded-lg bg-red-900/20 border border-red-900 text-lg">
          You must be signed in to view country VAT info.
        </div>
      </div>
    );
  }

  // Log current state on every render
  console.log("Current state:", {
    countries,
    selectedCountry,
    selectedDate,
    vatRateResult,
    error,
    searchClicked,
    loading
  });

  return (
    <div className="flex-1 w-full min-h-screen">
      <div className="w-full max-w-2xl mx-auto px-4 py-8 space-y-8">
        {/* Header */}
        <div className="text-center space-y-4">
          <h1 className="text-5xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
            Country VAT Rates
          </h1>
          <p className="text-xl text-gray-400">
            Search for a country's VAT rate on a specific date
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
                              console.log("Country selected:", country.name);
                            }}
                            className="cursor-pointer text-lg py-3 text-white hover:bg-white/10"
                          >
                            <Globe className="mr-3 h-5 w-5 text-[#dcff1a]" />
                            {country.name}
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
                      onDateChange={(date) => {
                        setSelectedDate(date);
                        console.log("Date selected:", date);
                      }}
                    />
                  </PopoverContent>
                </Popover>
              </div>
            </div>
            {/* Search and Clear Buttons */}
            <div className="flex justify-end pt-4 gap-4">
              <Button
                variant="outline"
                className="border border-slate-700 text-white px-6 py-2 rounded"
                onClick={handleClear}
                disabled={loading && !selectedCountry && !selectedDate}
              >
                Clear
              </Button>
              <Button
                variant="default"
                className="bg-[#dcff1a] text-slate-900 font-bold px-6 py-2 rounded"
                onClick={handleSearch}
                disabled={!selectedCountry || !selectedDate || loading}
              >
                Search
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Results Section */}
        {searchClicked && (
          <div className="w-full max-w-xl mx-auto mt-8">
            <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
              <CardHeader>
                <CardTitle className="text-2xl text-white">
                  VAT Rate Result
                </CardTitle>
              </CardHeader>
              <CardContent>
                {loading ? (
                  <div className="flex items-center justify-center py-8">
                    <Loader2 className="h-8 w-8 animate-spin text-[#dcff1a]" />
                  </div>
                ) : error ? (
                  <div className="text-red-400">{error}</div>
                ) : vatRateResult ? (
                  <div className="space-y-4">
                    <div className="text-lg text-gray-200">
                      <span className="font-bold text-[#dcff1a]">{selectedCountry}</span> on{" "}
                      <span className="font-bold text-[#dcff1a]">{selectedDate?.toLocaleDateString()}</span>
                    </div>
                    <div className="text-3xl font-bold text-[#dcff1a]">
                      VAT Rate: {typeof vatRateResult.rate === "number" ? `${vatRateResult.rate}%` : "N/A"}
                    </div>
                    <div className="text-gray-400 text-sm">
                      (Effective from {vatRateResult.date ? new Date(vatRateResult.date).toLocaleDateString() : "N/A"})
                    </div>
                    <div className="text-xs text-gray-500">
                      {/* Log the full VAT rate object for debug */}
                      {/* Debug: {JSON.stringify(vatRateResult)} */}
                    </div>
                  </div>
                ) : (
                  <div className="text-gray-400">No VAT rate found for this date.</div>
                )}
              </CardContent>
            </Card>
          </div>
        )}

        {/* Error Display */}
        {error && !searchClicked && (
          <div className="text-red-400 p-6 rounded-lg bg-red-900/20 border border-red-900">
            <p className="text-lg">{error}</p>
          </div>
        )}
      </div>
    </div>
  );
}
