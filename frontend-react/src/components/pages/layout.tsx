// src/components/Layout.tsx
import { ReactNode } from 'react'
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { AppSideBar } from "../pages/sidenavbar"

interface LayoutProps {
  children: ReactNode
}

export function Layout({ children }: LayoutProps) {
  return (
    <SidebarProvider>
        <AppSideBar />
        <main >
          <SidebarTrigger />
          {children}
        </main>
    </SidebarProvider>
  )
}