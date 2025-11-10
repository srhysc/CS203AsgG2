// import './App.css';
// import './index.css'
// import { Layout } from '@/components/pages/layout';
// import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
// import { AuroraBackground } from './components/ui/aurora-background';
// import {motion} from "motion/react"
// import { ThemeProvider } from "@/components/ui/theme-provider"
// import  Home  from './components/pages/home';
// import TariffCalculator from './services/TariffCalculator';
// import TariffLookup from './services/TariffDisplay';
// import Adminhome from './components/pages/administrator/adminhome';
// import EditTariffs from "./components/pages/administrator/edittariffs";
// import SyncUserToBackend from './services/firebaseusersync';


// function App() {

//   return (
//     <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
//     <SyncUserToBackend />
//     <AuroraBackground>
//       <motion.div
//         initial={{ opacity: 0.0, y: 40 }}
//         whileInView={{ opacity: 1, y: 0 }}
//         transition={{
//           delay: 0.3,
//           duration: 0.8,
//           ease: "easeInOut",
//         }}
//         className="relative flex flex-col gap-4 items-center justify-center px-4 w-screen"
//       >
//     <Router>
//     <Layout>
      
//       <div className="App">
//         <header className="App-header">
    
//         </header>
//         <main>
//           <Routes>
//             <Route path="/" element={<Home />} />
//             <Route path="/calculator" element={<TariffCalculator />}/>
//             <Route path="/lookup" element={<TariffLookup />}/>
//             <Route path="/administrator" element={<Adminhome />} />
//             <Route path="/administrator/tariffs" element={<EditTariffs />} />
//           </Routes>
//         </main>
//       </div>
//     </Layout>
//     </Router>
//     </motion.div>
//     </AuroraBackground>
//     </ThemeProvider>
//   );
// }

// export default App;


import "./App.css";
import "./index.css";
import { BrowserRouter as Router, Route, Routes, useLocation } from "react-router-dom";
import { AuroraBackground } from "./components/ui/aurora-background";
import { motion, AnimatePresence } from "framer-motion";
import { ThemeProvider } from "@/components/ui/theme-provider";
import { Layout } from "@/components/layout/layout";
import SyncUserToBackend from './services/firebaseusersync';

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

import RefineryInfoPage from "@/components/pages/RefineryInfoPage";
import Convertable from "@/components/pages/ConvertablePage";


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
        <Route path="/convertable" element={<Convertable />} />
        <Route path="/administrator" element={<Adminhome />} />
        <Route path="/administrator/tariffs" element={<EditTariffs />} />
        <Route path="/administrator/VAT-rates" element={<EditVATRates />} />
        <Route path="/administrator/shipping-fees" element={<EditShippingFee />} />
        <Route path="/administrator/product-prices" element={<EditProductPricesPage />} />
        <Route path = "/administrator/manage-admins" element= {<ManageAdminsPage/>} />
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