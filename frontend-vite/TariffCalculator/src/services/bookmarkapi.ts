import axios, { type AxiosInstance } from 'axios';
import { useAuth } from '@clerk/clerk-react';
import type { Tariff } from '@/services/types/countrytariff';


export interface UserSavedRoute {
  request: Tariff;
  name: string;
}

export interface BookmarkRequest {
  request: Tariff;
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
    const response = await api.get<UserSavedRoute[]>('/api/users/bookmarks', {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  };

  const addBookmark = async (
    request: Tariff,
    bookmarkName: string
  ): Promise<UserSavedRoute> => {
    const token = await getToken();
    const body: BookmarkRequest = { request, bookmarkName };
    const response = await api.post<UserSavedRoute>(
      '/api/users/bookmarks',
      body,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    return response.data;
  };

  return { getBookmarks, addBookmark };
};

export default api;
