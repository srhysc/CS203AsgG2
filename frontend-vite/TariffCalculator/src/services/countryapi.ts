import axios from 'axios';
import type { AxiosInstance } from 'axios';
import type { Country } from '@/services/types/country';
import { useAuth } from '@clerk/clerk-react';
import { useCallback } from "react";



// Base API configuration using env variable
const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

const countryapi: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});


//CLERK already provides validation of a session
//gives all the cookies, tokens, etc. no need for interceptors

// Country service with proper typing
export const useCountryService = () => {
  const { getToken } = useAuth();

  const getAllCountries = useCallback(async (): Promise<Country[]> => {
    try {
      const token = await getToken();
      console.log(`Testing: ${import.meta.env.VITE_API_URL}/countries`);
      const response = await countryapi.get<Country[]>(`/countries`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error(`Error fetching the list of countries`, error);
      throw error;
    }
  }, [getToken]);

  return { getAllCountries };
};

export default countryapi;
