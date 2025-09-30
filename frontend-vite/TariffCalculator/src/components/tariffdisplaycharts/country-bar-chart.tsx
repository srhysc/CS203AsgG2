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

    // Sort function to sort by ascending or descending order
    if (sortOrder === 'ascending') {
      data.sort((a, b) => a.vatRate - b.vatRate);
    } else if (sortOrder === 'descending') {
      data.sort((a, b) => b.vatRate - a.vatRate);
    }

    const sorted = [...data].sort((a, b) => b.vatRate - a.vatRate);
    data = sorted.slice(0, topResults);
    
    // Re-sort based on sortOrder
    if (sortOrder === 'ascending') {
      data.sort((a, b) => a.vatRate - b.vatRate);
    }

    return data;
  };

  const chartData = processedData();

  return (
    <Card>
      <CardHeader>
        <CardTitle>VAT Rates by Country</CardTitle>
        <CardDescription>
          {`Top ${topResults} countries by VAT rate`}
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
            <Bar 
                dataKey="vatRate" 
                layout="vertical" 
                radius={3}
                barSize={15} // Even thinner bars for compact cards
            />
            </BarChart>
        </ChartContainer>
      </CardContent>
      <CardFooter className="flex-col items-start gap-2 text-sm">
        <div className="text-muted-foreground leading-none">
          Showing VAT rates as percentages
        </div>
      </CardFooter>
    </Card>
  )
}