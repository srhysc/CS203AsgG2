// import React, { useState, useEffect} from 'react';
// import type { Tariff } from './types/countrytariff'; 
// import { tariffService } from './counttrytariffapi';
// import { TariffForm } from '@/components/ui/tarifflookupform';
// import { tariffSchema } from '@/components/ui/tarifflookupform';
// import { z } from 'zod';
// import { CometCard } from "@/components/ui/comet-card";
// import ToggleTable from "@/components/ui/tariffbreakdowntable"

// import { countryService } from './countryapi';
// import { petrolService } from './petroleumapi';

// import type { Petroleum } from './types/petroleum';
// import type {Country} from '@/services/types/country'

// const TariffCalculator: React.FC = () => {
//     //declare state hooks for useState to update when getting data
//     const [tariffs, setTariffs] = useState<Tariff | null >(null);
//     const [loading, setLoading] = useState<boolean>(false);
//     const [error, setError] = useState<string | null>(null);

//     //state hooks to populate form dropdown
//     const [countries, setCountries] = useState<Country[] | null >(null);
//     const [petroleum, setPetroleum] = useState<Petroleum[] | null >(null);


//     //run on render to get all countries
//         useEffect(() => {
//         const fetchCountries = async () => {
//         try {
//             const data = await countryService.getAllCountries();
//             setCountries(data);

//         } catch (err) {
//             setError('Failed to load countries.');
//             console.error(err);
//         } finally {
//             setLoading(false);
//         }
//         };

//         fetchCountries();
//     }, []); // empty dependency array → runs once on mount  

//     //run on render to get all petroleum
//         useEffect(() => {
//         const fetchPetroleum = async () => {
//         try {
//             const data = await petrolService.getAllPetroleum();
//             setPetroleum(data);

//         } catch (err) {
//             setError('Failed to retrieve petroleum.');
//             console.error(err);
//         } finally {
//             setLoading(false);
//         }
//         };

//         fetchPetroleum();
//     }, []); // empty dependency array → runs once on mount



//     const [clearFormSignal, setClearFormSignal] = useState(false);

//     const handleClearAll = () => {
//         setTariffs(null);        // clears calculated results
//         setError(null);
        
//     };


//     //retrieving Form Submission data from TariffForm, breaking down into strings for API call
//     //call async so can call API without blocking code, Promise<void> promises to finish task
//     const tariffFormSubmission = async (formData:z.infer<typeof tariffSchema>): Promise<void> =>{
                
//         setLoading(true);
//         setTariffs(null);
//         setError(null);

//         try{
//             //try getting all countries
//             const tariffs = await tariffService.getByRequirements(formData.importcountry,formData.exportcountry,formData.productcode,formData.units);
//             //update tariffs field
//             console.log('tarrifs:', tariffs);
//             setTariffs(tariffs);
//         } catch (err: any) {
//             setError('Failed to fetch Tariffs');
//             setError(err.response?.data?.message || 'Failed to fetch tariffs');
//         } finally{
//             setLoading(false);
//         }       
//     }   

//     return(        
//         <div className='w-fit mx-auto my-10'> 
//             <CometCard>
//             {/* Calculator Shell */}
//             <div className="bg-gray-200 rounded-lg shadow-md p-6 w-full max-w-md overflow-visible">

//                 {/* Calculator Screen */}
//                 <div className="bg-white text-right font-mono text-3xl p-4 mb-4 rounded-md border">
//                     {/* if null - false && true, does not render */}
//                     {tariffs && (
//                     <div>
//                         <p>Total Cost: {tariffs.totalLandedCost}</p>
//                         <p>Currency: {tariffs.currency}</p>
//                     </div>
//                     )}

//                     {/* Renders if tariffs is TRUE - true && true*/}
//                     {/* false && true, does not render */}
//                     {loading && (<div>Loading...</div>)}

//                     {/* Renders if errors is NOT NULL - true && true*/}
//                     {/* if null - false && true, does not render */}
//                     {error && (<div className="alert alert-danger">{error}</div>)}
//                 </div>

//                 <div>
//                     {tariffs && (
//                         <ToggleTable tariffObject={tariffs}/>
//                     )
//                     }
//                 </div>

//                 {/* Tariff form*/}
//                 <TariffForm onSubmit={tariffFormSubmission} countries={countries} petroleum={petroleum} clearSignal={clearFormSignal} onClear={handleClearAll}  />
//             </div>
//             </CometCard>
//         </div>
//     );
// };

// export default TariffCalculator;
import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Calculator, Search, Loader2, BarChart2, ChevronDown } from "lucide-react";
import { TariffBreakdownChart } from "@/components/ui/TariffBreakdownChart";
import TariffBreakdown from "@/components/ui/tariffbreakdowntable";
import type { Tariff } from './types/countrytariff';
import { tariffService } from './counttrytariffapi';
import { countryService } from './countryapi';
import { petrolService } from './petroleumapi';
import type { Petroleum } from './types/petroleum';
import type { Country } from '@/services/types/country';

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
          <CommandInput placeholder="Search countries..." />
          <CommandEmpty>No country found.</CommandEmpty>
          <CommandGroup>
            {countries.map((country) => (
              <CommandItem
                key={country.iso3n}
                value={country.name}
                onSelect={() => {
                  onChange(country.name);
                  onOpenChange(false);
                }}
                className="cursor-pointer text-lg py-3 text-white hover:bg-white/10"
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

const TariffCalculator: React.FC = () => {
  const [chartType, setChartType] = useState<'pie' | 'bar'>('bar');
  const [tariffs, setTariffs] = useState<Tariff | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [countries, setCountries] = useState<Country[]>([]);
  const [petroleum, setPetroleum] = useState<Petroleum[]>([]);
  
  const [importCountry, setImportCountry] = useState("");
  const [exportCountry, setExportCountry] = useState("");
  const [hsCode, setHsCode] = useState("");
  const [quantity, setQuantity] = useState("");
  const [date, setDate] = useState("");
  const [importOpen, setImportOpen] = useState(false);
  const [exportOpen, setExportOpen] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [countriesData, petroleumData] = await Promise.all([
          countryService.getAllCountries(),
          petrolService.getAllPetroleum()
        ]);
        setCountries(countriesData);
        setPetroleum(petroleumData);
      } catch (err) {
        setError('Failed to load initial data');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!importCountry || !exportCountry || !hsCode || !quantity) {
      setError('Please fill in all required fields');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const result = await tariffService.getByRequirements(
        importCountry,
        exportCountry,
        hsCode,
        quantity
      );
      setTariffs(result);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to calculate tariffs');
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setImportCountry("");
    setExportCountry("");
    setHsCode("");
    setQuantity("");
    setDate("");
    setTariffs(null);
    setError(null);
  };

  return (
    <div className="flex-1 space-y-8 p-8">
      {/* Header */}
      <div className="text-center space-y-4">
        <h1 className="text-5xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
          Tariff Calculator
        </h1>
        <p className="text-xl text-gray-400">
          Calculate import tariffs and fees between countries
        </p>
      </div>

      {/* Calculator Form */}
      <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Calculator className="w-6 h-6 text-[#dcff1a]" />
            Calculate Tariffs
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <CountrySelect
                label="Importing Country"
                value={importCountry}
                onChange={setImportCountry}
                isOpen={importOpen}
                onOpenChange={setImportOpen}
                countries={countries}
              />
              <CountrySelect
                label="Exporting Country"
                value={exportCountry}
                onChange={setExportCountry}
                isOpen={exportOpen}
                onOpenChange={setExportOpen}
                countries={countries}
              />
              <div className="space-y-2">
                <label className="text-sm font-medium text-gray-400">HS Code</label>
                <Input
                  type="text"
                  value={hsCode}
                  onChange={(e) => setHsCode(e.target.value)}
                  className="bg-slate-800/50 border-white/10"
                  placeholder="Enter HS code..."
                />
              </div>
              <div className="space-y-2">
                <label className="text-sm font-medium text-gray-400">Quantity</label>
                <Input
                  type="number"
                  value={quantity}
                  onChange={(e) => setQuantity(e.target.value)}
                  className="bg-slate-800/50 border-white/10"
                  placeholder="Enter quantity..."
                />
              </div>
              <div className="space-y-2">
                <label className="text-sm font-medium text-gray-400">Date (Optional)</label>
                <Input
                  type="date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  className="bg-slate-800/50 border-white/10"
                />
              </div>
            </div>

            <div className="flex justify-end gap-4">
              <Button
                type="button"
                variant="outline"
                onClick={handleClear}
                className="bg-slate-800/50 border-white/10"
              >
                Clear
              </Button>
              <Button
                type="submit"
                disabled={loading}
                className="bg-[#dcff1a] text-slate-900 hover:bg-[#dcff1a]/90"
              >
                {loading ? (
                  <Loader2 className="w-4 h-4 animate-spin mr-2" />
                ) : (
                  <Calculator className="w-4 h-4 mr-2" />
                )}
                Calculate
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      {/* Results */}
      {tariffs && !loading && (
        <>
          <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <BarChart2 className="w-6 h-6 text-[#dcff1a]" />
                  Cost Summary
                </div>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="outline" className="bg-slate-800/50 border-white/10">
                      {chartType === 'bar' ? 'Bar Chart' : 'Pie Chart'}
                      <ChevronDown className="ml-2 h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent className="bg-slate-900 border-white/10">
                    <DropdownMenuItem onClick={() => setChartType('bar')}>
                      Bar Chart
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => setChartType('pie')}>
                      Pie Chart
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="p-6 bg-slate-800/50 rounded-lg">
                  <p className="text-sm text-gray-400 mb-2">Total Cost</p>
                  <p className="text-3xl font-bold text-[#dcff1a]">
                    {tariffs.totalLandedCost} {tariffs.currency}
                  </p>
                </div>
                <div className="p-6 bg-slate-800/50 rounded-lg">
                  <p className="text-sm text-gray-400 mb-2">Base Price</p>
                  <p className="text-3xl font-bold text-gray-300">
                    {tariffs.basePrice} {tariffs.currency}
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="p-6 bg-slate-800/50 rounded-lg">
                  <TariffBreakdownChart tariff={tariffs} type={chartType} />
                </div>
                <div className="p-6 bg-slate-800/50 rounded-lg">
                  <TariffBreakdown tariffObject={tariffs} />
                </div>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="p-4 bg-slate-800/50 rounded-lg">
                  <p className="text-sm text-gray-400">Tariff Rate</p>
                  <p className="text-lg text-[#dcff1a]">{tariffs.tariffRate}%</p>
                </div>
                <div className="p-4 bg-slate-800/50 rounded-lg">
                  <p className="text-sm text-gray-400">VAT Rate</p>
                  <p className="text-lg text-[#dcff1a]">{tariffs.vatRate}%</p>
                </div>
                <div className="p-4 bg-slate-800/50 rounded-lg">
                  <p className="text-sm text-gray-400">Tariff Fees</p>
                  <p className="text-lg text-gray-300">
                    {tariffs.tariffFees} {tariffs.currency}
                  </p>
                </div>
                <div className="p-4 bg-slate-800/50 rounded-lg">
                  <p className="text-sm text-gray-400">VAT Fees</p>
                  <p className="text-lg text-gray-300">
                    {tariffs.vatFees} {tariffs.currency}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </>
      )}

      {error && (
        <div className="rounded-lg border border-red-600 bg-red-900/40 px-4 py-3 text-red-200">
          {error}
        </div>
      )}
    </div>
  );
};

export default TariffCalculator;