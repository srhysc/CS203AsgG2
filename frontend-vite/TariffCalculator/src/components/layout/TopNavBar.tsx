import { Home, Calculator, Search, Shield, Globe, Droplet, Factory, Ship, Map, Bookmark, Menu, X } from "lucide-react";
import { SignedIn, SignedOut, SignInButton, UserButton } from "@clerk/clerk-react";
import { Link, useLocation } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import { getUserRole } from "@/services/clerkauthentication";
import { useState } from "react";

interface NavItem {
  id: string;
  title: string;
  url: string;
  icon: React.FC<any>;
  requiresAdmin?: boolean;
}

const navItems: NavItem[] = [
  { id: "home", title: "Home", url: "/", icon: Home },
  { id: "calculator", title: "Calculator", url: "/calculator", icon: Calculator },
  { id: "lookup", title: "Lookup", url: "/lookup", icon: Search },
  { id: "country", title: "Countries", url: "/country", icon: Globe },
  { id: "petroleum", title: "Petroleum", url: "/petroleum", icon: Droplet },
  { id: "refineries", title: "Refineries", url: "/refineries", icon: Factory },
  { id: "shipping", title: "Shipping", url: "/shipping", icon: Ship },
  { id: "route", title: "Routes", url: "/route", icon: Map },
  { id: "bookmarks", title: "Bookmarks", url: "/bookmarks", icon: Bookmark },
  { id: "admin", title: "Admin", url: "/administrator", icon: Shield, requiresAdmin: true },
];

export function TopNavBar() {
  const location = useLocation();
  const { userRole, loading } = getUserRole();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-16 bg-slate-900/70 backdrop-blur-lg border-b border-white/10">
        <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-[#dcff1a]"></div>
      </div>
    );
  }

  const visibleItems = navItems.filter(item => !item.requiresAdmin || userRole === "ADMIN");

  return (
    <>
      <nav className="flex items-center justify-between w-full px-4 sm:px-6 py-3 bg-slate-900/70 backdrop-blur-lg border-b border-white/10 shadow-[0_4px_30px_rgba(0,0,0,0.2)] sticky top-0 z-50 transition-all">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2 text-xl sm:text-2xl font-bold select-none cursor-pointer group">
          <motion.span
            whileHover={{ scale: 1.05 }}
            className="bg-gradient-to-r from-[#dcff1a] to-emerald-400 bg-clip-text text-transparent"
          >
            Petro<span className="text-gray-300">Trade</span>
          </motion.span>
          <motion.div
            className="h-2 w-2 bg-[#dcff1a] rounded-full opacity-0 group-hover:opacity-100 transition-all"
            animate={{ y: [0, -2, 0] }}
            transition={{ repeat: Infinity, duration: 1.8 }}
          />
        </Link>

        {/* Desktop Navigation - Compact */}
        <div className="hidden xl:flex gap-1 relative overflow-x-auto">
          {visibleItems.map((item) => {
            const isActive = location.pathname === item.url;
            return (
              <motion.div
                key={item.id}
                whileHover={{ y: -1 }}
                whileTap={{ scale: 0.97 }}
                transition={{ type: "spring", stiffness: 400, damping: 20 }}
              >
                <Link
                  to={item.url}
                  className={`flex items-center gap-3 px-5 py-2.5 rounded-lg text-sm font-medium transition-all relative group ${
                    isActive
                      ? "bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-slate-900 shadow-md"
                      : "text-gray-300 hover:text-[#dcff1a] hover:bg-white/10"
                  }`}
                >
                  <item.icon size={18} className={`${isActive ? "text-slate-900" : "text-emerald-300 group-hover:text-[#dcff1a]"}`} />
                  <span className="whitespace-nowrap">{item.title}</span>

                  {isActive && (
                    <motion.div
                      layoutId="underline"
                      className="absolute bottom-0 left-0 right-0 h-[2px] bg-emerald-400/80 rounded-full"
                      transition={{ type: "spring", stiffness: 300, damping: 30 }}
                    />
                  )}
                </Link>
              </motion.div>
            );
          })}
        </div>

        {/* Right Side - Auth + Mobile Menu */}
        <div className="flex items-center gap-3">
          <SignedOut>
            <SignInButton mode="modal">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="px-4 py-2 bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-slate-900 font-semibold rounded-lg shadow hover:shadow-emerald-400/40 transition-all text-sm"
              >
                Sign In
              </motion.button>
            </SignInButton>
          </SignedOut>
          <SignedIn>
            <UserButton
              appearance={{
                elements: {
                  userButtonAvatarBox: "w-9 h-9",
                  userButtonRoot:
                    "bg-white/10 border border-white/20 hover:bg-white/20 text-white rounded-full p-1 transition-all shadow-sm",
                },
              }}
            />
          </SignedIn>

          {/* Mobile Menu Button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="xl:hidden p-2 text-gray-300 hover:text-[#dcff1a] hover:bg-white/10 rounded-lg transition-all"
          >
            {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </nav>

      {/* Mobile Menu Dropdown */}
      <AnimatePresence>
        {mobileMenuOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            className="xl:hidden bg-slate-900/95 backdrop-blur-lg border-b border-white/10 sticky top-[60px] z-40 overflow-hidden"
          >
            <div className="px-4 py-3 space-y-1 max-h-[calc(100vh-120px)] overflow-y-auto">
              {visibleItems.map((item) => {
                const isActive = location.pathname === item.url;
                return (
                  <Link
                    key={item.id}
                    to={item.url}
                    onClick={() => setMobileMenuOpen(false)}
                    className={`flex items-center gap-3 px-4 py-3 rounded-lg text-base font-medium transition-all ${
                      isActive
                        ? "bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-slate-900"
                        : "text-gray-300 hover:text-[#dcff1a] hover:bg-white/10"
                    }`}
                  >
                    <item.icon size={20} className={isActive ? "text-slate-900" : "text-emerald-300"} />
                    <span>{item.title}</span>
                  </Link>
                );
              })}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}