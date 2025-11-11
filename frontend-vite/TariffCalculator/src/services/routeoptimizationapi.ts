// not used
import axios, { type AxiosInstance } from 'axios';
import { useAuth } from '@clerk/clerk-react';
import type { RouteOptimizationResponse } from './types/routeOptimization';

// Base API configuration using env variable
const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

const routeoptimizationapi: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request parameters interface
export interface RouteOptimizationRequest {
  exporter: string;
  importer: string;
  hsCode: string;
  units: number;
  calculationDate: string;
}

// Hook-style Route Optimization service
export const useRouteOptimizationService = () => {
  const { getToken } = useAuth();

  // Fetch optimized routes based on inputs
  const getOptimizedRoutes = async (
    request: RouteOptimizationRequest
  ): Promise<RouteOptimizationResponse> => {
    const { exporter, importer, hsCode, units, calculationDate } = request;

    if (!exporter || !importer || !hsCode || !units || !calculationDate) {
      throw new Error('Missing required parameters for route optimization request.');
    }

    try {
      const token = await getToken();
      console.log('🔑 Token for route optimization:', token ? 'Token exists' : 'NO TOKEN');
      console.log(`📡 Sending route optimization request to: ${API_BASE_URL}/route-optimization`, request);

      const response = await routeoptimizationapi.post<RouteOptimizationResponse>(
        '/route-optimization',
        request,
        {
          headers: { 
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
        }
      );

      console.log('✅ Route optimization response:', response.data);
      return response.data;
    } catch (error: unknown) {
      if (error instanceof Error) {
        console.error('❌ Error fetching optimized route:', error);
      } else {
        console.error('❌ Unknown error fetching optimized route:', error);
      }
      throw error;
    }
  };

  return { getOptimizedRoutes };
};
