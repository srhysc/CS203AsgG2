import { useAuth } from '@clerk/clerk-react';
import { useState,useEffect } from 'react';
import axios from 'axios';
import type{ AxiosInstance} from 'axios';

// Base API configuration using env variable
const API_BASE_URL: string = import.meta.env.VITE_API_URL || '';

const userapi: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export const getUserRole= () => {

    const [userRole, setUserRole] = useState<string>("USER");
    const { getToken } = useAuth();
    const [loading, setLoading] = useState<boolean>(true);

    // Check if user is an admin (customize this logic based on your setup)
    useEffect(() => {
    const fetchUserRole = async () =>{
        try{

        setLoading(true);

        const token = await getToken();

        //try getting token, because have to state in request
        const response = await userapi.get<String>("/api/users/roles", {
                headers: { Authorization: `Bearer ${token}` },
            });
        //if problem getting token
        setUserRole(String(response.data));

 console.log("User role: ", String(response.data))

        
        } catch (err) {
            console.error("Failed to fetch user role!!!", err);
            setUserRole("USER")
        } finally{
            setLoading(false);
        }
    };

    fetchUserRole();
    }, [getToken]);


    return {userRole,loading}
  }