import React, { useState, useEffect } from 'react';
import type { tariff } from './counttrytariffapi'; 
import { tariffService } from './counttrytariffapi';
import { TariffForm } from '@/components/ui/tarifflookupform';
import { tariffSchema } from '@/components/ui/tarifflookupform';
import { z } from 'zod';


const TariffCalculator: React.FC = () => {
    //declare state hooks for useState to update when getting data
    const [tariffs, setTariffs] = useState<tariff | null >(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);



    //retrieving Form Submission data from TariffForm, breaking down into strings for API call
                                //call async so can call API without blocking code, Promise<void> promises to finish task
    const tariffFormSubmission = async (formData:z.infer<typeof tariffSchema>): Promise<void> =>{
        console.log('Form data: ', formData);
        
        setLoading(true);
        setTariffs(null);
        setError(null);

        try{
            //try getting all countries
            const tariffs = await tariffService.getByRequirements(formData.importcountry,formData.exportcountry,formData.productcode,formData.units);
            //update tariffs field
            setTariffs(tariffs);
        } catch (err: any) {
            setError('Failed to fetch Tariffs');
            setError(err.response?.data?.message || 'Failed to fetch tariffs');
        } finally{
            setLoading(false);
        }       
    }   

    return(
        <div>
        {/* Renders if tariffs is TRUE - true && true*/}
        {/* false && true, does not render */}
        {loading && (<div>Loading...</div>)}

        {/* Renders if errors is NOT NULL - true && true*/}
        {/* if null - false && true, does not render */}
        {error && (<div className="alert alert-danger">{error}</div>)}

        {/* Renders if tariffs is NOT NULL - true && true*/}
        {/* if null - false && true, does not render */}
        {tariffs && (
        <div>
            <p>Total Cost: {tariffs.totalcost}</p>
            <p>Currency: {tariffs.currency}</p>
        </div>
        )}
        
        {/* Tariff form*/}
        <TariffForm onSubmit={tariffFormSubmission}/>
        
    </div>
    );
};

export default TariffCalculator;