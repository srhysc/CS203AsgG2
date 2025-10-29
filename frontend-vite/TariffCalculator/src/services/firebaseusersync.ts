import { useAuth } from "@clerk/clerk-react";
import { useEffect } from "react";

export default function SyncUserToBackend() {
  const { getToken, isSignedIn } = useAuth();

  useEffect(() => {
    if (isSignedIn) {

      async function pingBackend() {
        try {
          const token = await getToken();

          await fetch("http://localhost:8080/api/users/profile", {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
        } catch (err) {
          console.error("Error fetching token or pinging backend:", err);
        }
      }

      pingBackend();
    }
  }, [isSignedIn, getToken]);

  return null;
}
