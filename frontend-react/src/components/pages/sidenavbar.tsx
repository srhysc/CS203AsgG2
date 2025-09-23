// src/components/Layout.tsx

import { Home, Search } from "lucide-react"

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarHeader,
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
    title: "Tariff Search",
    url: "#",
    icon: Search,
  },

]

export function AppSideBar(){
    return(
      <Sidebar className="w-64 h-screen border-r bg-gray-900 text-white">
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Welcome to Tariff Calculator</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {items.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild>
                    <a href={item.url}>
                      <item.icon />
                      <span>{item.title}</span>
                    </a>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
    )
}