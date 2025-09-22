"use client";
import { useState } from "react";

export default function TariffCalculator() {
  const [origin, setOrigin] = useState("Singapore");
  const [destination, setDestination] = useState("Malaysia");
  const [productRate, setProductRate] = useState(0.05);
  const [quantity, setQuantity] = useState<number | "">("");
  const [result, setResult] = useState<string>("");

  function calculateTariff() {
    const qty = typeof quantity === "number" ? quantity : Number(quantity);
    if (!qty || qty <= 0) {
      setResult("Please enter a valid quantity.");
      return;
    }
    const basePrice = 1.2; // mock price per litre
    const subtotal = basePrice * qty;
    const tariff = subtotal * productRate;
    const total = subtotal + tariff;

    setResult(
      `Subtotal: $${subtotal.toFixed(2)} · Tariff: $${tariff.toFixed(
        2
      )} · Total: $${total.toFixed(2)}`
    );
  }

  return (
    <div className="w-full max-w-xl rounded-2xl border border-[--border] bg-[--card] p-6 shadow-lg
                    [--card:theme(colors.background)] [--border:color-mix(in_oklab,currentColor_20%,transparent)]">
      <h1 className="text-xl font-semibold">Petroleum Tariff Calculator</h1>
      <p className="text-sm text-muted-foreground mt-1">
        Mock prototype — no real data yet.
      </p>

      {/* 2 columns on md+ screens */}
      <div className="mt-6 grid gap-4 md:grid-cols-2">
        <div>
          <label htmlFor="origin" className="block text-sm text-muted-foreground mb-1">
            Origin Country
          </label>
          <select
            id="origin"
            value={origin}
            onChange={(e) => setOrigin(e.target.value)}
            className="w-full h-10 rounded-lg border border-[--border] bg-transparent px-3 outline-none focus:ring-4 focus:ring-blue-500/30"
          >
            <option>Singapore</option>
            <option>Malaysia</option>
            <option>USA</option>
          </select>
        </div>

        <div>
          <label htmlFor="destination" className="block text-sm text-muted-foreground mb-1">
            Destination Country
          </label>
          <select
            id="destination"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            className="w-full h-10 rounded-lg border border-[--border] bg-transparent px-3 outline-none focus:ring-4 focus:ring-blue-500/30"
          >
            <option>Singapore</option>
            <option>Malaysia</option>
            <option>USA</option>
          </select>
        </div>

        <div>
          <label htmlFor="product" className="block text-sm text-muted-foreground mb-1">
            Product
          </label>
          <select
            id="product"
            value={productRate}
            onChange={(e) => setProductRate(parseFloat(e.target.value))}
            className="w-full h-10 rounded-lg border border-[--border] bg-transparent px-3 outline-none focus:ring-4 focus:ring-blue-500/30"
          >
            <option value={0.05}>RON95 (5% tariff)</option>
            <option value={0.07}>RON97 (7% tariff)</option>
            <option value={0.04}>Diesel (4% tariff)</option>
          </select>
        </div>

        <div>
          <label htmlFor="qty" className="block text-sm text-muted-foreground mb-1">
            Quantity (litres)
          </label>
          <input
            id="qty"
            type="number"
            min={1}
            value={quantity}
            onChange={(e) => setQuantity(e.target.value === "" ? "" : Number(e.target.value))}
            className="w-full h-10 rounded-lg border border-[--border] bg-transparent px-3 outline-none focus:ring-4 focus:ring-blue-500/30"
          />
        </div>
      </div>

      <button
        onClick={calculateTariff}
        className="mt-5 w-full h-11 rounded-lg bg-blue-500 font-medium text-white hover:bg-blue-600 active:translate-y-[1px] transition"
      >
        Calculate
      </button>

      {result && (
        <div className="mt-4 rounded-lg border border-[--border] bg-black/5 p-3 text-sm font-semibold">
          {result}
        </div>
      )}
    </div>
  );
}
