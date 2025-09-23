import React, {useState} from 'react';
import logo from './logo.svg';
import './App.css';
import { Layout } from '@/components/pages/layout';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import CountryList from '@/services/countryList';

function App() {

  return (
    <Router>
    <Layout>
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
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
            <Route path="/countries" element = {<CountryList />} />
          </Routes>
        </main>
      </div>
    </Layout>
    </Router>
  );
}

export default App;
