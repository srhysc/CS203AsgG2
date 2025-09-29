import { Moon, Sun } from "lucide-react"

import { Button } from "./button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "./dropdown-menu"
import { useTheme } from "./theme-provider"

export function ModeToggle() {
  const { setTheme } = useTheme()

  const baseClasses = "bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm transition-colors";
  const hoverClasses = "hover:bg-gray-100 dark:hover:bg-gray-700";

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="icon" className={`${baseClasses} ${hoverClasses}`}>
          <Sun className="h-[1.2rem] w-[1.2rem] scale-100 rotate-0 transition-all dark:scale-0 dark:-rotate-90" />
          <Moon className="absolute h-[1.2rem] w-[1.2rem] scale-0 rotate-90 transition-all text-gray-800 dark:text-gray-200 dark:scale-100 dark:rotate-0" />
          <span className="sr-only">Toggle theme</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className={`${baseClasses} p-1 min-w-[8rem]`}>
        <DropdownMenuItem className={`px-2 py-1.5 rounded-sm ${hoverClasses} transition-colors`} onClick={() => {setTheme("light");}}>
          Light
        </DropdownMenuItem>
        <DropdownMenuItem className={`px-2 py-1.5 rounded-sm ${hoverClasses} transition-colors`} onClick={() => setTheme("dark")}>
          Dark
        </DropdownMenuItem>
        <DropdownMenuItem className={`px-2 py-1.5 rounded-sm ${hoverClasses} transition-colors`} onClick={() => setTheme("system")}>
          System
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}