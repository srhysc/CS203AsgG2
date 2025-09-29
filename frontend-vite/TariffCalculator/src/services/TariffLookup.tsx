import React, { useState} from 'react';
import type { tariff } from './counttrytariffapi'; 
import { tariffService } from './counttrytariffapi';
import { TariffForm } from '@/components/ui/tarifflookupform';
import { tariffSchema } from '@/components/ui/tarifflookupform';
import { z } from 'zod';
import { CometCard } from "@/components/ui/comet-card";
import ToggleTable from "@/components/ui/tariffbreakdowntable"



const TariffLookup: React.FC = () => {
    //declare state hooks for useState to update when getting data
    const [tariffs, setTariffs] = useState<tariff | null >(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);



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
            <div className="bg-gray-200 rounded-lg shadow-md p-6 w-full max-w-md">

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
                <TariffForm onSubmit={tariffFormSubmission}/>
            </div>
            </CometCard>
        </div>
    );
};

export default TariffLookup;