"use client";
import { useState } from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import type { LucideIcon } from "lucide-react";
import {
  Calculator,
  Search,
  Globe,
  Droplet,
  Factory,
  Ship,
  Map,
  Bookmark,
} from "lucide-react";

interface ToolCard {
  id: string;
  icon: LucideIcon;
  title: string;
  description: string;
  buttonText: string;
  url: string;
  featured?: boolean;
}

export default function Home() {
  const [activeTab, setActiveTab] = useState("home");
  const navigate = useNavigate();

  const toolCards: ToolCard[] = [
    {
      id: "calculator",
      icon: Calculator,
      title: "Tariff Calculator",
      description: "Calculate total landed costs including tariffs, VAT, and shipping fees.",
      buttonText: "Launch Calculator",
      url: "/calculator",
      featured: true,
    },
    {
      id: "bookmarks",
      icon: Bookmark,
      title: "Bookmarks",
      description: "Access your saved calculations and favorite routes for quick reference.",
      buttonText: "View Bookmarks",
      url: "/bookmarks",
      featured: true,
    },
    {
      id: "tariffs",
      icon: Search,
      title: "View Tariffs",
      description: "Explore historical and current tariff rates between countries.",
      buttonText: "Browse Tariffs",
      url: "/lookup",
    },
    {
      id: "country",
      icon: Globe,
      title: "Country Info",
      description: "Access detailed information about trading countries and their regulations.",
      buttonText: "View Countries",
      url: "/country",
    },
    {
      id: "petroleum",
      icon: Droplet,
      title: "Petroleum Details",
      description: "Get insights into different types of petroleum products and their specifications.",
      buttonText: "View Details",
      url: "/petroleum",
    },
    {
      id: "refineries",
      icon: Factory,
      title: "Refineries",
      description: "Explore global refineries and their processing capabilities.",
      buttonText: "View Refineries",
      url: "/refineries",
    },
    {
      id: "shipping",
      icon: Ship,
      title: "Shipping Cost",
      description: "Calculate shipping costs between different ports and routes.",
      buttonText: "Calculate Shipping",
      url: "/shipping",
    },
    {
      id: "convertable",
      icon: Map,
      title: "Petroleum Convertion",
      description: "See percentage yield of different Petroleum types.",
      buttonText: "View Conversions",
      url: "/convertable",
    },
  ];

  const featuredCards = toolCards.filter(card => card.featured);
  const regularCards = toolCards.filter(card => !card.featured);

  const handleCardClick = (card: ToolCard) => {
    setActiveTab(card.id);
    navigate(card.url);
  };

  return (
    <div className="min-h-[calc(100vh-120px)] flex flex-col px-4 sm:px-6 lg:px-8">
      {activeTab === "home" && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -20 }}
          transition={{ duration: 0.3 }}
          className="flex-1 flex flex-col"
        >
          {/* Hero Section */}
          <div className="text-center mb-12 mt-8">
            <h1 className="text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-extrabold mb-6 bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400 cursor-default">
              Petroleum Trade Analysis Platform
            </h1>
            <p className="text-lg sm:text-xl md:text-2xl text-gray-300 max-w-3xl mx-auto leading-relaxed cursor-default">
              Analyze petroleum import costs with historical data, shipping fees, and refinery insights.
            </p>
          </div>

          {/* Featured Cards - Larger and Highlighted */}
          <div className="mb-8">
            <h2 className="text-2xl font-bold text-gray-200 mb-4 flex items-center gap-2">
              <span className="w-1 h-6 bg-gradient-to-b from-[#dcff1a] to-emerald-400 rounded-full"></span>
              Quick Access
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {featuredCards.map((card, index) => (
                <motion.div
                  key={card.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="bg-gradient-to-br from-white/15 to-white/5 backdrop-blur-md rounded-xl overflow-hidden shadow-xl hover:shadow-2xl transition-all border border-[#dcff1a]/20 cursor-pointer flex flex-col items-center justify-center p-8 relative group"
                  whileHover={{ y: -8, scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleCardClick(card)}
                >
                  {/* Glow effect */}
                  <div className="absolute inset-0 bg-gradient-to-br from-[#dcff1a]/0 via-emerald-400/0 to-[#dcff1a]/0 group-hover:from-[#dcff1a]/10 group-hover:via-emerald-400/10 group-hover:to-[#dcff1a]/10 transition-all duration-500"></div>
                  
                  {/* Icon */}
                  <div className="relative">
                    <card.icon size={80} className="text-[#dcff1a] mb-6 drop-shadow-[0_0_15px_rgba(220,255,26,0.5)]" />
                  </div>

                  {/* Card Content */}
                  <h3 className="text-2xl font-bold mb-3 text-white text-center relative">{card.title}</h3>
                  <p className="text-gray-300 mb-6 text-center leading-relaxed relative">{card.description}</p>
                  <motion.button
                    className="w-full py-3 px-6 bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-slate-900 rounded-lg font-semibold shadow-lg relative"
                    whileHover={{ scale: 1.05, boxShadow: "0 15px 50px rgba(220, 255, 26, 0.4)" }}
                    whileTap={{ scale: 0.95 }}
                    onClick={(e) => {
                      e.stopPropagation();
                      handleCardClick(card);
                    }}
                  >
                    {card.buttonText}
                  </motion.button>
                </motion.div>
              ))}
            </div>
          </div>

          {/* Regular Tool Cards */}
          <div className="mb-8">
            <h2 className="text-2xl font-bold text-gray-200 mb-4 flex items-center gap-2">
              <span className="w-1 h-6 bg-gradient-to-b from-[#dcff1a] to-emerald-400 rounded-full"></span>
              All Tools
            </h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {regularCards.map((card, index) => (
                <motion.div
                  key={card.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: (featuredCards.length + index) * 0.08 }}
                  className="bg-white/10 backdrop-blur-md rounded-xl overflow-hidden shadow-lg hover:shadow-2xl transition-all border border-white/10 cursor-pointer flex flex-col items-center justify-center p-6"
                  whileHover={{ y: -5, backgroundColor: "rgba(255, 255, 255, 0.15)" }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleCardClick(card)}
                >
                  {/* Icon */}
                  <card.icon size={60} className="text-[#dcff1a] mb-4" />

                  {/* Card Content */}
                  <h3 className="text-xl font-semibold mb-2 text-white text-center">{card.title}</h3>
                  <p className="text-gray-300 mb-4 text-center text-sm leading-relaxed">{card.description}</p>
                  <motion.button
                    className="w-full py-2.5 px-4 bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-slate-900 rounded-lg font-medium shadow-lg"
                    whileHover={{ scale: 1.02, boxShadow: "0 10px 40px rgba(220, 255, 26, 0.3)" }}
                    whileTap={{ scale: 0.98 }}
                    onClick={(e) => {
                      e.stopPropagation();
                      handleCardClick(card);
                    }}
                  >
                    {card.buttonText}
                  </motion.button>
                </motion.div>
              ))}
            </div>
          </div>
        </motion.div>
      )}
    </div>
  );
}
