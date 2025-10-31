// src/components/Layout.tsx
import { Home, Calculator, Search, Shield } from "lucide-react"
import { SignedIn, SignedOut, SignInButton, UserButton } from '@clerk/clerk-react';

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar"

import { getUserRole } from "@/services/clerkauthentication";

const items = [
  { 
    title: "Home",
    url: "/",
    icon: Home,
  },

  {
    title: "Tariff Calculator",
    url: "/calculator", 
    icon: Calculator,
  },

  {
    title: "Tariff Lookup",
    url: "/lookup", 
    icon: Search,
  },

  {
    title: "Administrator",
    url: "/administrator",
    icon: Shield,
    requiresAdmin: true, // Add a flag for admin-only items
  },

]


export function AppSideBar(){
  
  //get user role based on backend user created by reading database
  const { userRole, loading } = getUserRole();

    if (loading) {
        return (
            <div className="flex items-center justify-center h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900"></div>
            </div>
        );
    }

  // Filter items based on admin status
  const visibleItems = items.filter(item => !item.requiresAdmin || userRole == "ADMIN")

  
    return(
      <Sidebar className="w-64 h-screen">
        <SidebarContent className="flex flex-col h-full p-4 bg-gray-200 dark:bg-gray-900 text-black dark:text-white">

        {/* Header box */}
        <div className="p-4 rounded-md mb-4 text-white dark:text-slate-900 dark:bg-[#9AA9BA]" style={{ backgroundColor: "#71869A" }}>
          <h1 className="text-lg font-semibold">Welcome to Tariff Calculator</h1>
        </div>

        {/* Menu items */}
        <SidebarMenu className="flex-1">
          {visibleItems.map((item) => (
            <SidebarMenuItem key={item.title}>
              <SidebarMenuButton asChild>
                <a href={item.url} className="flex items-center gap-2">
                  <item.icon />
                  <span>{item.title}</span>
                </a>
              </SidebarMenuButton>
            </SidebarMenuItem>
          ))}
        </SidebarMenu>

        {/* Footer box with login */}
        <SidebarFooter className="mt-auto flex flex-col gap-2">
          <div className="flex flex-row gap-2 items-center justify-center w-full">
          <SignedOut>
            <SignInButton />
          </SignedOut>
          <SignedIn>
            <UserButton
              appearance={{
                elements: {
                  userButtonAvatarBox: "w-10 h-10",
                  userButtonRoot: "flex-1 bg-gray-700 hover:bg-gray-600 text-white rounded-md p-2",
                },
              }}
            />
          </SignedIn>
          
        </div>
        </SidebarFooter>

       </SidebarContent>
       </Sidebar>

    )
}