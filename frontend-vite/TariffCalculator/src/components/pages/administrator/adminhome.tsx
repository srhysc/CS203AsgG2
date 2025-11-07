import {motion} from "motion/react"
import { Card } from "../../ui/card";
import { FileText,FileSignature,Ship,Plus } from "lucide-react";


const adminActions = [
  {
    title: "Edit Tariffs",
    icon: FileText,
    description: "Manage and update tariff rates",
    url: "/administrator/tariffs",
  },
  {
    title: "Edit VAT Rates",
    icon: FileSignature,
    description: "Modify value-added tax rates",
    url: "/administrator/VATrates",
  },
  {
    title: "Edit Shipping Fees",
    icon: Ship,
    description: "Update shipping cost structures",
    url: "/administrator/shipping-fees",
  },
  {
    title: "Create New Shipping Cost",
    icon: Plus,
    description: "Add new shipping cost entry",
    url: "/administrator/create-shipping",
  },
];



export default function Adminhome(){
    return(
    <div className="px-4 py-10 md:py-20">
        <h1 className="relative z-10 mx-auto max-w-4xl text-center text-2xl font-bold text-slate-700 md:text-4xl lg:text-7xl dark:text-slate-300">
          {"Welcome, administrator."
            .split(" ")
            .map((word, index) => (
              <motion.span
                key={index}
                initial={{ opacity: 0, filter: "blur(4px)", y: 10 }}
                animate={{ opacity: 1, filter: "blur(0px)", y: 0 }}
                transition={{
                  duration: 0.7,
                  delay: index * 0.25,
                  ease: "easeInOut",
                }}
                className="mr-2 inline-block"
              >
                {word}
              </motion.span>
            ))}
        </h1>
        <motion.p
          initial={{
            opacity: 0,
          }}
          animate={{
            opacity: 1,
          }}
          transition={{
            duration: 0.9,
            delay: 0.8,
          }}
          className="relative z-10 mx-auto max-w-xl py-4 text-center text-lg font-normal text-neutral-600 dark:text-neutral-400"
        >
        View all your abilities!
        </motion.p>

        <div>
            <motion.div 
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 1.0, delay: 0.5 }}
                className="mx-auto mt-12 max-w-6xl grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6"
            >
        {/* motion to float in */}
            {adminActions.map((action, index) => (
            <motion.div
                key={action.title}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ 
                duration: 0.5, 
                delay: 1.0 + (index * 0.1),
                ease: "easeOut" 
                }}
            >
            <Card className="group h-full p-6 hover:shadow-lg transition-all duration-300 cursor-pointer hover:scale-105 bg-white dark:bg-gray-800">
              <a href={action.url} className="flex flex-col items-center text-center h-full">
                <div className="mb-4 p-4 rounded-full bg-slate-100 dark:bg-slate-700 group-hover:bg-slate-200 dark:group-hover:bg-slate-600 transition-colors">
                  <action.icon className="w-8 h-8 text-slate-700 dark:text-slate-300" />
                </div>
                <h3 className="text-lg font-semibold text-slate-800 dark:text-slate-200 mb-2">
                  {action.title}
                </h3>
                <p className="text-sm text-slate-600 dark:text-slate-400">
                  {action.description}
                </p>
              </a>
            </Card>
          </motion.div>
        ))}
      </motion.div>

        </div>
    </div>
  
    )
}