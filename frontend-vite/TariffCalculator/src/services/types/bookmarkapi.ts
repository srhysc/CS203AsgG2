import axios, { type AxiosInstance } from 'axios';
import { useAuth } from '@clerk/clerk-react';
import type { Tariff } from '@/services/types/countrytariff';


export interface UserSavedRoute {
    savedResponse: Tariff;
    name: string;
}

export interface BookmarkRequest {
    savedResponse: Tariff;
    bookmarkName: string;
}

// Base API config
const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const useBookmarkService = () => {
  const { getToken } = useAuth();

  const getBookmarks = async (): Promise<UserSavedRoute[]> => {
    const token = await getToken();
    const response = await api.get<UserSavedRoute[]>('/users/bookmarks', {
      headers: { Authorization: `Bearer ${token}` },
    });
console.log(response.data);
    return response.data;
  };

  const addBookmark = async (
    savedResponse: Tariff,
    bookmarkName: string
  ): Promise<UserSavedRoute> => {
    const token = await getToken();
    const body: BookmarkRequest = { savedResponse, bookmarkName };
    const apiresponse = await api.post<UserSavedRoute>(
      '/users/bookmarks',
      body,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    return apiresponse.data;
  };

  return { getBookmarks, addBookmark };
};

export default api;
