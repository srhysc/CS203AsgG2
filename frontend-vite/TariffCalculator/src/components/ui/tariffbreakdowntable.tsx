// import { useState } from "react";
// import type {Tariff} from '@/services/types/countrytariff'

// export default function TariffBreakdownTable({ tariffObject }: { tariffObject: Tariff }) {
//   const [activeView, setActiveView] = useState<'breakdown' | 'routes' | null>(null);

//   // Build routes array
//   const routes = [];
  
//   // Direct route
//   routes.push({
//     transitCountry: 'Direct',
//     baseCost: tariffObject.basePrice,
//     tariffFees: tariffObject.tariffFees,
//     vatRate: tariffObject.vatRate,
//     vatFees: tariffObject.vatFees,
//     totalLandedCost: tariffObject.totalLandedCost,
//     route: `${tariffObject.exportingCountry} → ${tariffObject.importingCountry}`,
//     isDirect: true,
//   });

//   // Alternative routes
//   Object.entries(tariffObject.alternativeRoutes || {}).forEach(([transitCountry, breakdown]) => {
//     routes.push({
//       ...breakdown,
//       route: `${tariffObject.exportingCountry} → ${transitCountry} → ${tariffObject.importingCountry}`,
//       isDirect: false,
//     });
//   });

//   const formatCurrency = (value: number) => {
//     return new Intl.NumberFormat('en-US', {
//       style: 'currency',
//       currency: tariffObject.currency,
//       minimumFractionDigits: 2,
//       maximumFractionDigits: 2,
//     }).format(value);
//   };

//   const formatPercentage = (value: number) => {
//     return `${(value * 100).toFixed(2)}%`;
//   };

//   return (
//     <div className="p-6 max-w-7xl mx-auto">
//       {/* Button Group */}
//       <div className="flex gap-4 mb-6">
//         <button
//           onClick={() => setActiveView(activeView === 'breakdown' ? null : 'breakdown')}
//           className={`rounded-xl px-6 py-2 font-medium transition-all ${
//             activeView === 'breakdown'
//               ? 'bg-blue-600 text-white shadow-lg'
//               : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
//           }`}
//         >
//           {activeView === 'breakdown' ? 'Hide' : 'Show'} Tariff Breakdown
//         </button>
        
//         <button
//           onClick={() => setActiveView(activeView === 'routes' ? null : 'routes')}
//           className={`rounded-xl px-6 py-2 font-medium transition-all ${
//             activeView === 'routes'
//               ? 'bg-green-600 text-white shadow-lg'
//               : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
//           }`}
//         >
//           {activeView === 'routes' ? 'Hide' : 'Show'} Optimized Routes
//         </button>
//       </div>

//       {/* Tables Container */}
//       <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
//         {/* Tariff Breakdown Table */}
//         <div
//           className={`transition-all duration-300 ${
//             activeView === 'breakdown' 
//               ? 'opacity-100 max-h-[2000px]' 
//               : 'opacity-0 max-h-0 overflow-hidden'
//           }`}
//         >
//           <div className="bg-white rounded-lg shadow-md overflow-hidden">
//             <div className="bg-blue-600 text-white px-4 py-3">
//               <h3 className="text-lg font-semibold">Tariff Breakdown</h3>
//             </div>
//             <table className="w-full">
//               <tbody>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">Importing Country</td>
//                   <td className="p-3">{tariffObject.importingCountry}</td>
//                 </tr>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">Exporting Country</td>
//                   <td className="p-3">{tariffObject.exportingCountry}</td>
//                 </tr>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">Petroleum Type</td>
//                   <td className="p-3">{tariffObject.petroleumName}</td>
//                 </tr>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">Petroleum Code</td>
//                   <td className="p-3">{tariffObject.hsCode}</td>
//                 </tr>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">Price per Unit</td>
//                   <td className="p-3">{formatCurrency(tariffObject.pricePerUnit)}</td>
//                 </tr>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">Base Price</td>
//                   <td className="p-3">{formatCurrency(tariffObject.basePrice)}</td>
//                 </tr>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">Tariff Rate</td>
//                   <td className="p-3">{formatPercentage(tariffObject.tariffRate)}</td>
//                 </tr>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">Tariff Fees</td>
//                   <td className="p-3">{formatCurrency(tariffObject.tariffFees)}</td>
//                 </tr>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">VAT Rate</td>
//                   <td className="p-3">{formatPercentage(tariffObject.vatRate)}</td>
//                 </tr>
//                 <tr className="border-b">
//                   <td className="p-3 font-semibold bg-gray-50">VAT Fees</td>
//                   <td className="p-3">{formatCurrency(tariffObject.vatFees)}</td>
//                 </tr>
//                 <tr className="bg-blue-50">
//                   <td className="p-3 font-bold text-blue-900">Total Landed Cost</td>
//                   <td className="p-3 font-bold text-blue-900">{formatCurrency(tariffObject.totalLandedCost)}</td>
//                 </tr>
//               </tbody>
//             </table>
//           </div>
//         </div>

//         {/* Optimized Routes Table */}
//         <div
//           className={`transition-all duration-300 ${
//             activeView === 'routes' 
//               ? 'opacity-100 max-h-[2000px]' 
//               : 'opacity-0 max-h-0 overflow-hidden'
//           }`}
//         >
//           <div className="bg-white rounded-lg shadow-md overflow-hidden">
//             <div className="bg-green-600 text-white px-4 py-3">
//               <h3 className="text-lg font-semibold">Optimized Routes</h3>
//               <p className="text-sm text-green-100">
//                 {tariffObject.petroleumName} ({tariffObject.hsCode})
//               </p>
//             </div>
            
//             <div className="overflow-x-auto">
//               <table className="w-full text-sm">
//                 <thead className="bg-gray-100 border-b-2 border-gray-300">
//                   <tr>
//                     <th className="p-3 text-left font-semibold">Route</th>
//                     <th className="p-3 text-right font-semibold">Base Cost</th>
//                     <th className="p-3 text-right font-semibold">Tariff</th>
//                     <th className="p-3 text-right font-semibold">VAT</th>
//                     <th className="p-3 text-right font-semibold">Total</th>
//                   </tr>
//                 </thead>
//                 <tbody>
//                   {routes.map((route, index) => (
//                     <tr 
//                       key={index} 
//                       className={`border-b ${route.isDirect ? 'bg-green-50' : 'hover:bg-gray-50'}`}
//                     >
//                       <td className="p-3">
//                         <div className="flex flex-col gap-1">
//                           <span className="font-medium text-xs text-gray-600">{route.route}</span>
//                           {route.isDirect && (
//                             <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-green-600 text-white w-fit">
//                               ⭐ Optimal
//                             </span>
//                           )}
//                         </div>
//                       </td>
//                       <td className="p-3 text-right">{formatCurrency(route.baseCost)}</td>
//                       <td className="p-3 text-right">{formatCurrency(route.tariffFees)}</td>
//                       <td className="p-3 text-right">{formatCurrency(route.vatFees)}</td>
//                       <td className="p-3 text-right font-bold">{formatCurrency(route.totalLandedCost)}</td>
//                     </tr>
//                   ))}
//                 </tbody>
//               </table>
//             </div>

//             {routes.length > 1 && (
//               <div className="bg-gray-50 px-4 py-3 text-sm text-gray-600 border-t">
//                 <span className="font-semibold">Savings with optimal route:</span>{' '}
//                 {formatCurrency(routes[1].totalLandedCost - routes[0].totalLandedCost)}
//               </div>
//             )}
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// }

// import { useState } from "react";
// import type { Tariff } from "@/services/types/countrytariff";

// export default function TariffBreakdownTable({ tariffObject }: { tariffObject: Tariff }) {
//   const [activeView, setActiveView] = useState<"breakdown" | "routes" | null>(null);

//   const routes = [];

//   // Direct route
//   routes.push({
//     transitCountry: "Direct",
//     baseCost: tariffObject.basePrice,
//     tariffFees: tariffObject.tariffFees,
//     vatRate: tariffObject.vatRate,
//     vatFees: tariffObject.vatFees,
//     totalLandedCost: tariffObject.totalLandedCost,
//     route: `${tariffObject.exportingCountry} → ${tariffObject.importingCountry}`,
//     isDirect: true,
//   });

//   // Alternative routes
//   Object.entries(tariffObject.alternativeRoutes || {}).forEach(([transitCountry, breakdown]) => {
//     routes.push({
//       ...breakdown,
//       route: `${tariffObject.exportingCountry} → ${transitCountry} → ${tariffObject.importingCountry}`,
//       isDirect: false,
//     });
//   });

//   const formatCurrency = (value: number) =>
//     new Intl.NumberFormat("en-US", {
//       style: "currency",
//       currency: tariffObject.currency,
//       minimumFractionDigits: 2,
//       maximumFractionDigits: 2,
//     }).format(value);

//   const formatPercentage = (value: number) => `${(value * 100).toFixed(2)}%`;

//   return (
//     <div className="p-6 max-w-7xl mx-auto">
//       {/* Button Group */}
//       <div className="flex gap-4 mb-6 justify-center">
//         <button
//           onClick={() => setActiveView(activeView === "breakdown" ? null : "breakdown")}
//           className={`rounded-xl px-6 py-2 font-medium transition-all border border-white/20 ${
//             activeView === "breakdown"
//               ? "bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black shadow-lg"
//               : "bg-slate-800/60 text-gray-200 hover:bg-slate-700"
//           }`}
//         >
//           {activeView === "breakdown" ? "Hide" : "Show"} Tariff Breakdown
//         </button>

//         <button
//           onClick={() => setActiveView(activeView === "routes" ? null : "routes")}
//           className={`rounded-xl px-6 py-2 font-medium transition-all border border-white/20 ${
//             activeView === "routes"
//               ? "bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black shadow-lg"
//               : "bg-slate-800/60 text-gray-200 hover:bg-slate-700"
//           }`}
//         >
//           {activeView === "routes" ? "Hide" : "Show"} Optimized Routes
//         </button>
//       </div>

//       {/* Tables Container */}
//       <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
//         {/* Tariff Breakdown Table */}
//         <div
//           className={`transition-all duration-300 ${
//             activeView === "breakdown"
//               ? "opacity-100 max-h-[2000px]"
//               : "opacity-0 max-h-0 overflow-hidden"
//           }`}
//         >
//           <div className="bg-slate-900/80 border border-white/10 rounded-xl shadow-lg overflow-hidden">
//             <div className="bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black px-4 py-3">
//               <h3 className="text-lg font-semibold">Tariff Breakdown</h3>
//             </div>
//             <table className="w-full text-sm text-gray-200">
//               <tbody>
//                 {[
//                   ["Importing Country", tariffObject.importingCountry],
//                   ["Exporting Country", tariffObject.exportingCountry],
//                   ["Petroleum Type", tariffObject.petroleumName],
//                   ["Petroleum Code", tariffObject.hsCode],
//                   ["Price per Unit", formatCurrency(tariffObject.pricePerUnit)],
//                   ["Base Price", formatCurrency(tariffObject.basePrice)],
//                   ["Tariff Rate", formatPercentage(tariffObject.tariffRate)],
//                   ["Tariff Fees", formatCurrency(tariffObject.tariffFees)],
//                   ["VAT Rate", formatPercentage(tariffObject.vatRate)],
//                   ["VAT Fees", formatCurrency(tariffObject.vatFees)],
//                 ].map(([label, value], index) => (
//                   <tr key={index} className="border-b border-white/10">
//                     <td className="p-3 font-medium text-gray-400">{label}</td>
//                     <td className="p-3 text-right text-gray-100">{value}</td>
//                   </tr>
//                 ))}
//                 <tr className="bg-slate-800/80">
//                   <td className="p-3 font-bold text-[#dcff1a]">Total Landed Cost</td>
//                   <td className="p-3 font-bold text-right text-[#dcff1a]">
//                     {formatCurrency(tariffObject.totalLandedCost)}
//                   </td>
//                 </tr>
//               </tbody>
//             </table>
//           </div>
//         </div>

//         {/* Optimized Routes Table */}
//         <div
//           className={`transition-all duration-300 ${
//             activeView === "routes"
//               ? "opacity-100 max-h-[2000px]"
//               : "opacity-0 max-h-0 overflow-hidden"
//           }`}
//         >
//           <div className="bg-slate-900/80 border border-white/10 rounded-xl shadow-lg overflow-hidden">
//             <div className="bg-gradient-to-r from-emerald-400 to-[#dcff1a] text-black px-4 py-3">
//               <h3 className="text-lg font-semibold">Optimized Routes</h3>
//               <p className="text-sm text-black/80">
//                 {tariffObject.petroleumName} ({tariffObject.hsCode})
//               </p>
//             </div>

//             <div className="overflow-x-auto">
//               <table className="w-full text-sm text-gray-200">
//                 <thead className="bg-slate-800/60 text-gray-300 border-b border-white/10">
//                   <tr>
//                     <th className="p-3 text-left font-semibold">Route</th>
//                     <th className="p-3 text-right font-semibold">Base Cost</th>
//                     <th className="p-3 text-right font-semibold">Tariff</th>
//                     <th className="p-3 text-right font-semibold">VAT</th>
//                     <th className="p-3 text-right font-semibold">Total</th>
//                   </tr>
//                 </thead>
//                 <tbody>
//                   {routes.map((route, index) => (
//                     <tr
//                       key={index}
//                       className={`border-b border-white/10 ${
//                         route.isDirect
//                           ? "bg-emerald-500/10"
//                           : "hover:bg-slate-800/50 transition-colors"
//                       }`}
//                     >
//                       <td className="p-3">
//                         <div className="flex flex-col gap-1">
//                           <span className="font-medium text-xs text-gray-300">{route.route}</span>
//                           {route.isDirect && (
//                             <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black w-fit">
//                               ⭐ Optimal
//                             </span>
//                           )}
//                         </div>
//                       </td>
//                       <td className="p-3 text-right">{formatCurrency(route.baseCost)}</td>
//                       <td className="p-3 text-right">{formatCurrency(route.tariffFees)}</td>
//                       <td className="p-3 text-right">{formatCurrency(route.vatFees)}</td>
//                       <td className="p-3 text-right font-bold text-[#dcff1a]">
//                         {formatCurrency(route.totalLandedCost)}
//                       </td>
//                     </tr>
//                   ))}
//                 </tbody>
//               </table>
//             </div>

//             {routes.length > 1 && (
//               <div className="bg-slate-800/60 px-4 py-3 text-sm text-gray-300 border-t border-white/10">
//                 <span className="font-semibold text-[#dcff1a]">Savings with optimal route:</span>{" "}
//                 {formatCurrency(routes[1].totalLandedCost - routes[0].totalLandedCost)}
//               </div>
//             )}
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// }

// export default function ToggleTable({ tariffObject }: { tariffObject: Tariff }) {
//   const [showTable, setShowTable] = useState(false);

//   return (
//     <div className="p-6">
//       <button
//         onClick={() => setShowTable((prev) => !prev)}
//         className="mb-4 rounded-xl bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 transition"
//       >
//         {showTable ? "Hide Table" : "Show Table"}
//       </button>

//       {/* Animate in/out with Tailwind */}
//       <div
//         className={`transition-all duration-300 ${
//           showTable ? "opacity-100 max-h-screen" : "opacity-0 max-h-0 overflow-hidden"
//         }`}
//       >
//         <table className="w-full border border-gray-300">
//           <thead className="bg-gray-200">
            
//           </thead>
//           <tbody>
//             <tr>
//               <th className="border p-2">Importing Country:</th>
//               <th className="border p-2">{tariffObject.importingCountry}</th>
//             </tr>
//             <tr>
//               <th className="border p-2">Exporting Country:</th>
//               <th className="border p-2">{tariffObject.exportingCountry}</th>
//             </tr>
//             <tr>
//               <td className="border p-2">Petroleum Type:</td>
//               <td className="border p-2">{tariffObject.petroleumName}</td>
//             </tr>
//             <tr>
//               <td className="border p-2">Petroleum Code: </td>
//               <td className="border p-2">{tariffObject.hsCode}</td>
//             </tr>
//             <tr>
//               <td className="border p-2">Price per unit: </td>
//               <td className="border p-2">{tariffObject.pricePerUnit}</td>
//             </tr>
//             <tr>
//               <td className="border p-2">Base price: </td>
//               <td className="border p-2">{tariffObject.basePrice}</td>
//             </tr>
//             <tr>
//               <td className="border p-2">Tariff Rate: </td>
//               <td className="border p-2">{tariffObject.tariffRate}</td>
//             </tr>
//             <tr>
//               <td className="border p-2">Tariff Fees: </td>
//               <td className="border p-2">{tariffObject.tariffFees}</td>
//             </tr>
//             <tr>
//               <td className="border p-2">Vat Rate: </td>
//               <td className="border p-2">{tariffObject.vatRate}</td>
//             </tr>
//             <tr>
//               <td className="border p-2">Vat Fees: </td>
//               <td className="border p-2">{tariffObject.vatFees}</td>
//             </tr>
//             <tr>
//               <td className="border p-2">Total Cost: </td>
//               <td className="border p-2">{tariffObject.totalLandedCost}</td>
//             </tr>
//           </tbody>
//         </table>
//       </div>
//     </div>
//   );
// } 

// import { useState } from "react";
// import type { Tariff } from "@/services/types/countrytariff";

// export default function TariffBreakdownTable({ tariffObject }: { tariffObject: Tariff }) {
//   const [showTables, setShowTables] = useState(false);

//   const routes = [];

//   // Direct route
//   routes.push({
//     transitCountry: "Direct",
//     baseCost: tariffObject.basePrice,
//     tariffFees: tariffObject.tariffFees,
//     vatRate: tariffObject.vatRate,
//     vatFees: tariffObject.vatFees,
//     totalLandedCost: tariffObject.totalLandedCost,
//     shippingCost: tariffObject.shippingCost,
//     route: `${tariffObject.exportingCountry} → ${tariffObject.importingCountry}`,
//     isDirect: true,
//   });

//   // Alternative routes
//   Object.entries(tariffObject.alternativeRoutes || {}).forEach(([transitCountry, breakdown]) => {
//     routes.push({
//       ...breakdown,
//       route: `${tariffObject.exportingCountry} → ${transitCountry} → ${tariffObject.importingCountry}`,
//       isDirect: false,
//     });
//   });

//   const formatCurrency = (value: number) =>
//     new Intl.NumberFormat("en-US", {
//       style: "currency",
//       currency: tariffObject.currency,
//       minimumFractionDigits: 2,
//       maximumFractionDigits: 2,
//     }).format(value);

//   return (
//     <div className="p-6 max-w-7xl mx-auto">
//       {/* Single Toggle Button */}
//       <div className="flex justify-center mb-6">
//         <button
//           onClick={() => setShowTables(!showTables)}
//           className={`rounded-xl px-6 py-3 font-medium transition-all border border-white/20 ${
//             showTables
//               ? "bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black shadow-lg"
//               : "bg-slate-800/60 text-gray-200 hover:bg-slate-700"
//           }`}
//         >
//           {showTables ? "Hide" : "Display"} Tariff Breakdown & Optimized Routes
//         </button>
//       </div>

//       {/* Side-by-Side Tables Container */}
//       <div
//         className={`transition-all duration-300 ${
//           showTables
//             ? "opacity-100 max-h-[2000px]"
//             : "opacity-0 max-h-0 overflow-hidden"
//         }`}
//       >
//         <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
//           {/* Tariff Breakdown Table */}
//           <div className="bg-slate-900/80 border border-white/10 rounded-xl shadow-lg overflow-hidden">
//             <div className="bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black px-4 py-3">
//               <h3 className="text-lg font-semibold">Tariff Breakdown</h3>
//             </div>
//             <table className="w-full text-sm text-gray-200">
//               <tbody>
//                 {[
//                   ["Importing Country", tariffObject.importingCountry],
//                   ["Exporting Country", tariffObject.exportingCountry],
//                   ["Petroleum Type", tariffObject.petroleumName],
//                   ["Petroleum Code", tariffObject.hsCode],
//                   ["Price per Unit", formatCurrency(tariffObject.pricePerUnit)],
//                   ["Base Price", formatCurrency(tariffObject.basePrice)],
//                   ["Tariff Rate", tariffObject.tariffRate],
//                   ["Tariff Fees", formatCurrency(tariffObject.tariffFees)],
//                   ["VAT Rate", tariffObject.vatRate],
//                   ["VAT Fees", formatCurrency(tariffObject.vatFees)],
//                 ].map(([label, value], index) => (
//                   <tr key={index} className="border-b border-white/10">
//                     <td className="p-3 font-medium text-gray-400">{label}</td>
//                     <td className="p-3 text-right text-gray-100">{value}</td>
//                   </tr>
//                 ))}
//                 <tr className="bg-slate-800/80">
//                   <td className="p-3 font-bold text-[#dcff1a]">Total Landed Cost</td>
//                   <td className="p-3 font-bold text-right text-[#dcff1a]">
//                     {formatCurrency(tariffObject.totalLandedCost)}
//                   </td>
//                 </tr>
//               </tbody>
//             </table>
//           </div>

//           {/* Optimized Routes Table */}
//           <div className="bg-slate-900/80 border border-white/10 rounded-xl shadow-lg overflow-hidden">
//             <div className="bg-gradient-to-r from-emerald-400 to-[#dcff1a] text-black px-4 py-3">
//               <h3 className="text-lg font-semibold">Optimized Routes</h3>
//               {/* <p className="text-sm text-black/80">
//                 {tariffObject.petroleumName} ({tariffObject.hsCode})
//               </p> */}
//             </div>

//             <div className="overflow-x-auto">
//               <table className="w-full text-sm text-gray-200">
//                 <thead className="bg-slate-800/60 text-gray-300 border-b border-white/10">
//                   <tr>
//                     <th className="p-3 text-left font-semibold">Route</th>
//                     <th className="p-3 text-right font-semibold">Base Cost</th>
//                     <th className="p-3 text-right font-semibold">Tariff</th>
//                     <th className="p-3 text-right font-semibold">VAT</th>
//                     <th className="p-3 text-right font-semibold">Total</th>
//                     <th className="p-3 text-right font-semibold">Shipping Cost</th>
//                   </tr>
//                 </thead>
//                 <tbody>
//                   {routes.map((route, index) => (
//                     <tr
//                       key={index}
//                       className={`border-b border-white/10 ${
//                         route.isDirect
//                           ? "bg-emerald-500/10"
//                           : "hover:bg-slate-800/50 transition-colors"
//                       }`}
//                     >
//                       <td className="p-3 text-left align-middle">
//                         <div className="flex flex-col gap-1 items-start">
//                           <span className="font-medium text-xs text-gray-300">{route.route}</span>
//                           {route.isDirect && (
//                             <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black w-fit">
//                               ⭐ Optimal
//                             </span>
//                           )}
//                         </div>
//                       </td>
//                       <td className="p-3 text-right">{formatCurrency(route.baseCost)}</td>
//                       <td className="p-3 text-right">{formatCurrency(route.tariffFees)}</td>
//                       <td className="p-3 text-right">{formatCurrency(route.vatFees)}</td>
//                       <td className="p-3 text-right font-bold text-[#dcff1a]">
//                         {formatCurrency(route.totalLandedCost)}
//                       </td>
//                       <td className="p-3 text-right font-bold text-[#dcff1a]">
//                         {(route.shippingCost)}
//                       </td>
              
//                     </tr>
//                   ))}
//                 </tbody>
//               </table>
//             </div>

//             {routes.length > 1 && (
//               <div className="bg-slate-800/60 px-4 py-3 text-sm text-gray-300 border-t border-white/10">
//                 <span className="font-semibold text-[#dcff1a]">Savings with optimal route:</span>{" "}
//                 {formatCurrency(routes[1].totalLandedCost - routes[0].totalLandedCost)}
//               </div>
//             )}
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// }

import { useState } from "react";
import type { Tariff } from "@/services/types/countrytariff";

export default function TariffBreakdownTable({ tariffObject }: { tariffObject: Tariff }) {
  const [showTables, setShowTables] = useState(false);

  const routes = [];

  // Direct route
  routes.push({
    transitCountry: "Direct",
    baseCost: tariffObject.basePrice,
    tariffFees: tariffObject.tariffFees,
    vatRate: tariffObject.vatRate,
    vatFees: tariffObject.vatFees,
    shippingCost: tariffObject.shippingCost,
    totalLandedCost: tariffObject.totalLandedCost,
    route: `${tariffObject.exportingCountry} → ${tariffObject.importingCountry}`,
    isDirect: true,
  });

  // Alternative routes
  Object.entries(tariffObject.alternativeRoutes || {}).forEach(([transitCountry, breakdown]) => {
    routes.push({
      ...breakdown,
      route: `${tariffObject.exportingCountry} → ${transitCountry} → ${tariffObject.importingCountry}`,
      isDirect: false,
    });
  });

  routes.sort((a, b) => a.totalLandedCost - b.totalLandedCost);

  // Mark the cheapest route as optimal
  if (routes.length > 0) {
    routes.forEach(route => route.isDirect = false); // Reset all
    routes[0].isDirect = true; // Mark cheapest as optimal
  }

  const formatCurrency = (value: number) =>
    new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: tariffObject.currency,
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);

  const formatPercentage = (value: number) => `${(value * 100).toFixed(2)}%`;

  return (
    <div className="p-6 max-w-7xl mx-auto">
      {/* Single Toggle Button */}
      <div className="flex justify-center mb-6">
        <button
          onClick={() => setShowTables(!showTables)}
          className={`rounded-xl px-6 py-3 font-medium transition-all border border-white/20 ${
            showTables
              ? "bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black shadow-lg"
              : "bg-slate-800/60 text-gray-200 hover:bg-slate-700"
          }`}
        >
          {showTables ? "Hide" : "Display"} Tariff Breakdown & Optimized Routes
        </button>
      </div>

      {/* Side-by-Side Tables Container */}
      <div
        className={`transition-all duration-300 ${
          showTables
            ? "opacity-100 max-h-[2000px]"
            : "opacity-0 max-h-0 overflow-hidden"
        }`}
      >
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Tariff Breakdown Table */}
          <div className="bg-slate-900/80 border border-white/10 rounded-xl shadow-lg overflow-hidden">
            <div className="bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black px-4 py-3">
              <h3 className="text-lg font-semibold">Tariff Breakdown</h3>
            </div>
            <table className="w-full text-sm text-gray-200">
              <tbody>
                {[
                  ["Importing Country", tariffObject.importingCountry],
                  ["Exporting Country", tariffObject.exportingCountry],
                  ["Petroleum Type", tariffObject.petroleumName],
                  ["Petroleum Code", tariffObject.hsCode],
                  ["Price per Unit", formatCurrency(tariffObject.pricePerUnit)],
                  ["Base Price", formatCurrency(tariffObject.basePrice)],
                  ["Tariff Rate", formatPercentage(tariffObject.tariffRate)],
                  ["Tariff Fees", formatCurrency(tariffObject.tariffFees)],
                  ["VAT Rate", formatPercentage(tariffObject.vatRate)],
                  ["VAT Fees", formatCurrency(tariffObject.vatFees)],
                ].map(([label, value], index) => (
                  <tr key={index} className="border-b border-white/10">
                    <td className="p-3 font-medium text-gray-400">{label}</td>
                    <td className="p-3 text-right text-gray-100">{value}</td>
                  </tr>
                ))}
                <tr className="bg-slate-800/80">
                  <td className="p-3 font-bold text-[#dcff1a]">Total Landed Cost</td>
                  <td className="p-3 font-bold text-right text-[#dcff1a]">
                    {formatCurrency(tariffObject.totalLandedCost)}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          {/* Optimized Routes Table */}
          <div className="bg-slate-900/80 border border-white/10 rounded-xl shadow-lg overflow-hidden">
            <div className="bg-gradient-to-r from-emerald-400 to-[#dcff1a] text-black px-4 py-3">
              <h3 className="text-lg font-semibold">Optimized Routes</h3>
              {/* <p className="text-sm text-black/80">
                {tariffObject.petroleumName} ({tariffObject.hsCode})
              </p> */}
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-sm text-gray-200">
                <thead className="bg-slate-800/60 text-gray-300 border-b border-white/10">
                  <tr>
                    <th className="p-3 text-left font-semibold">Route</th>
                    <th className="p-3 text-right font-semibold">Base Cost</th>
                    <th className="p-3 text-right font-semibold">Tariff</th>
                    <th className="p-3 text-right font-semibold">VAT</th>
                    <th className="p-3 text-right font-semibold">Shipping Cost</th>
                    <th className="p-3 text-right font-semibold">Total</th>
                  </tr>
                </thead>
                <tbody>
                  {routes.map((route, index) => (
                    <tr
                      key={index}
                      className={`border-b border-white/10 ${
                        route.isDirect
                          ? "bg-emerald-500/10"
                          : "hover:bg-slate-800/50 transition-colors"
                      }`}
                    >
                      <td className="p-3 text-left align-top">
                        <div className="flex flex-col gap-1">
                          <span className="font-medium text-xs text-gray-300">{route.route}</span>
                          {route.isDirect && (
                            <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black w-fit">
                              ⭐ Optimal
                            </span>
                          )}
                        </div>
                      </td>
                      <td className="p-3 text-right">{formatCurrency(route.baseCost)}</td>
                      <td className="p-3 text-right">{formatCurrency(route.tariffFees)}</td>
                      <td className="p-3 text-right">{formatCurrency(route.vatFees)}</td>
                      <td className="p-3 text-right">{formatCurrency(route.shippingCost)}</td>
                      <td className="p-3 text-right font-bold text-[#dcff1a]">
                        {formatCurrency(route.totalLandedCost)}
                      </td>
                    </tr>
                  ))}
                  {routes.length > 1 && (
                  <tr className="bg-slate-800/80 border-t-2 border-white/20">
                    <td
                      colSpan={6}
                      className="p-4 text-center font-semibold text-[#dcff1a] tracking-wide"
                    >
                      Savings with optimal route:&nbsp;
                      {formatCurrency(routes[1].totalLandedCost - routes[0].totalLandedCost)}
                    </td>
                  </tr>
                )}

                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}