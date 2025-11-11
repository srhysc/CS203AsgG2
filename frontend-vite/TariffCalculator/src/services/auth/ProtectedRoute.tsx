// src/components/auth/ProtectedRoute.tsx
import { useAuth, SignInButton } from "@clerk/clerk-react";
import { motion } from "framer-motion";
import { Shield, Lock } from "lucide-react";
import { useUserRole } from "@/services/clerkauthentication";

interface ProtectedRouteProps {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isSignedIn, isLoaded } = useAuth();

  // Loading state while Clerk is checking authentication
  if (!isLoaded) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="flex flex-col items-center gap-4"
        >
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-[#dcff1a] border-t-transparent"></div>
          <p className="text-gray-300 text-lg">Loading...</p>
        </motion.div>
      </div>
    );
  }

  // Not signed in - show sign in prompt
  if (!isSignedIn) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="flex items-center justify-center min-h-[60vh]"
      >
        <div className="max-w-md w-full mx-auto p-8 bg-slate-900/50 backdrop-blur-lg rounded-2xl border border-white/10 shadow-xl">
          <div className="flex flex-col items-center gap-6 text-center">
            <div className="p-4 bg-[#dcff1a]/10 rounded-full">
              <Lock className="w-12 h-12 text-[#dcff1a]" />
            </div>
            
            <div>
              <h2 className="text-2xl font-bold text-white mb-2">
                Authentication Required
              </h2>
              <p className="text-gray-400">
                Please sign in to access this page
              </p>
            </div>

            <SignInButton mode="modal">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="px-6 py-3 bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-slate-900 font-semibold rounded-lg shadow-lg hover:shadow-emerald-400/40 transition-all"
              >
                Sign In to Continue
              </motion.button>
            </SignInButton>
          </div>
        </div>
      </motion.div>
    );
  }

  // User is signed in - render the protected content
  return <>{children}</>;
}

// Admin-only route wrapper that checks backend role
export function AdminRoute({ children }: { children: React.ReactNode }) {
  const { isSignedIn, isLoaded } = useAuth();
  const { userRole, loading } = useUserRole();

  // Loading state while checking both Clerk and backend
  if (!isLoaded || loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="flex flex-col items-center gap-4"
        >
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-[#dcff1a] border-t-transparent"></div>
          <p className="text-gray-300 text-lg">Verifying permissions...</p>
        </motion.div>
      </div>
    );
  }

  // Not signed in
  if (!isSignedIn) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-center min-h-[60vh]"
      >
        <div className="max-w-md w-full mx-auto p-8 bg-slate-900/50 backdrop-blur-lg rounded-2xl border border-white/10 shadow-xl">
          <div className="flex flex-col items-center gap-6 text-center">
            <div className="p-4 bg-red-500/10 rounded-full">
              <Shield className="w-12 h-12 text-red-400" />
            </div>
            
            <div>
              <h2 className="text-2xl font-bold text-white mb-2">
                Admin Access Required
              </h2>
              <p className="text-gray-400">
                You must be signed in as an administrator to access this page
              </p>
            </div>

            <SignInButton mode="modal">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="px-6 py-3 bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-slate-900 font-semibold rounded-lg shadow-lg hover:shadow-emerald-400/40 transition-all"
              >
                Sign In
              </motion.button>
            </SignInButton>
          </div>
        </div>
      </motion.div>
    );
  }

  // Signed in but not admin
  if (userRole !== "ADMIN") {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-center min-h-[60vh]"
      >
        <div className="max-w-md w-full mx-auto p-8 bg-slate-900/50 backdrop-blur-lg rounded-2xl border border-red-500/10 shadow-xl">
          <div className="flex flex-col items-center gap-6 text-center">
            <div className="p-4 bg-red-500/10 rounded-full">
              <Shield className="w-12 h-12 text-red-400" />
            </div>
            
            <div>
              <h2 className="text-2xl font-bold text-white mb-2">
                Access Denied
              </h2>
              <p className="text-gray-400">
                You don't have permission to access this page. Administrator privileges are required.
              </p>
            </div>

            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => window.history.back()}
              className="px-6 py-3 bg-gray-700 hover:bg-gray-600 text-white font-semibold rounded-lg transition-all"
            >
              Go Back
            </motion.button>
          </div>
        </div>
      </motion.div>
    );
  }

  // User is admin - render the protected content
  return <>{children}</>;
}
