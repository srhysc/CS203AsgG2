import "./App.css";
import "./index.css";
import { BrowserRouter as Router, Route, Routes, useLocation } from "react-router-dom";
import { AuroraBackground } from "./components/ui/aurora-background";
import { motion, AnimatePresence } from "framer-motion";
import { ThemeProvider } from "@/components/ui/theme-provider";
import { Layout } from "@/components/layout/layout";
import SyncUserToBackend from './services/firebaseusersync';
import { ProtectedRoute, AdminRoute } from "./services/auth/ProtectedRoute";


// Page Imports
import Home from "@/components/pages/home";
import ShippingCostPage from "@/components/pages/shipping-cost";
import PetroleumDetailsPage from "@/components/pages/petroleum-details";
import CountryInfoPage from "@/components/pages/country-info";
import TariffCalculator from "@/services/TariffCalculator";
import TariffLookup from "@/components/pages/TariffDetailsPage";
import Adminhome from "@/components/pages/administrator/adminhome";
import EditTariffs from "@/components/pages/administrator/edittariffs";
import EditVATRates from "@/components/pages/administrator/editVATrates";
import EditShippingFee from "@/components/pages/administrator/editshippingfees";
import EditProductPricesPage from "./components/pages/administrator/editproductprices";
import ManageAdminsPage from "./components/pages/administrator/manageuserroles";
import BookmarksPage from "@/components/pages/BookmarksPage";
import RefineryInfoPage from "@/components/pages/RefineryInfoPage";
import Convertable from "@/components/pages/ConvertablePage";


function AnimatedRoutes() {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/" element={<Home />} />
        <Route path="/calculator" element={
          <ProtectedRoute>
              <TariffCalculator />
            </ProtectedRoute>
          } />
        <Route path="/lookup" element={
            <ProtectedRoute>
              <TariffLookup />
            </ProtectedRoute>
          } />
        <Route path="/country" element={
            <ProtectedRoute>
              <CountryInfoPage />
            </ProtectedRoute>
          } />
        <Route path="/petroleum" element={
            <ProtectedRoute>
              <PetroleumDetailsPage />
            </ProtectedRoute>
          } />
        <Route path="/refineries" element={
          <ProtectedRoute>
            <RefineryInfoPage />
          </ProtectedRoute>
          } />
        <Route path="/shipping" element={
          <ProtectedRoute>
            <ShippingCostPage />
          </ProtectedRoute>
          } />
        <Route path="/convertable" element={
          <ProtectedRoute>
            <Convertable />
          </ProtectedRoute>
          } />
        <Route path="/route" element={<div>Refinery Route Page (coming soon)</div>} />
        <Route path="/bookmarks" element={
          <ProtectedRoute>
            <BookmarksPage />
          </ProtectedRoute>
          } />
        <Route path="/administrator" element={
          <AdminRoute>
            <Adminhome />
          </AdminRoute>
          } />
        <Route path="/administrator/tariffs" element={
          <AdminRoute>
            <EditTariffs />
          </AdminRoute>
          } />
        <Route path="/administrator/VAT-rates" element={
          <AdminRoute>
            <EditVATRates />
          </AdminRoute>
          } />
        <Route path="/administrator/shipping-fees" element={
          <AdminRoute>
            <EditShippingFee />
          </AdminRoute>
          } />
        <Route path="/administrator/product-prices" element={
          <AdminRoute>
            <EditProductPricesPage />
          </AdminRoute>
          } />
        <Route path = "/administrator/manage-admins" element= {
           <AdminRoute>
            <ManageAdminsPage/>
          </AdminRoute>
          } />

      </Routes>
    </AnimatePresence>
  );
}


function App() {
  return (
    <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
      <SyncUserToBackend />
      <AuroraBackground>
        <Router>
          <Layout>
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
              <AnimatedRoutes />
            </motion.div>
          </Layout>
        </Router>
      </AuroraBackground>
    </ThemeProvider>
  );
}

export default App;