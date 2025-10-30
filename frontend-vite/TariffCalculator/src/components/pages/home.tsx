// import {motion} from "motion/react"
// import { WorldMap } from '@/components/ui/world-map';


// export default function Home(){
//     return(
//     <div className="px-4 py-10 md:py-20">
//         <h1 className="relative z-10 mx-auto max-w-4xl text-center text-2xl font-bold text-slate-700 md:text-4xl lg:text-7xl dark:text-slate-300">
//           {"Petroleum Tariffs made simple, to fuel you."
//             .split(" ")
//             .map((word, index) => (
//               <motion.span
//                 key={index}
//                 initial={{ opacity: 0, filter: "blur(4px)", y: 10 }}
//                 animate={{ opacity: 1, filter: "blur(0px)", y: 0 }}
//                 transition={{
//                   duration: 0.7,
//                   delay: index * 0.25,
//                   ease: "easeInOut",
//                 }}
//                 className="mr-2 inline-block"
//               >
//                 {word}
//               </motion.span>
//             ))}
//         </h1>
//         <motion.p
//           initial={{
//             opacity: 0,
//           }}
//           animate={{
//             opacity: 1,
//           }}
//           transition={{
//             duration: 0.9,
//             delay: 2.3,
//           }}
//           className="relative z-10 mx-auto max-w-xl py-4 text-center text-lg font-normal text-neutral-600 dark:text-neutral-400"
//         >
//         Look up and calculate any petroleum tariffs with us.
//         </motion.p>

//         <div className="max-w-7xl mx-auto text-center dark:bg-black bg-white">
//             <WorldMap
//               dots={[
//                 {
//                   start: {
//                     lat: 64.2008,
//                     lng: -149.4937,
//                   }, // Alaska (Fairbanks)
//                   end: {
//                     lat: 34.0522,
//                     lng: -118.2437,
//                   }, // Los Angeles
//                 },
//                 {
//                   start: { lat: 64.2008, lng: -149.4937 }, // Alaska (Fairbanks)
//                   end: { lat: -15.7975, lng: -47.8919 }, // Brazil (Brasília)
//                 },
//                 {
//                   start: { lat: -15.7975, lng: -47.8919 }, // Brazil (Brasília)
//                   end: { lat: 38.7223, lng: -9.1393 }, // Lisbon
//                 },
//                 {
//                   start: { lat: 51.5074, lng: -0.1278 }, // London
//                   end: { lat: 28.6139, lng: 77.209 }, // New Delhi
//                 },
//                 {
//                   start: { lat: 28.6139, lng: 77.209 }, // New Delhi
//                   end: { lat: 43.1332, lng: 131.9113 }, // Vladivostok
//                 },
//                 {
//                   start: { lat: 28.6139, lng: 77.209 }, // New Delhi
//                   end: { lat: -1.2921, lng: 36.8219 }, // Nairobi
//                 },
//               ]}
//             />
//         </div>
//     </div>

//     )import { useState } from "react";
"use client";
import React, { useState } from "react";
import { motion } from "framer-motion";
import { Layout } from "./Layout";
import { useNavigate } from "react-router-dom"; // <--- import
import CalculatorImg from "../images/Calculator.png";
import ViewTariffImg from "../images/viewTariff.png";
import CountryInfoImg from "../images/CountryInfo.png";
import PetroleumImg from "../images/petroleumInfo.png";
import RefineriesImg from "../images/refineries.png";
import ShippingImg from "../images/ships.png";
import RouteImg from "../images/routeOptimisation.png";

interface ToolCard {
  id: string;
  imageSrc: string;
  title: string;
  description: string;
  buttonText: string;
  url: string; // add the URL mapping
}

export default function Home() {
  const [activeTab, setActiveTab] = useState("home");
  const navigate = useNavigate(); // <--- hook

  const toolCards: ToolCard[] = [
    { id: "calculator", imageSrc: CalculatorImg, title: "Tariff Calculator", description: "Calculate total landed costs including tariffs, VAT, and shipping fees.", buttonText: "Launch Calculator", url: "/calculator" },
    { id: "tariffs", imageSrc: ViewTariffImg, title: "View Tariffs", description: "Explore historical and current tariff rates between countries.", buttonText: "Browse Tariffs", url: "/lookup" },
    { id: "country", imageSrc: CountryInfoImg, title: "Country Info", description: "Access detailed information about trading countries and their regulations.", buttonText: "View Countries", url: "/country" },
    { id: "petroleum", imageSrc: PetroleumImg, title: "Petroleum Details", description: "Get insights into different types of petroleum products and their specifications.", buttonText: "View Details", url: "/petroleum" },
    { id: "refineries", imageSrc: RefineriesImg, title: "Refineries", description: "Explore global refineries and their processing capabilities.", buttonText: "View Refineries", url: "/refineries" },
    { id: "shipping", imageSrc: ShippingImg, title: "Shipping Cost", description: "Calculate shipping costs between different ports and routes.", buttonText: "Calculate Shipping", url: "/shipping" },
    { id: "route", imageSrc: RouteImg, title: "Refinery Route", description: "Plan and optimize refinery routes for efficient transportation.", buttonText: "Plan Route", url: "/route" },
  ];

  const handleCardClick = (card: ToolCard) => {
    setActiveTab(card.id);
    navigate(card.url); // navigate to URL
  };

  return (
    <Layout activeTab={activeTab} setActiveTab={setActiveTab}>
      <div className="min-h-[calc(100vh-120px)] flex flex-col">
        {activeTab === "home" && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.3 }}
            className="flex-1 flex flex-col"
          >
            {/* Hero Section */}
            <div className="text-center mb-12">
              <h1 className="text-6xl md:text-7xl lg:text-8xl font-extrabold mb-6 bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400 cursor-default">
                Petroleum Trade Analysis Platform
              </h1>
              <p className="text-xl md:text-2xl text-gray-300 max-w-3xl mx-auto leading-relaxed cursor-default">
                Analyze petroleum import costs with historical data, shipping fees, and refinery insights.
              </p>
            </div>

            {/* Tool Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 mb-8">
              {toolCards.map((card, index) => (
                <motion.div
                  key={card.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="bg-white/10 backdrop-blur-md rounded-xl overflow-hidden shadow-lg hover:shadow-2xl transition-all border border-white/10 cursor-pointer flex flex-col group"
                  whileHover={{ y: -5, backgroundColor: "rgba(255, 255, 255, 0.15)" }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleCardClick(card)}
                >
                  {/* Image Container */}
                  <div className="relative w-full h-48 bg-gradient-to-br from-slate-800 to-slate-900 overflow-hidden">
                    <img
                      src={card.imageSrc}
                      alt={card.title}
                      className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent pointer-events-none" />
                  </div>

                  {/* Card Content */}
                  <div className="p-6 flex flex-col flex-1">
                    <h3 className="text-xl font-semibold mb-2 text-white text-center">{card.title}</h3>
                    <p className="text-gray-300 mb-4 text-center text-sm leading-relaxed flex-grow">{card.description}</p>
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
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}
      </div>
    </Layout>
  );
}
