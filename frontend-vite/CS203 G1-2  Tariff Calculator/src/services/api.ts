import axios from 'axios';
import type{ AxiosInstance, InternalAxiosRequestConfig, AxiosError } from 'axios';

// Define interfaces for object data types with their values
export interface Country {
  country: string;
  vatRate: number;
}

// Define API response wrapper if your API uses one
export interface ApiResponse<T> {
  data: T;
  message?: string;
  status?: number;
}

// Base API configuration
const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add request interceptor for auth tokens
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
    const token: string | null = localStorage.getItem('token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error)
);

// Add response interceptor for error handling (optional)
api.interceptors.response.use(
  response => response,
  (error: AxiosError) => {
    // Handle common errors here
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('token');
      // Redirect to login if needed
    }
    return Promise.reject(error);
  }
);

// Country service with proper typing
export const countryService = {
  getAll: async (): Promise<Country[]> => {
    try {
      const response = await api.get<Country[]>('/vat');
      return response.data;
    } catch (error) {
      console.error('Error fetching countries:', error);
      throw error;
    }
  },

  // Additional methods you might need
  getById: async (id: string): Promise<Country> => {
    try {
      const response = await api.get<Country>(`/vat/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching country ${id}:`, error);
      throw error;
    }
  },

  create: async (country: Omit<Country, 'id'>): Promise<Country> => {
    try {
      const response = await api.post<Country>('/vat', country);
      return response.data;
    } catch (error) {
      console.error('Error creating country:', error);
      throw error;
    }
  },

  update: async (id: string, country: Partial<Country>): Promise<Country> => {
    try {
      const response = await api.put<Country>(`/vat/${id}`, country);
      return response.data;
    } catch (error) {
      console.error(`Error updating country ${id}:`, error);
      throw error;
    }
  },

  delete: async (id: string): Promise<void> => {
    try {
      await api.delete(`/vat/${id}`);
    } catch (error) {
      console.error(`Error deleting country ${id}:`, error);
      throw error;
    }
  }
};

// Generic API service for other endpoints
export const apiService = {
  get: async <T>(url: string): Promise<T> => {
    const response = await api.get<T>(url);
    return response.data;
  },

  post: async <T>(url: string, data: any): Promise<T> => {
    const response = await api.post<T>(url, data);
    return response.data;
  },

  put: async <T>(url: string, data: any): Promise<T> => {
    const response = await api.put<T>(url, data);
    return response.data;
  },

  delete: async <T>(url: string): Promise<T> => {
    const response = await api.delete<T>(url);
    return response.data;
  }
};

export default api;