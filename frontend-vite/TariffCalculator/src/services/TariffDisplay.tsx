import React, { useState, useEffect} from 'react'; 
import {motion} from "motion/react"

import { countryService } from './countryapi';
import { petrolService } from './petroleumapi';
import { agreeementService } from './agreementapi';

import type { Petroleum } from './types/petroleum';
import type {Country} from '@/services/types/country'
import type { Tradeagreement } from './types/tradegreement';

import { CountryBarChart } from '@/components/tariffdisplaycharts/country-bar-chart';
import { PetroleumBarChart } from '@/components/tariffdisplaycharts/petroleum-bar-chart';
import { AgreementDataTable, columns } from '@/components/tariffdisplaycharts/agreement-table';


const TariffLookup: React.FC = () => {
    //declare state hooks for useState to update when getting data
    const [countries, setCountries] = useState<Country[] | null >(null);
    const [petroleum, setPetroleum] = useState<Petroleum[] | null >(null);
    const [agreements, setAgreements] = useState<Tradeagreement[] | null >(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);


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


  //run on render to get all agreements
  useEffect(() => {
  console.log("trying find agreemeents")
    const fetchAgreements = async () => {
      try {
        const data = await agreeementService.getAllAgreements();
console.log("agreemeents", data)
        setAgreements(data);

      } catch (err) {
        setError('Failed to retrieve any agreements.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchAgreements();
  }, []); // empty dependency array → runs once on mount

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

    return(        
      <div>
        <div className="max-w-7xl mx-auto text-center">
        <p className="font-bold text-xl md:text-4xl dark:text-white text-black">
          Petroleum Tariff{" "}
          <span className="text-neutral-400">
            {"Searcher".split("").map((word, idx) => (
              <motion.span
                key={idx}
                className="inline-block"
                initial={{ x: -10, opacity: 0 }}
                animate={{ x: 0, opacity: 1 }}
                transition={{ duration: 0.5, delay: idx * 0.04 }}
              >
                {word}
              </motion.span>
            ))}
          </span>
        </p>
        <p className="text-sm md:text-lg text-neutral-500 max-w-2xl mx-auto py-4">
          Search for any tariff in the world.
        </p>
      </div>

    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-w-7xl mx-auto mt-8">
      <div className='w-full'>
        {countries && <CountryBarChart countries={countries} />}
        {/* Or show no countries loaded */}
        {!countries && <p>No countries found in database...</p>}
      </div>

      

      <div className='w-full overflow-visible'>
        {petroleum && <PetroleumBarChart petroleum={petroleum} />}
        {/* Or show no countries loaded */}
        {!petroleum && <p>No petroleum found in database...</p>}
      </div>
    </div>

      <div>
        {agreements && <AgreementDataTable columns={columns} data={agreements} />}
      </div>
    </div>
    );
};

export default TariffLookup;