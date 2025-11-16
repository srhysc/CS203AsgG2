import { Bar, BarChart, YAxis, XAxis } from "recharts"
import { useState } from "react"


import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart"

import type { ChartConfig } from "@/components/ui/chart"
import type {Country} from "@/services/types/country"

//props that Chart will accept
interface CountryBarChartProps {
  countries: Country[];
}

const chartConfig = {
  vatRate: {
    //tooltip value
    label: "VAT Rate",
    //bar colour
    color: "var(--chart-1)",
  },
} satisfies ChartConfig

//top number of results to be shown
const topResults = 5;

export function CountryBarChart({ 
  countries, 
}: CountryBarChartProps) {
  // useState inside the component to track sort order
  const [sortOrder, setSortOrder] = useState<'ascending' | 'descending' | 'none'>('descending');

  // Process the data based on sorting and filtering
  const processedData = () => {
    //for each entry of country map the country name, vat rate
    let data = [...countries].map(country => ({
      country: country.name,
      vatRate: country.vatRate,
      fill: "var(--chart-1)"
    }));

    /// Sort and slice based on sortOrder
  if (sortOrder === 'ascending') {
        // Bottom 5 (lowest prices) displayed in ascending order
        data.sort((a, b) => a.vatRate - b.vatRate);
        data = data.slice(0, topResults);
    } 
    
    else if (sortOrder === 'descending') {
        // Top 5 (highest prices) displayed in descending order
        data.sort((a, b) => b.vatRate - a.vatRate);
        data = data.slice(0, topResults);
    } else{
        // 'none' shows all data unsorted
        data = data.slice(0, topResults);

    }

    return data;
  };

  const chartData = processedData();

  return (
    <Card className="dark:text-gray-100">
      <CardHeader>
        <CardTitle className="dark:text-gray-100">VAT Rates by Country</CardTitle>
        <CardDescription className="dark:text-gray-400">
          {`Countries by VAT rate`}
          {sortOrder !== 'none' && ` - Sorted ${sortOrder}`}
        </CardDescription>
        
        {/* Add buttons in the header */}
        <div className="flex gap-2 mt-4">
          <button
            onClick={() => setSortOrder('ascending')}
            className={`px-3 py-1 text-sm rounded transition-colors ${
              sortOrder === 'ascending' 
                ? 'bg-sky-500 text-white' 
                : 'bg-gray-200 hover:bg-gray-300 dark:bg-gray-400 dark:hover:bg-gray-350'
            }`}
          >
            Ascending
          </button>
          <button
            onClick={() => setSortOrder('descending')}
            className={`px-3 py-1 text-sm rounded transition-colors ${
              sortOrder === 'descending' 
                ? 'bg-sky-500 text-white' 
                : 'bg-gray-200 hover:bg-gray-300 dark:bg-gray-400 dark:hover:bg-gray-350'
            }`}
          >
            Descending
          </button>
          <button
            onClick={() => setSortOrder('none')}
            className={`px-3 py-1 text-sm rounded transition-colors ${
              sortOrder === 'none' 
                ? 'bg-sky-500 text-white' 
                : 'bg-gray-200 hover:bg-gray-300 dark:bg-gray-400 dark:hover:bg-gray-350'
            }`}
          >
            None
          </button>
        </div>
      </CardHeader>
      <CardContent>
        <ChartContainer config={chartConfig}>
            <BarChart
            accessibilityLayer
            data={chartData}
            layout="vertical"
            margin={{
                left: 0,
                right: 10, // Add some right margin
            }}
            >
            <YAxis
                dataKey="country"
                type="category"
                tickLine={false}
                tickMargin={10}
                axisLine={false}
                width={80} // Reduced from 100 for narrower cards
                tick={{ fontSize: 12 }} // Smaller font for compact view
            />
            <XAxis 
                type="number"
                hide={true}  // Hide it if you don't want it visible
            />

            {/* TOOLTIP - Appears on hover */}
            <ChartTooltip
            cursor={{ fill: "transparent" }}
            content={
                <ChartTooltipContent 
                formatter={(value) => `$${value}`}
                hideLabel={true} // hide the bar/category name
                className="bg-sky-300 text-black rounded-md px-2 py-1" // Tailwind for tooltip
                />
            }
            />
            
            <Bar 
                dataKey="vatRate" 
                layout="vertical" 
                radius={3}
                barSize={20} // Even thinner bars for compact cards
                animationEasing="ease-out" // Smooth easing function
                style={{ fill: "#3B82F6" }}
            />
            </BarChart>
        </ChartContainer>
      </CardContent>

    </Card>
  )
}