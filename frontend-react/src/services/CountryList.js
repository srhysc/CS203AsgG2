import React, { useState, useEffect } from 'react';
import { countryService } from '../services/api';


const CountryList = () => {
  const [countries, setCountries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchCountries = async () => {
      try {
        const countries = await countryService.getAll();
        setCountries(countries);
        setLoading(false);
      } catch (err) {
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
          countries.map(country => (
            <div className="col-md-4 mb-3" key={country.iso6Code}>
              <div className="card">
                <div className="card-body">
                  <h5 className="card-title">{country.iso6Code} : {country.name}</h5>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default CountryList;