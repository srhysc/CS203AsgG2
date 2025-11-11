import { useAuth } from '@clerk/clerk-react';
import { useState, useEffect } from 'react';
import axios from 'axios';
import type { AxiosInstance } from 'axios';

// Base API configuration using env variable
const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

const userapi: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export const useUserRole = () => {
  const [userRole, setUserRole] = useState<string>("USER");
  const { getToken } = useAuth();
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchUserRole = async () => {
      try {
        setLoading(true);

        const token = await getToken();

        const response = await userapi.get<string>("/api/users/roles", {
          headers: { Authorization: `Bearer ${token}` },
        });

        setUserRole(String(response.data));
        console.log("User role: ", String(response.data));
      } catch (err) {
        console.error("Failed to fetch user role!!!", err);
        setUserRole("USER");
      } finally {
        setLoading(false);
      }
    };

    fetchUserRole();
  }, [getToken]);

  return { userRole, loading };
};
