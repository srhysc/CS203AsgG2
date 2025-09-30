import axios from 'axios';
import type{ AxiosInstance} from 'axios';
import type { Tradeagreement } from './types/tradegreement';


// Base API configuration using env variable
const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

const agreementapi: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});


//CLERK already provides validation of a session
//gives all the cookies, tokens, etc. no need for interceptors

// Country service with proper typing
export const agreeementService = {
  getAllAgreements: async (): Promise<Tradeagreement[]> => {
    //Call /Tradeagreements API endpoint
    try {
console.log(`Testing: ${import.meta.env.VITE_API_URL}/tradeAgreements`)
        //try getting data from api
        const response = await agreementapi.get<Tradeagreement[]>(`/tradeAgreements`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching the list of trade agreements`, error);
      throw error;
    }
  },

};

export default agreementapi;