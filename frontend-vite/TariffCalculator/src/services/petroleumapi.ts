import axios from 'axios';
import type{ AxiosInstance} from 'axios';
import type {Petroleum} from '@/services/types/petroleum'


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
export const petrolService = {
  getAllPetroleum: async (): Promise<Petroleum[]> => {
    //Call /Country API endpoint
    try {
console.log(`Testing: ${import.meta.env.VITE_API_URL}/petroleum`)
        //try getting data from api
        const response = await countryapi.get<Petroleum[]>(`/petroleum`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching the list of petroleum`, error);
      throw error;
    }
  },

};

export default countryapi;