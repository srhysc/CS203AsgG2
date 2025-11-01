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
<<<<<<< HEAD
import Adminhome from './components/pages/administrator/adminhome';
import EditTariffs from "./components/pages/administrator/edittariffs";

=======
import SyncUserToBackend from './services/firebaseusersync';
>>>>>>> 2687790edb2074beab116fa51584595ce8a4abbb


function App() {

  return (
    <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
    <SyncUserToBackend />
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
            <Route path="/administrator/tariffs" element={<EditTariffs />} />
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
