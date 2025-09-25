import './App.css';
import { Layout } from '@/components/pages/layout';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import CountryTariffList from '@/services/CountryTariffList';
import TariffCalculator from './services/TariffCalculator';

function App() {

  return (
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
  );
}

export default App;
