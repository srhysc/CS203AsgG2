// src/components/Layout.tsx
import type { ReactNode } from 'react'
import { ModeToggle } from "@/components/ui/mode-toggle";
import { SidebarProvider, SidebarTrigger,useSidebar } from "@/components/ui/sidebar"
import { AppSideBar } from "../pages/sidenavbar"

function LayoutContent({ children }: {children:ReactNode}) {
  const { open } = useSidebar()
  
  return (
    <>
      <AppSideBar />
      <main className="flex-1 relative">
        {/* Floating trigger - moves with sidebar state */}
        <SidebarTrigger 
          className={`fixed top-5 z-50 p-1 w-1/16 h-1/16 bg-white shadow-sm border rounded hover:bg-gray-150 transition-all duration-300 ${
            open ? 'left-[calc(16rem+0.5rem)]' : 'left-4'
          }`} 
        />
        <div className="p-2 h-full">
          {children}
        </div>
      </main>
      
      {/* Floating Light/Dark mode button */}
      <div className="fixed bottom-4 right-4 z-50">
        <ModeToggle />
      </div>
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