// import axios, { type AxiosInstance } from 'axios';
// import { useAuth } from '@clerk/clerk-react';

// // Base API configuration using env variable
// const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

// const routeoptimizationapi: AxiosInstance = axios.create({
//   baseURL: API_BASE_URL,
//   headers: {
//     'Content-Type': 'application/json',
//   },
// });

// // Define the interface based on your backend response
// export interface RouteOptimizationResult {
//   originCountry: string;
//   destinationCountry: string;
//   optimizedRoute: string[];
//   totalDistanceKm: number;
//   estimatedCost: number;
//   estimatedDurationHours: number;
//   routeBreakdown?: {
//     segment: string;
//     cost: number;
//     distanceKm: number;
//     transportMode: string;
//   }[];
// }

// // Request parameters interface
// export interface RouteOptimizationRequest {
//   exporter: string;
//   importer: string;
//   hsCode: string;
//   units: number;
//   calculationDate: string;
// }

// // Hook-style Route Optimization service
// export const routeOptimizationService = () => {
//   const { getToken } = useAuth();

//   // Fetch optimized routes based on inputs
//   const getOptimizedRoutes = async (
//     request: RouteOptimizationRequest
//   ): Promise<RouteOptimizationResult> => {
//     const { exporter, importer, hsCode, units, calculationDate } = request;

//     if (!exporter || !importer || !hsCode || !units || !calculationDate) {
//       throw new Error('Missing required parameters for route optimization request.');
//     }

//     try {
//       const token = await getToken();
//       console.log(`📡 Sending route optimization request to: ${API_BASE_URL}/route-optimization`, request);

//       const response = await routeoptimizationapi.post<RouteOptimizationResult>(
//         '/route-optimization',
//         request,
//         {
//           headers: { Authorization: `Bearer ${token}` },
//         }
//       );

//       console.log('✅ Route optimization response:', response.data);
//       return response.data;
//     } catch (error) {
//       console.error('❌ Error fetching optimized route:', error);
//       throw error;
//     }
//   };

//   return { getOptimizedRoutes };
// };

// export default routeoptimizationapi;

// import axios, { type AxiosInstance } from 'axios';
// import { useAuth } from '@clerk/clerk-react';
// import type { RouteOptimizationResponse } from './types/routeOptimization';

// // Base API configuration using env variable
// const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

// const routeoptimizationapi: AxiosInstance = axios.create({
//   baseURL: API_BASE_URL,
//   headers: {
//     'Content-Type': 'application/json',
//   },
// });

// // Request parameters interface
// export interface RouteOptimizationRequest {
//   exporter: string;
//   importer: string;
//   hsCode: string;
//   units: number;
//   calculationDate: string;
// }

// // Hook-style Route Optimization service
// export const routeOptimizationService = () => {
//   const { getToken } = useAuth();

//   // Fetch optimized routes based on inputs
//   const getOptimizedRoutes = async (
//     request: RouteOptimizationRequest
//   ): Promise<RouteOptimizationResponse> => {
//     const { exporter, importer, hsCode, units, calculationDate } = request;

//     if (!exporter || !importer || !hsCode || !units || !calculationDate) {
//       throw new Error('Missing required parameters for route optimization request.');
//     }

//     try {
//       const token = await getToken();
//       console.log(`📡 Sending route optimization request to: ${API_BASE_URL}/route-optimization`, request);

//       const response = await routeoptimizationapi.post<RouteOptimizationResponse>(
//         '/route-optimization',
//         request,
//         {
//           headers: { Authorization: `Bearer ${token}` },
//         }
//       );

//       console.log('✅ Route optimization response:', response.data);
//       return response.data;
//     } catch (error) {
//       console.error('❌ Error fetching optimized route:', error);
//       throw error;
//     }
//   };

//   return { getOptimizedRoutes };
// };

// export default routeoptimizationapi;
/*
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
export const routeOptimizationService = () => {
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
      console.log(`📡 Sending route optimization request to: ${API_BASE_URL}/route-optimization`, request);

      const response = await routeoptimizationapi.post<RouteOptimizationResponse>(
        '/route-optimization',
        request,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      console.log('✅ Route optimization response:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ Error fetching optimized route:', error);
      throw error;
    }
  };

  return { getOptimizedRoutes };
};

// Don't export as default, or if you do, also keep the named export
export default routeoptimizationapi;
*/
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
export const routeOptimizationService = () => {
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
    } catch (error: any) {
      console.error('❌ Error fetching optimized route:', error);
      console.error('❌ Error response:', error.response?.data);
      console.error('❌ Error status:', error.response?.status);
      console.error('❌ Error headers:', error.response?.headers);
      throw error;
    }
  };

  return { getOptimizedRoutes };
};