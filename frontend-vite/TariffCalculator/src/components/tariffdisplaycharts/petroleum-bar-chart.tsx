"use client"

import { Bar, BarChart, XAxis, YAxis } from "recharts"
import { useState } from "react"

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
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
    //for each entry of country map the country name, vat rate
    let data = [...petroleum].map(petrol => ({
      country: petrol.name,
      pricePerUnit: petrol.pricePerUnit,
      fill: "var(--chart-1)"
    }));

    // Sort function to sort by ascending or descending order
    if (sortOrder === 'ascending') {
      data.sort((a, b) => a.pricePerUnit - b.pricePerUnit);
    } else if (sortOrder === 'descending') {
      data.sort((a, b) => b.pricePerUnit - a.pricePerUnit);
    }

    const sorted = [...data].sort((a, b) => b.pricePerUnit - a.pricePerUnit);
    data = sorted.slice(0, topResults);
    
    // Re-sort based on sortOrder
    if (sortOrder === 'ascending') {
      data.sort((a, b) => a.pricePerUnit - b.pricePerUnit);
    }

    return data;
  };

  const chartData = processedData();

  return (
    <Card>
      <CardHeader>
        <CardTitle>VAT Rates by Country</CardTitle>
        <CardDescription>
          {`Top ${topResults} petroleum products by Price per unit`}
          {sortOrder !== 'none' && ` - Sorted ${sortOrder}`}
        </CardDescription>
        
        {/* Add buttons in the header */}
        <div className="flex gap-2 mt-4">
          <button
            onClick={() => setSortOrder('ascending')}
            className={`px-3 py-1 text-sm rounded transition-colors ${
              sortOrder === 'ascending' 
                ? 'bg-sky-500 text-white' 
                : 'bg-gray-200 hover:bg-gray-300'
            }`}
          >
            Ascending
          </button>
          <button
            onClick={() => setSortOrder('descending')}
            className={`px-3 py-1 text-sm rounded transition-colors ${
              sortOrder === 'descending' 
                ? 'bg-sky-500 text-white' 
                : 'bg-gray-200 hover:bg-gray-300'
            }`}
          >
            Descending
          </button>
          <button
            onClick={() => setSortOrder('none')}
            className={`px-3 py-1 text-sm rounded transition-colors ${
              sortOrder === 'none' 
                ? 'bg-sky-500 text-white' 
                : 'bg-gray-200 hover:bg-gray-300'
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
            }}
          >
            <YAxis
              dataKey="country"
              type="category"
              tickLine={false}
              tickMargin={10}
              axisLine={false}
              width={100}
            />
            <XAxis dataKey="pricePerUnit" type="number" hide />
            <ChartTooltip
              cursor={false}
              content={<ChartTooltipContent hideLabel />}
            />
            <Bar dataKey="pricePerUnit" layout="vertical" radius={3} // smaller radius for thinner bar
              barSize={20} // thinner bars so tooltip is visible
              />
          </BarChart>
        </ChartContainer>
      </CardContent>
      <CardFooter className="flex-col items-start gap-2 text-sm">
        <div className="text-muted-foreground leading-none">
          Showing Petroleum price per unit
        </div>
      </CardFooter>
    </Card>
  )
}