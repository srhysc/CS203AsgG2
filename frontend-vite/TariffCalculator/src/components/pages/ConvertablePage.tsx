import React, { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { useAuth } from "@clerk/clerk-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";

interface ErrorBoundaryProps {
  children: React.ReactNode;
}

// Error Boundary Component
class ErrorBoundary extends React.Component<ErrorBoundaryProps, { hasError: boolean }> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false };
  }
  static getDerivedStateFromError() {
    return { hasError: true };
  }
  render() {
    if (this.state.hasError) {
      return (
        <div className="flex items-center justify-center py-12">
          <div className="text-red-400 p-6 rounded-lg bg-red-900/20 border border-red-900 text-lg">
            Something went wrong. Please refresh the page.
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}

interface ConvertTo {
  hscode: string;
  name: string;
  yield_percent: number;
}
interface Convertable {
  hscode: string;
  name: string;
  to: ConvertTo[];
}

function ConvertablePageInner() {
  const { isLoaded, isSignedIn, getToken } = useAuth();
  const [convertables, setConvertables] = useState<Convertable[]>([]);
  const [selectedFrom, setSelectedFrom] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [searchClicked, setSearchClicked] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!isLoaded) return;
    if (!isSignedIn) {
      setError("You must be signed in to view convertable information.");
      setConvertables([]);
      return;
    }
    setLoading(true);
    setError("");
    getToken().then(token => {
      axios.get<Convertable[]>(
        `${import.meta.env.VITE_API_URL}/convertables`,
        { headers: { Authorization: `Bearer ${token}` } }
      )
        .then(res => {
          if (Array.isArray(res.data)) {
            // Only keep entries with valid hscode and name
            setConvertables(res.data.filter(c => c.hscode && c.name));
          } else {
            setConvertables([]);
            setError("Unexpected data format from server.");
          }
        })
        .catch(() => {
          setError("Failed to load convertables. Please try again later.");
          setConvertables([]);
        })
        .finally(() => setLoading(false));
    }).catch(() => {
      setError("Authentication error. Please sign in again.");
      setLoading(false);
    });
  }, [isLoaded, isSignedIn, getToken]);

  const options = useMemo(() =>
    Array.isArray(convertables)
      ? convertables.map(c => ({
          label: `${c.name} (${c.hscode})`,
          value: c.hscode
        }))
      : [],
    [convertables]
  );

  const selectedConvertable = useMemo(() => {
    if (!selectedFrom || !Array.isArray(convertables)) return undefined;
    return convertables.find(c => c.hscode === selectedFrom);
  }, [selectedFrom, convertables]);

  const handleClear = () => {
    setSelectedFrom("");
    setSearchClicked(false);
  };

  if (!isLoaded) {
    return (
      <div className="flex items-center justify-center py-12">
        <span className="ml-4 text-gray-300 text-lg">Loading authentication…</span>
      </div>
    );
  }
  if (!isSignedIn) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-red-400 p-6 rounded-lg bg-red-900/20 border border-red-900 text-lg">
          You must be signed in to view convertable information.
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 w-full min-h-screen">
      <div className="w-full max-w-3xl mx-auto px-4 py-8 space-y-8">
        <div className="text-center space-y-4">
          <h1 className="text-5xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
            Convertable Breakdown
          </h1>
          <p className="text-xl text-gray-400">
            Select a product to see what it can be broken down into
          </p>
        </div>
        <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
          <CardHeader>
            <CardTitle className="text-2xl text-white">Search Convertables</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex flex-col gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Select Product (From)
                </label>
                <Popover open={open} onOpenChange={setOpen}>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      role="combobox"
                      aria-expanded={open}
                      className="w-full justify-between bg-slate-800/50 border-white/10 text-white h-11 text-base cursor-pointer"
                    >
                      {selectedConvertable
                        ? `${selectedConvertable.name} (${selectedConvertable.hscode})`
                        : "Select a product..."}
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-[400px] p-0 bg-slate-900 border-white/10">
                    <Command>
                      <CommandInput
                        placeholder="Search products..."
                        className="h-11 text-base text-white"
                      />
                      <CommandEmpty className="text-base py-4 text-gray-400">
                        No product found.
                      </CommandEmpty>
                      <CommandGroup>
                        {options.map((option) => (
                          <CommandItem
                            key={option.value}
                            value={option.value}
                            onSelect={() => {
                              setSelectedFrom(option.value);
                              setOpen(false);
                            }}
                            className="cursor-pointer text-base py-3 text-white hover:bg-white/10"
                          >
                            {option.label}
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    </Command>
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
                onClick={() => setSearchClicked(true)}
                disabled={!selectedFrom}
              >
                Search
              </Button>
            </div>
          </CardContent>
        </Card>
        {loading && (
          <div className="flex items-center justify-center py-12">
            <span className="ml-4 text-gray-300 text-lg">Loading…</span>
          </div>
        )}
        {selectedConvertable && searchClicked && !loading && (
          <Card className="bg-white/5 backdrop-blur-lg border border-white/10 mt-8">
            <CardHeader className="p-6">
              <CardTitle className="text-3xl text-white mb-6">
                {selectedConvertable.name} ({selectedConvertable.hscode})
              </CardTitle>
              <div className="space-y-4">
                <div className="text-lg text-gray-400 mb-2">Breakdown Products:</div>
                <div className="overflow-auto">
                  <table className="w-full text-left">
                    <thead>
                      <tr>
                        <th className="text-gray-400 text-lg py-2">HS Code</th>
                        <th className="text-gray-400 text-lg py-2">Name</th>
                        <th className="text-gray-400 text-lg py-2">Yield (%)</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(selectedConvertable.to ?? []).map((to) => (
                        <tr key={to.hscode} className="hover:bg-white/5 cursor-pointer">
                          <td className="text-gray-300 text-base py-2">{to.hscode}</td>
                          <td className="text-gray-300 text-base py-2">{to.name}</td>
                          <td className="text-[#dcff1a] text-base font-medium py-2">{to.yield_percent}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </CardHeader>
          </Card>
        )}
        {error && (
          <div className="text-red-400 p-6 rounded-lg bg-red-900/20 border border-red-900">
            <p className="text-lg">{error}</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default function ConvertablePage() {
  return (
    <ErrorBoundary>
      <ConvertablePageInner /> </ErrorBoundary>
  );
}
