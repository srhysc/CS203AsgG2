import axios from 'axios';
import type{ AxiosInstance} from 'axios';

import type {Tariff} from '@/services/types/countrytariff'

// Base API configuration using env variable
const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

const countrytariffapi: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

//CLERK already provides validation of a session
//gives all the cookies, tokens, etc. no need for interceptors


// Tariff service with proper typing
export const tariffService = {
  getAll: async (): Promise<Tariff[]> => {
    try {
      const response = await countrytariffapi.get<Tariff[]>('/vat');
      return response.data;
    } catch (error) {
      console.error('Error fetching countries:', error);
      throw error;
    }
  },

  // Additional methods you might need
  getByRequirements: async (importcountryid: string, exportcountryid: string, productcode:string, units:string): Promise<Tariff> => {
    if (!importcountryid || !exportcountryid || !productcode || !units) {
      throw new Error("Missing required parameters for tariff request.");
    }
    
    //Create a query string to append to URL when calling API, using method parameters
    const params = new URLSearchParams({
        importer: importcountryid,
        exporter: exportcountryid,
        hsCode: productcode,
        units: units
    });
    
    try {
      console.log(`Testing: ${import.meta.env.VITE_API_URL}/landedcost?${params}`)
        //try getting data from api using parameters(?${params} - ? indicates start of query string)
        const response = await countrytariffapi.get<Tariff>(`/landedcost?${params}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching the tariff calculation between country ${importcountryid} and ${exportcountryid} for ${units} of ${productcode}:`, error);
      throw error;
    }
  },

};

export default countrytariffapi;