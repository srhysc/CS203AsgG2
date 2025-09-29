import React, { useState, useEffect} from 'react'; 
import {motion} from "motion/react"
import { WorldMap } from '@/components/ui/world-map';

import { countryService } from './countryapi';
import type {Country} from '@/services/types/country'

import { CountryBarChart } from '@/components/tariffdisplaycharts/country-bar-chart';



const TariffLookup: React.FC = () => {
    //declare state hooks for useState to update when getting data
    const [countries, setCountries] = useState<Country[] | null >(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);


    //run on render
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

  //if (loading) return <div>Loading...</div>;
  //if (error) return <div>{error}</div>;

    return(        
        <div className=" py-40 w-full">
        <div className="max-w-7xl mx-auto text-center dark:bg-black bg-white">
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

      <WorldMap
        dots={[
          {
            start: {
              lat: 64.2008,
              lng: -149.4937,
            }, // Alaska (Fairbanks)
            end: {
              lat: 34.0522,
              lng: -118.2437,
            }, // Los Angeles
          },
          {
            start: { lat: 64.2008, lng: -149.4937 }, // Alaska (Fairbanks)
            end: { lat: -15.7975, lng: -47.8919 }, // Brazil (Brasília)
          },
          {
            start: { lat: -15.7975, lng: -47.8919 }, // Brazil (Brasília)
            end: { lat: 38.7223, lng: -9.1393 }, // Lisbon
          },
          {
            start: { lat: 51.5074, lng: -0.1278 }, // London
            end: { lat: 28.6139, lng: 77.209 }, // New Delhi
          },
          {
            start: { lat: 28.6139, lng: 77.209 }, // New Delhi
            end: { lat: 43.1332, lng: 131.9113 }, // Vladivostok
          },
          {
            start: { lat: 28.6139, lng: 77.209 }, // New Delhi
            end: { lat: -1.2921, lng: 36.8219 }, // Nairobi
          },
        ]}
      />

      <div>
        {countries && <CountryBarChart countries={countries} />}
        {/* Or show no countries loaded */}
        {!countries && <p>No countries found in database...</p>}
      </div>
    </div>
    );
};

export default TariffLookup;