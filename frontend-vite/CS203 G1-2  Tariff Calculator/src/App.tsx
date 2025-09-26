import './App.css';
import { Layout } from '@/components/pages/layout';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import CountryTariffList from '@/services/CountryTariffList';
import TariffCalculator from './services/TariffCalculator';
import { AuroraBackground } from './components/ui/aurora-background';
import {motion} from "motion/react"

function App() {

  return (
    <AuroraBackground>
      <motion.div
        initial={{ opacity: 0.0, y: 40 }}
        whileInView={{ opacity: 1, y: 0 }}
        transition={{
          delay: 0.3,
          duration: 0.8,
          ease: "easeInOut",
        }}
        className="relative flex flex-col gap-4 items-center justify-center px-4"
      >
    <Router>
    <Layout>
      <div className="App">
        <header className="App-header">
          <p>
            Edit <code>src/App.tsx</code> and save to reload.
          </p>
          <a
            className="App-link"
            href="https://reactjs.org"
            target="_blank"
            rel="noopener noreferrer"
          >
            Learn React
          </a>
        </header>

        <main>
          <Routes>
            <Route path="/" element={<h2>Welcome to the App</h2>} />
            <Route path="/vat" element = {<CountryTariffList />} />
            <Route path="/calculator" element={<TariffCalculator />}/>
          </Routes>
        </main>
      </div>
    </Layout>
    </Router>
    </motion.div>
    </AuroraBackground>
  );
}

export default App;
