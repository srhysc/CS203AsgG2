import axios from 'axios';
import type { AxiosInstance } from 'axios';
import { useAuth } from '@clerk/clerk-react';
import type { Tariff } from '@/services/types/countrytariff';

// Base API configuration using env variable
const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

const countrytariffapi: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Hook-style tariff service
export const tariffService = () => {
  const { getToken } = useAuth();

  // Fetch all tariffs
  const getAll = async (): Promise<Tariff[]> => {
    try {
      const token = await getToken();
      const response = await countrytariffapi.get<Tariff[]>('/vat', {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching countries:', error);
      throw error;
    }
  };

  // Fetch tariff by specific requirements
  const getByRequirements = async (
    importcountryid: string,
    exportcountryid: string,
    productcode: string,
    units: string,
    calculationDate: string,
  ): Promise<Tariff> => {
    if (!importcountryid || !exportcountryid || !productcode || !units) {
      throw new Error('Missing required parameters for tariff request.');
    }

    const params = new URLSearchParams({
      importer: importcountryid,
      exporter: exportcountryid,
      hsCode: productcode,
      units: units,
      date: calculationDate
    });

    try {
      const token = await getToken();

      console.log(`Testing: ${import.meta.env.VITE_API_URL}/landedcost?${params} with ${token}`);
      const response = await countrytariffapi.get<Tariff>(`/landedcost?${params}`,{
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error(
        `Error fetching the tariff calculation between country ${importcountryid} and ${exportcountryid} for ${units} of ${productcode}:`,
        error
      );
      throw error;
    }
  };

  // Return functions as an object
  return { getAll, getByRequirements };
};

export default countrytariffapi;
