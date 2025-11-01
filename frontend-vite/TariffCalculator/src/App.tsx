import "./App.css";
import "./index.css";
import { BrowserRouter as Router, Route, Routes, useLocation } from "react-router-dom";
import { AuroraBackground } from "./components/ui/aurora-background";
import { motion, AnimatePresence } from "framer-motion";
import { ThemeProvider } from "@/components/ui/theme-provider";
import Home from "./components/pages/home";
import ShippingCostPage from "./components/pages/shipping-cost";
import PetroleumDetailsPage from "./components/pages/petroleum-details";
import CountryInfoPage from "./components/pages/country-info";
import TariffCalculator from "./services/TariffCalculator";
import TariffLookup from "./services/TariffDisplay";
import Adminhome from "./components/pages/adminhome";
import EditTariffs from "./components/pages/edittariffs";
import RefineryInfoPage from "./components/pages/RefineryInfoPage";
import { Layout } from "./components/layout/Layout";

function AnimatedRoutes() {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/" element={<Home />} />
        <Route path="/calculator" element={<TariffCalculator />} />
        <Route path="/lookup" element={<TariffLookup />} />
        <Route path="/country" element={<CountryInfoPage />} />
        <Route path="/petroleum" element={<PetroleumDetailsPage />} />
        <Route path="/refineries" element={<RefineryInfoPage />} />
        <Route path="/shipping" element={<ShippingCostPage />} />
        <Route path="/route" element={<div>Refinery Route Page (coming soon)</div>} />
        <Route path="/administrator" element={<Adminhome />} />
        <Route path="/admin/tariffs" element={<EditTariffs />} />
      </Routes>
    </AnimatePresence>
  );
}

function App() {
  return (
    <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
      <AuroraBackground>
        <Router>
          {/* Persistent Layout */}
          <Layout activeTab="home" setActiveTab={() => {}}>
            <motion.div
              initial={{ opacity: 0.0, y: 40 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{
                delay: 0.3,
                duration: 0.8,
                ease: "easeInOut",
              }}
              className="relative flex flex-col gap-4 items-center justify-center px-4 w-screen"
            >
              {/* Animated Routes */}
              <AnimatedRoutes />
            </motion.div>
          </Layout>
        </Router>
      </AuroraBackground>
    </ThemeProvider>
  );
}

export default App;