import React from 'react';
import {
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend
} from 'recharts';
import type { Tariff } from '@/services/types/countrytariff';

interface TariffBreakdownChartProps {
  tariff: Tariff;
  type: 'pie' | 'bar';
}

const COLORS = ['#dcff1a', '#34d399', '#818cf8', '#f87171'];

export const TariffBreakdownChart: React.FC<TariffBreakdownChartProps> = ({
  tariff,
  type
}) => {
  const data = [
    {
      name: 'Base Price',
      value: Number(tariff.basePrice)
    },
    {
      name: 'Tariff Fees',
      value: Number(tariff.tariffFees)
    },
    {
      name: 'VAT Fees',
      value: Number(tariff.vatFees)
    }
  ];

  if (type === 'pie') {
    return (
      <div className="w-full h-[400px]">
        <ResponsiveContainer>
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              labelLine={false}
              label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
              outerRadius={150}
              fill="#8884d8"
              dataKey="value"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip
              contentStyle={{
                backgroundColor: '#1e1b4b',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                borderRadius: '6px',
              }}
              itemStyle={{ color: '#fff' }}
            />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </div>
    );
  }

  return (
    <div className="w-full h-[400px]">
      <ResponsiveContainer>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#ffffff20" />
          <XAxis dataKey="name" stroke="#94a3b8" />
          <YAxis stroke="#94a3b8" />
          <Tooltip
            contentStyle={{
              backgroundColor: '#1e1b4b',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              borderRadius: '6px',
            }}
            itemStyle={{ color: '#fff' }}
          />
          <Legend />
          <Bar dataKey="value" fill="#dcff1a" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};