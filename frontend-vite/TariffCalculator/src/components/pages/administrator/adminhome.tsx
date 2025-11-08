import {motion} from "motion/react"
import { FileText, FileSignature, Ship, DollarSign, UserPlus } from "lucide-react";

const adminActions = [
  {
    title: "Edit Tariff Rates",
    icon: FileText,
    description: "Manage import/export tariffs",
    url: "/administrator/tariffs",
  },
  {
    title: "Edit VAT Rates",
    icon: FileSignature,
    description: "Update tax percentages",
    url: "/administrator/VAT-rates",
  },
  {
    title: "Edit Shipping Rates",
    icon: Ship,
    description: "Configure shipping costs",
    url: "/administrator/shipping-fees",
  },
  {
    title: "Edit Product Prices",
    icon: DollarSign,
    description: "Update product pricing",
    url: "/administrator/product-prices",
  },
  {
    title: "Manage User Roles",
    icon: UserPlus,
    description: "Grant admin privileges",
    url: "/administrator/manage-admins",
  },
];

export default function Adminhome() {
  return (
    <div className="min-h-screen flex flex-col px-4 py-4 md:py-8">
      {/* Hero Section */}
      <div className="text-center mb-6 px-4">
        <h1 className="relative z-10 mx-auto max-w-4xl text-center text-2xl md:text-4xl lg:text-7xl font-extrabold bg-clip-text bg-gradient-to-r from-[#dcff1a] to-emerald-400">
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
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.9, delay: 0.8 }}
          className="relative z-10 mx-auto max-w-xl py-4 text-center text-lg text-gray-300"
        >
          Manage platform settings, rates, and user permissions
        </motion.p>
      </div>


      {/* Admin Action Cards */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 1.0, delay: 0.5 }}
        className="mx-auto max-w-7xl grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-16"
      >
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
            <motion.a
              href={action.url}
              className="block h-full bg-white/10 backdrop-blur-md rounded-xl overflow-hidden shadow-lg hover:shadow-2xl transition-all border border-white/10 cursor-pointer p-6"
              whileHover={{ y: -5, backgroundColor: "rgba(255, 255, 255, 0.15)" }}
              whileTap={{ scale: 0.98 }}
            >
              {/* Icon */}
              <div className="mb-4 w-16 h-16 rounded-xl bg-gradient-to-br from-[#dcff1a] to-emerald-400 flex items-center justify-center shadow-lg">
                <action.icon className="w-8 h-8 text-slate-900" />
              </div>

              {/* Content */}
              <div>
                <h3 className="text-xl font-semibold mb-2 text-white">
                  {action.title}
                </h3>
                <p className="text-gray-300 text-sm leading-relaxed mb-4">
                  {action.description}
                </p>
                <motion.div
                  className="inline-flex items-center text-[#dcff1a] font-medium text-sm"
                  whileHover={{ x: 5 }}
                >
                  Manage
                  <svg
                    className="w-4 h-4 ml-2"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M9 5l7 7-7 7"
                    />
                  </svg>
                </motion.div>
              </div>
            </motion.a>
          </motion.div>
        ))}
      </motion.div>
    </div>
  );
}

