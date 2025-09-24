// src/components/Layout.tsx
import type { ReactNode } from 'react'
import { SidebarProvider, SidebarTrigger,useSidebar } from "@/components/ui/sidebar"
import { AppSideBar } from "../pages/sidenavbar"

interface LayoutProps {
  children: ReactNode
}

function LayoutContent({ children }: {children:ReactNode}) {
  const { open } = useSidebar()
  
  return (
    <>
      <AppSideBar />
      <main className="flex-1 relative">
        {/* Floating trigger - moves with sidebar state */}
        <SidebarTrigger 
          className={`absolute top-2 z-10 p-1 w-6 h-6 bg-white shadow-sm border rounded hover:bg-gray-50 transition-all duration-300 ${
            open ? 'left-[17rem]' : 'left-2'
          }`} 
        />
        <div className="p-2 h-full">
          {children}
        </div>
      </main>
    </>
  )
}

export function Layout({ children }: {children:ReactNode}) {
  return (
    <SidebarProvider>
      <LayoutContent>{children}</LayoutContent>
    </SidebarProvider>
  )
}