// import { useState } from "react";
// import type {Tariff} from '@/services/types/countrytariff'


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


import { useState } from "react";
import type { Tariff } from '@/services/types/countrytariff'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ChevronDown, ChevronUp } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function TariffBreakdown({ tariffObject }: { tariffObject: Tariff }) {
    const [showTable, setShowTable] = useState(false);

    return (
        <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
            <CardHeader>
                <CardTitle className="flex items-center justify-between">
                    <span>Tariff Breakdown</span>
                    <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setShowTable(!showTable)}
                        className="text-gray-400 hover:text-white"
                    >
                        {showTable ? <ChevronUp /> : <ChevronDown />}
                    </Button>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div className={`transition-all duration-300 ${
                    showTable ? "opacity-100 max-h-[800px]" : "opacity-0 max-h-0"
                } overflow-hidden`}>
                    <Table>
                        <TableBody>
                            <TableRow>
                                <TableCell className="text-gray-400">Importing Country</TableCell>
                                <TableCell className="text-right font-medium">
                                    {tariffObject.importingCountry}
                                </TableCell>
                            </TableRow>
                            {/* Add all other rows similarly */}
                        </TableBody>
                    </Table>
                </div>
            </CardContent>
        </Card>
    );
}