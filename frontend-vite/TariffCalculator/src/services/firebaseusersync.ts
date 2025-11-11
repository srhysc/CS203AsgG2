import { useAuth } from "@clerk/clerk-react";
import { useEffect, useRef } from "react";

const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export default function SyncUserToBackend() {
  const { getToken, isSignedIn, isLoaded } = useAuth();
  const hasSynced = useRef(false);

  useEffect(() => {
    // Check if user is signed in
    // If not signed in or still loading, reset sync flag and exit, ensures re-sync when they sign in again
    if (!isLoaded || !isSignedIn) {
      hasSynced.current = false; // Reset
      return; 
    }

    // IF User is signed in - check if we already synced
    // If we already synced this session, don't do it again
    if (hasSynced.current) {
      console.log("User already synced with backend");
      return; // Exit - already synced
    }

    // STEP 3: User is signed in AND we haven't synced yet
    // Proceed with sync

    async function syncUser() {
      try {
        console.log("Syncing user with backend...");
        
        const token = await getToken();
        
        const response = await fetch(`${API_BASE}/api/users/profile`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          console.error("Backend sync failed with status:", response.status);
          return;
        }

        const data = await response.json();
        console.log("User synced successfully:", data);
        //set hassynced so dont have to ping backend again
        hasSynced.current = true;

      } catch (err) {
        console.error("Error syncing user to backend:", err);
      }
    }

    syncUser();
  }, [isSignedIn, isLoaded, getToken]);

  return null;
}
