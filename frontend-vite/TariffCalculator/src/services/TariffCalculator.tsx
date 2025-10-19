import React, { useState, useEffect} from 'react';
import type { Tariff } from './types/countrytariff'; 
import { tariffService } from './countrytariffapi';
import { TariffForm } from '@/components/ui/tarifflookupform';
import { tariffSchema } from '@/components/ui/tarifflookupform';
import { z } from 'zod';
import { CometCard } from "@/components/ui/comet-card";
import ToggleTable from "@/components/ui/tariffbreakdowntable"

import { countryService } from './countryapi';
import { petrolService } from './petroleumapi';

import type { Petroleum } from './types/petroleum';
import type {Country} from '@/services/types/country'

const TariffCalculator: React.FC = () => {
    //declare state hooks for useState to update when getting data
    const [tariffs, setTariffs] = useState<Tariff | null >(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    //state hooks to populate form dropdown
    const [countries, setCountries] = useState<Country[] | null >(null);
    const [petroleum, setPetroleum] = useState<Petroleum[] | null >(null);


    //run on render to get all countries
        useEffect(() => {
        const fetchCountries = async () => {
        try {
            const data = await countryService.getAllCountries();
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
            const data = await petrolService.getAllPetroleum();
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


    //retrieving Form Submission data from TariffForm, breaking down into strings for API call
    //call async so can call API without blocking code, Promise<void> promises to finish task
    const tariffFormSubmission = async (formData:z.infer<typeof tariffSchema>): Promise<void> =>{
                
        setLoading(true);
        setTariffs(null);
        setError(null);

        try{
            //try getting all countries
            const tariffs = await tariffService.getByRequirements(formData.importcountry,formData.exportcountry,formData.productcode,formData.units);
            //update tariffs field
            console.log('tarrifs:', tariffs);
            setTariffs(tariffs);
        } catch (err: any) {
            setError('Failed to fetch Tariffs');
            setError(err.response?.data?.message || 'Failed to fetch tariffs');
        } finally{
            setLoading(false);
        }       
    }   

    return(        
        <div className='w-fit mx-auto my-10'> 
            <CometCard>
            {/* Calculator Shell */}
            <div className="bg-gray-200 rounded-lg shadow-md p-6 w-full max-w-md overflow-visible">

                {/* Calculator Screen */}
                <div className="bg-white text-right font-mono text-3xl p-4 mb-4 rounded-md border">
                    {/* if null - false && true, does not render */}
                    {tariffs && (
                    <div>
                        <p>Total Cost: {tariffs.totalLandedCost}</p>
                        <p>Currency: {tariffs.currency}</p>
                    </div>
                    )}

                    {/* Renders if tariffs is TRUE - true && true*/}
                    {/* false && true, does not render */}
                    {loading && (<div>Loading...</div>)}

                    {/* Renders if errors is NOT NULL - true && true*/}
                    {/* if null - false && true, does not render */}
                    {error && (<div className="alert alert-danger">{error}</div>)}
                </div>

                <div>
                    {tariffs && (
                        <ToggleTable tariffObject={tariffs}/>
                    )
                    }
                </div>

                {/* Tariff form*/}
                <TariffForm onSubmit={tariffFormSubmission} countries={countries} petroleum={petroleum} clearSignal={clearFormSignal} onClear={handleClearAll}  />
            </div>
            </CometCard>
        </div>
    );
};

export default TariffCalculator;