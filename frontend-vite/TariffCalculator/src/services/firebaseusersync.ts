import { useAuth } from "@clerk/clerk-react";
import { useEffect } from "react";

export default function SyncUserToBackend() {
  const { getToken, isSignedIn } = useAuth();

  useEffect(() => {
    if (isSignedIn) {

      async function pingBackend() {
        try {
console.log("trying to ping backend...");

          const token = await getToken();

          const response = await fetch("http://localhost:8080/api/users/profile", {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });

          if (!response.ok) {
            console.error("Backend returned error status:", response.status);
            return;
          }

          const data = await response.json();
          console.log("Backend response:", data);


        } catch (err) {
          console.error("Error fetching token or pinging backend:", err);
        }
      }

      pingBackend();
    }
  }, [isSignedIn, getToken]);

  return null;
}
