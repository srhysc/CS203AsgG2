import axios from 'axios';
import type{ AxiosInstance} from 'axios';


//Defining tariff interface based on response format
export interface tariff{
    importingCountry: string;
    exportingCountry: string;
    petroleumName: string;
    hsCode: string;
    pricePerUnit: number;
    basePrice: number;
    tariffRate: number;
    tarrifFees: number;
    vatRate: number;
    vatFees: number;
    totalLandedCost: number;
    currency: string;
}
//no need for API wrapper because just returning those two values


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
  getAll: async (): Promise<tariff[]> => {
    try {
      const response = await countrytariffapi.get<tariff[]>('/vat');
      return response.data;
    } catch (error) {
      console.error('Error fetching countries:', error);
      throw error;
    }
  },

  // Additional methods you might need
  getByRequirements: async (importcountryid: string, exportcountryid: string, productcode:string, units:string): Promise<tariff> => {
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
console.log(`/landedcost?${params}`)
        const response = await countrytariffapi.get<tariff>(`/landedcost?${params}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching the tariff calculation between country ${importcountryid} and ${exportcountryid} for ${units} of ${productcode}:`, error);
      throw error;
    }
  },

};

export default countrytariffapi;