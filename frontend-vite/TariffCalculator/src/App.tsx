import './App.css';
import './index.css'
import { Layout } from '@/components/pages/layout';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { AuroraBackground } from './components/ui/aurora-background';
import {motion} from "motion/react"
import { ThemeProvider } from "@/components/ui/theme-provider"
import  Home  from './components/pages/home';
import TariffCalculator from './services/TariffCalculator';
import TariffLookup from './services/TariffDisplay';
import Adminhome from './components/pages/adminhome';
import EditTariffs from "./components/pages/edittariffs";



function App() {

  return (
    <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
    <AuroraBackground>
      <motion.div
        initial={{ opacity: 0.0, y: 40 }}
        whileInView={{ opacity: 1, y: 0 }}
        transition={{
          delay: 0.3,
          duration: 0.8,
          ease: "easeInOut",
        }}
        className="relative flex flex-col gap-4 items-center justify-center px-4 w-screen"
      >
    <Router>
    <Layout>
      
      <div className="App">
        <header className="App-header">
    
        </header>
        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/calculator" element={<TariffCalculator />}/>
            <Route path="/lookup" element={<TariffLookup />}/>
            <Route path="/administrator" element={<Adminhome />} />
            <Route path="/admin/tariffs" element={<EditTariffs />} />
          </Routes>
        </main>
      </div>
    </Layout>
    </Router>
    </motion.div>
    </AuroraBackground>
    </ThemeProvider>
  );
}

export default App;
