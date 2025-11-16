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
import type {Petroleum} from "@/services/types/petroleum"

//props that Chart will accept
interface PetrolBarChartProps {
  petroleum: Petroleum[];
}

const chartConfig = {
  pricePerUnit: {
    //tooltip value
    label: "Price per unit",
    //bar colour
    color: "var(--chart-1)",
  },
} satisfies ChartConfig

//top number of results to be shown
const topResults = 5;

export function PetroleumBarChart({ 
  petroleum, 
}: PetrolBarChartProps) {
  // useState inside the component to track sort order
  const [sortOrder, setSortOrder] = useState<'ascending' | 'descending' | 'none'>('descending');

  // Process the data based on sorting and filtering
  const processedData = () => {
    //for each entry of data, map to product and price
    let data = [...petroleum].map(petrol => ({
      product: petrol.name,
      pricePerUnit: petrol.pricePerUnit,
      fill: "var(--chart-1)"
    }));

    
  // Sort and slice based on sortOrder
  if (sortOrder === 'ascending') {
        // Bottom 5 (lowest prices) displayed in ascending order
        data.sort((a, b) => a.pricePerUnit - b.pricePerUnit);
        data = data.slice(0, topResults);
    } 
    
    else if (sortOrder === 'descending') {
        // Top 5 (highest prices) displayed in descending order
        data.sort((a, b) => b.pricePerUnit - a.pricePerUnit);
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
        <CardTitle className="dark:text-gray-100">Petroleum Prices</CardTitle>
        <CardDescription className="dark:text-gray-400">
          {`Petroleum by Price per unit`}
        </CardDescription>
        
        <div className="flex flex-wrap gap-2 mt-4">
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
              right: 20,
              top: 0,
              bottom: 0,
            }}
          >
            {/* Y-AXIS - Shows product names on the left */}
            <YAxis
              dataKey="product"        // Which field to show
              type="category"          // Categorical data (not numbers)
              tickLine={false}         // Hide tick marks
              tickMargin={10}          // Space between labels and axis
              axisLine={false}         // Hide the axis line
              width={80}              // Width for product names
              tick={{ fontSize: 12 }} // Font size of labels
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
            
            {/* THE BARS */}
            <Bar 
              dataKey="pricePerUnit"   // Which field determines bar length
              layout="vertical"        // Horizontal bars
              radius={3}               // Rounded corners (3px)
              barSize={20}             // Thickness of bars (15px)
              animationEasing="ease-out" // Smooth easing function
              style={{ fill: "#3B82F6" }}
            />
          </BarChart>
        </ChartContainer>
      </CardContent>
    </Card>
  )
}