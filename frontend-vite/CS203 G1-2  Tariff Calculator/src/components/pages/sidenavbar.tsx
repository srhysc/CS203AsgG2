// src/components/Layout.tsx
import { Home, Calculator } from "lucide-react"
import { SignedIn, SignedOut, SignInButton, UserButton } from '@clerk/clerk-react';


import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarGroupContent
} from "@/components/ui/sidebar"

const items = [
  { 
    title: "Home",
    url: "#",
    icon: Home,
  },

  {
    title: "Tariff Calculator",
    url: "/calculator", 
    icon: Calculator,
  },

]

export function AppSideBar(){
    return(
      <Sidebar className="w-64 h-screen">
      <SidebarContent className="flex flex-col h-full p-4 bg-gray-900 text-white">

        {/* Header box */}
        <div className="p-4 rounded-md mb-4" style={{ backgroundColor: "#71869A" }}>
          <h1 className="text-lg font-semibold">Welcome to Tariff Calculator</h1>
        </div>

        {/* Menu items */}
        <SidebarMenu className="flex-1">
          {items.map((item) => (
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
          <SignedOut>
            <SignInButton className="w-full bg-gray-700 hover:bg-gray-600 text-white rounded-md p-2" />
          </SignedOut>
          <SignedIn>
            <UserButton
              appearance={{
                elements: {
                  userButtonAvatarBox: "w-10 h-10",
                  userButtonRoot: "w-full bg-gray-700 hover:bg-gray-600 text-white rounded-md p-2",
                },
              }}
            />
          </SignedIn>
        </SidebarFooter>

       </SidebarContent>
       </Sidebar>

    )
}