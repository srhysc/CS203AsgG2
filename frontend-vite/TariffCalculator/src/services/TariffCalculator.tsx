// import React, { useState, useEffect} from 'react';
// import type { Tariff } from './types/countrytariff'; 
// import { tariffService } from './countrytariffapi';
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

//     const { getAllCountries } = countryService();
//     const { getByRequirements } = tariffService();
//     const {getAllPetroleum} = petrolService();

//     //run on render to get all countries
//         useEffect(() => {
//         const fetchCountries = async () => {
//         try {
//             const data = await getAllCountries();
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
//             const data = await getAllPetroleum();
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

//         //validation for form inputs - if countries and exist
//          const importerExists = countries?.some(
//             (c) => c.name.toLowerCase() === formData.importcountry.toLowerCase()
//         );
//         const exporterExists = countries?.some(
//             (c) => c.name.toLowerCase() === formData.exportcountry.toLowerCase()
//         );

//         if (!importerExists || !exporterExists) {
//             setError("Invalid country selection. Please choose from the list.");
//             setLoading(false);
//             return; // stop execution
//         }

//         try{
//             //try getting all countries
//             const tariffs = await getByRequirements(formData.importcountry,formData.exportcountry,formData.productcode,formData.units);
//             //update tariffs field
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
import { Calculator, Loader2, Check, CheckCircle} from "lucide-react";
import type { Tariff } from './types/countrytariff';
import { tariffService } from './countrytariffapi';
import { TariffForm } from '@/components/ui/tarifflookupform';
import { tariffSchema } from '@/components/ui/tarifflookupform';
import { z } from 'zod';
import TariffBreakdownTable from "@/components/ui/tariffbreakdowntable";
import { countryService } from './countryapi';
import { petrolService } from './petroleumapi';
import type { Petroleum } from './types/petroleum';
import type { Country } from '@/services/types/country';
import BookmarkAddButton from './BookmarkAddButton';



const TariffCalculator: React.FC = () => {
    // ...existing state declarations...
    //declare state hooks for useState to update when getting data
    const [tariffs, setTariffs] = useState<Tariff | null >(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    //state hooks to populate form dropdown
    const [countries, setCountries] = useState<Country[] | null >(null);
    const [petroleum, setPetroleum] = useState<Petroleum[] | null >(null);
    const [showSuccessToast, setShowSuccessToast] = useState(false);

    const { getAllCountries } = countryService();
    const { getByRequirements } = tariffService();
    const { getAllPetroleum } = petrolService();

    const todayISOString = new Date().toISOString().split('T')[0];
    const getFormattedDate = (date) => {
        if (date instanceof Date && !isNaN(date as any)) {
            return date.toISOString().split('T')[0];
        }
        return todayISOString;
    }

    //run on render to get all countries
        useEffect(() => {
        const fetchCountries = async () => {
        try {
            const data = await getAllCountries();
            setCountries(data);
        } catch (err) {
            setError('Failed to load countries.');
            console.error(err);
        } finally {
            setLoading(false);
        }
        };

        fetchCountries();
    }, []); // empty dependency array → runs once on mount  

    //run on render to get all petroleum
        useEffect(() => {
        const fetchPetroleum = async () => {
        try {
            const data = await getAllPetroleum();
            setPetroleum(data);

        } catch (err) {
            setError('Failed to retrieve petroleum.');
            console.error(err);
        } finally {
            setLoading(false);
        }
        };

        fetchPetroleum();
    }, []); // empty dependency array → runs once on mount



    const [clearFormSignal, setClearFormSignal] = useState(false);

    const handleClearAll = () => {
        setTariffs(null);        // clears calculated results
        setError(null);
        
    };

    const handleBookmarkSuccess = () => {
        setShowSuccessToast(true);
        setTimeout(() => setShowSuccessToast(false), 3000);
    };


    //retrieving Form Submission data from TariffForm, breaking down into strings for API call
    //call async so can call API without blocking code, Promise<void> promises to finish task
    const tariffFormSubmission = async (formData:z.infer<typeof tariffSchema>): Promise<void> =>{
                
        setLoading(true);
        setTariffs(null);
        setError(null);

        //validation for form inputs - if countries and exist
         const importerExists = countries?.some(
            (c) => c.name.toLowerCase() === formData.importcountry.toLowerCase()
        );
        const exporterExists = countries?.some(
            (c) => c.name.toLowerCase() === formData.exportcountry.toLowerCase()
        );

        if (!importerExists || !exporterExists) {
            setError("Invalid country selection. Please choose from the list.");
            setLoading(false);
            return; // stop execution
        }

        try {
            const formattedDate = getFormattedDate(formData.date);
            //try getting all countries
            //const tariffs = await getByRequirements(formData.importcountry,formData.exportcountry,formData.productcode,formData.units);
            const tariffs = await getByRequirements(
                formData.importcountry,
                formData.exportcountry,
                formData.productcode,
                formData.units,
                formattedDate 
            );
            //update tariffs field
            setTariffs(tariffs);      
        } catch (err: any) {
            setError('Failed to fetch Tariffs');
            setError(err.response?.data?.message || 'Failed to fetch tariffs');
        } finally{
            setLoading(false);
        }       
    }   

    return (
        <div className="flex-1 space-y-8 p-8">
            {/* Success Toast Notification */}
            {showSuccessToast && (
                <div className="fixed top-20 right-6 z-50 animate-slide-in">
                    <div className="bg-gradient-to-r from-emerald-500 to-[#dcff1a] text-black px-6 py-4 rounded-lg shadow-2xl flex items-center gap-3 min-w-[300px]">
                        <div className="flex-shrink-0 w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                            <Check className="w-5 h-5" />
                        </div>
                        <div>
                            <p className="font-semibold text-lg">Bookmark Added!</p>
                            <p className="text-sm text-black/80">Successfully saved to your bookmarks</p>
                        </div>
                    </div>
                </div>
            )}
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
                    <TariffForm 
                        onSubmit={tariffFormSubmission} 
                        countries={countries} 
                        petroleum={petroleum} 
                        clearSignal={clearFormSignal} 
                        onClear={handleClearAll} 
                    />
                </CardContent>
            </Card>

            {/* Results */}
            {tariffs && !loading && (
                <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <Calculator className="w-6 h-6 text-[#dcff1a]" />
                            Cost Summary
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-6">
                        <BookmarkAddButton savedResponse={tariffs} onSuccess={handleBookmarkSuccess} />

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

                        {/* Tariff Breakdown Table with Routes */}
                        <div className="p-6 bg-slate-800/50 rounded-lg">
                            <TariffBreakdownTable tariffObject={tariffs} />
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
            )}

            {/* Loading State */}
            {loading && (
                <div className="flex items-center justify-center p-8">
                    <Loader2 className="w-8 h-8 animate-spin text-[#dcff1a]" />
                </div>
            )}

            {/* Error Display */}
            {error && (
                <div className="rounded-lg border border-red-600 bg-red-900/40 px-4 py-3 text-red-200">
                    {error}
                </div>
            )}
        </div>
    );
};

export default TariffCalculator;