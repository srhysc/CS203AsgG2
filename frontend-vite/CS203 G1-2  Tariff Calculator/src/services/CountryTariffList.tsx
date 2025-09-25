import React, { useState, useEffect } from 'react';
import { countryService } from './api';
//take country definition from api.ts
import type {Country} from './api';


const CountryTariffList: React.FC = () => {
  //declare state hooks for useState to update when getting data
  const [countries, setCountries] = useState<Country[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  //sending getAll requets to vat API to fetch country list
  useEffect(() => {
    const fetchCountries = async (): Promise<void> => {
      try {
        const countries: Country[] = await countryService.getAll();
        setCountries(countries);
        setLoading(false);
      } catch (err: any) {
        setError('Failed to fetch Countries');
        setLoading(false);
        setError(err.response?.data?.message || 'Failed to fetch countries');
      }
    };
    
    fetchCountries();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;
  
  return (
    <div className="container mt-4">
      <h2>Country List</h2>
      <div className="row">
        {countries.length === 0 ? (
          <p>No countries found</p>
        ) : (
          countries.map((country: Country) => (
            <div className="col-md-4 mb-3" key={country.country}>
              <div className="card">
                <div className="card-body">
                  <h5 className="card-title">
                    {country.country} : {country.vatRate}
                  </h5>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default CountryTariffList;