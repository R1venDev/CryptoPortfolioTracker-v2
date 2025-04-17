import React, { useState, useEffect } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import Home from "./pages/Home";
import Register from "./pages/Register";
import Login from "./pages/Login";
import CreatePortfolio from "./pages/CreatePortfolio";
import PortfolioSelector from "./components/PortfolioSelector";
import Portfolio from "./pages/Portfolio";
import { getUsername, getToken, setUserSession, resetUserSession } from './service/authService';
import axios from './apis/cryptoPortfolio.js';
import { APIKey } from "./apis/apiKey";
import "./App.css";
import TransactionHistory from "./pages/TransactionHistory";
import UpdatePortfolio from "./pages/UpdatePortfolio";
import AdminPage from "./pages/AdminPage";

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(null);
    const [portfolioResponse, setPortfolioResponse] = useState(null);
    const navigate = useNavigate();  // Initialize navigate

    const handleLoginSuccess = (loginResponseData) => {
        setIsAuthenticated(true); // Mark user as authenticated
        console.log("here we are " + loginResponseData.authToken);
        console.log(loginResponseData);
        const requestConfig = {
            headers: {
                'x-api-key': APIKey,
                'cp-auth-token': loginResponseData.authToken
            }
        };
        console.log(sessionStorage.getItem('user'));
        console.log(requestConfig)

        axios.get(`get-portfolio/${sessionStorage.getItem('user')}`, requestConfig)
            .then((receivedResponse) => {
                console.log("successful call to get-portfolio")
                receivedResponse = receivedResponse.data;
                setPortfolioResponse(receivedResponse);
                console.log(receivedResponse);
                // Navigate to one of the portfolio-related pages based on response
                if (receivedResponse.portfolio) {
                    console.log("here are we have one porfolio");
                    const customPortfolio = {
                        portfolioId: receivedResponse.portfolio.id,
                        transactions: receivedResponse.portfolio.transactions,
                        portfolioName: receivedResponse.portfolio.portfolioName,
                        username: receivedResponse.portfolio.username,
                        assetQuantityMap: receivedResponse.portfolio.assetQuantityMap
                    }
                    const response = {portfolios:[customPortfolio], porfolio: null, isEmpty:false};
                    navigate('/portfolioSelector', { state: { response: response}}); // Redirect to PortfolioSelector
                    //navigate(`/portfolio/${response.portfolio.id}`, { state: { response} }); // Redirect to Portfolio
                } else if (receivedResponse.portfolios && receivedResponse.portfolios.length > 0) {
                    // Create custom portfolio objects for each portfolio in the receivedResponse.portfolios array
                    const customPortfolios = receivedResponse.portfolios.map((portfolio) => ({
                        portfolioId: portfolio.id,
                        transactions: portfolio.transactions,
                        portfolioName: portfolio.portfolioName,
                        username: portfolio.username,
                        assetQuantityMap: portfolio.assetQuantityMap
                    }));

                    const response = { portfolios: customPortfolios, portfolio: null, isEmpty: false };
                    console.log("here are several portfolio");
                    navigate('/portfolioSelector', { state: { response: response}}); // Redirect to PortfolioSelector
                } else if (receivedResponse.isEmpty) {
                    console.log("here are no portfolio for current user");
                    navigate('/portfolio', { state: {response: receivedResponse} }); // Redirect to CreatePortfolio
                }
            })
            .catch(() => {
                console.log("we are not go anywhere");
                navigate('/');
            });
    };
    useEffect(() => {
        if (portfolioResponse) {
            console.log('Updated portfolioResponse:', portfolioResponse); // This will print after state has updated
        }
    }, [portfolioResponse]);

    return (
        <div className="container">
            <div className="header">
                <h4 className="text">Crypto Portfolio Tracker</h4>
            </div>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route
                    path="/login"
                    element={<Login onLoginSuccess={handleLoginSuccess} />}
                />
                <Route path="/register" element={<Register />} />
                <Route path="/portfolioSelector" element={<PortfolioSelector response={portfolioResponse}/>} />
                <Route path="/create-portfolio" element={<CreatePortfolio />} />
                <Route path="/portfolio/:id" element={<Portfolio />} />
                <Route path="/portfolio" element={<Portfolio />} />
                <Route path="/transactionHistory" element={<TransactionHistory />} />
                <Route path="/updatePortfolio" element={<UpdatePortfolio />} />
                <Route path="/admin" element={<AdminPage />} />
            </Routes>
        </div>
    );
}

export default App;
