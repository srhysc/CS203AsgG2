// // src/components/Layout.tsx
// import { Home, Calculator, Search, Shield } from "lucide-react"
// import { SignedIn, SignedOut, SignInButton, UserButton } from '@clerk/clerk-react';

// import {
//   Sidebar,
//   SidebarContent,
//   SidebarFooter,
//   SidebarMenu,
//   SidebarMenuButton,
//   SidebarMenuItem,
// } from "@/components/ui/sidebar"

// import { getUserRole } from "@/services/clerkauthentication";

// const items = [
//   { 
//     title: "Home",
//     url: "/",
//     icon: Home,
//   },

//   {
//     title: "Tariff Calculator",
//     url: "/calculator", 
//     icon: Calculator,
//   },

//   {
//     title: "Tariff Lookup",
//     url: "/lookup", 
//     icon: Search,
//   },

//   {
//     title: "Administrator",
//     url: "/administrator",
//     icon: Shield,
//     requiresAdmin: true, // Add a flag for admin-only items
//   },

// ]


// export function AppSideBar(){
  
//   //get user role based on backend user created by reading database
//   const { userRole, loading } = getUserRole();

//     if (loading) {
//         return (
//             <div className="flex items-center justify-center h-screen">
//                 <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900"></div>
//             </div>
//         );
//     }

//   // Filter items based on admin status
//   const visibleItems = items.filter(item => !item.requiresAdmin || userRole == "ADMIN")

  
//     return(
//       <Sidebar className="w-64 h-screen">
//         <SidebarContent className="flex flex-col h-full p-4 bg-gray-200 dark:bg-gray-900 text-black dark:text-white">

//         {/* Header box */}
//         <div className="p-4 rounded-md mb-4 text-white dark:text-slate-900 dark:bg-[#9AA9BA]" style={{ backgroundColor: "#71869A" }}>
//           <h1 className="text-lg font-semibold">Welcome to Tariff Calculator</h1>
//         </div>

//         {/* Menu items */}
//         <SidebarMenu className="flex-1">
//           {visibleItems.map((item) => (
//             <SidebarMenuItem key={item.title}>
//               <SidebarMenuButton asChild>
//                 <a href={item.url} className="flex items-center gap-2">
//                   <item.icon />
//                   <span>{item.title}</span>
//                 </a>
//               </SidebarMenuButton>
//             </SidebarMenuItem>
//           ))}
//         </SidebarMenu>

//         {/* Footer box with login */}
//         <SidebarFooter className="mt-auto flex flex-col gap-2">
//           <div className="flex flex-row gap-2 items-center justify-center w-full">
//           <SignedOut>
//             <SignInButton />
//           </SignedOut>
//           <SignedIn>
//             <UserButton
//               appearance={{
//                 elements: {
//                   userButtonAvatarBox: "w-10 h-10",
//                   userButtonRoot: "flex-1 bg-gray-700 hover:bg-gray-600 text-white rounded-md p-2",
//                 },
//               }}
//             />
//           </SignedIn>
          
//         </div>
//         </SidebarFooter>

//        </SidebarContent>
//        </Sidebar>

//     )
// }

import { Home, Calculator, Search, Shield, Globe, Droplet, Factory, Ship, Map } from "lucide-react";
import { SignedIn, SignedOut, SignInButton, UserButton } from '@clerk/clerk-react';
import { Link, useLocation } from "react-router-dom";
import { motion } from "framer-motion";
import { getUserRole } from "@/services/clerkauthentication";

interface NavItem {
  id: string;
  title: string;
  url: string;
  icon: React.FC<any>;
  requiresAdmin?: boolean;
}

const navItems: NavItem[] = [
  { id: "home", title: "Home", url: "/", icon: Home },
  { id: "calculator", title: "Tariff Calculator", url: "/calculator", icon: Calculator },
  { id: "lookup", title: "Tariff Lookup", url: "/lookup", icon: Search },
  { id: "country", title: "Country Info", url: "/country", icon: Globe },
  { id: "petroleum", title: "Petroleum Details", url: "/petroleum", icon: Droplet },
  { id: "refineries", title: "Refineries", url: "/refineries", icon: Factory },
  { id: "shipping", title: "Shipping Cost", url: "/shipping", icon: Ship },
  { id: "route", title: "Refinery Route", url: "/route", icon: Map },
  { id: "admin", title: "Administrator", url: "/administrator", icon: Shield, requiresAdmin: true },
];

export function TopNavBar() {
  const location = useLocation();
  const { userRole, loading } = getUserRole();

  if (loading) {
    return (
      <div className="flex items-center justify-center h-16">
        <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-[#dcff1a]"></div>
      </div>
    );
  }

  // Filter items based on admin status
  const visibleItems = navItems.filter(item => !item.requiresAdmin || userRole === "ADMIN");

  return (
    <nav className="flex items-center justify-between w-full px-6 py-4 bg-white/10 backdrop-blur-md shadow-xl rounded-b-2xl border-b border-white/10">
      {/* Logo */}
      <div className="flex items-center gap-2 text-2xl font-bold cursor-pointer select-none">
        <span className="bg-gradient-to-r from-[#dcff1a] to-emerald-400 bg-clip-text text-transparent">
          Petro Trade
        </span>
      </div>

      {/* Navigation Items */}
      <div className="hidden lg:flex gap-3 relative">
        {visibleItems.map((item) => (
          <motion.div
            key={item.id}
            whileHover={{ y: -2 }}
            whileTap={{ scale: 0.95 }}
            transition={{ type: "spring", stiffness: 400, damping: 25 }}
          >
            <Link
              to={item.url}
              className={`flex items-center gap-2 px-5 py-3 rounded-lg transition-all text-lg font-semibold ${
                location.pathname === item.url
                  ? "bg-[#dcff1a] text-slate-900 font-medium"
                  : "text-gray-300 hover:bg-white/10"
              }`}
            >
              <item.icon size={20} />
              <span>{item.title}</span>
            </Link>
          </motion.div>
        ))}
      </div>

      {/* User Authentication */}
      <div className="flex items-center gap-4">
        <SignedOut>
          <SignInButton mode="modal" />
        </SignedOut>
        <SignedIn>
          <UserButton
            appearance={{
              elements: {
                userButtonAvatarBox: "w-10 h-10",
                userButtonRoot: "bg-gray-700 hover:bg-gray-600 text-white rounded-md p-2",
              },
            }}
          />
        </SignedIn>
      </div>
    </nav>
  );
}