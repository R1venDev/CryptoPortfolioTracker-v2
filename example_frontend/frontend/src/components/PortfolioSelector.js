import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { getToken } from "../service/authService";
import coinGecko from "../apis/coinGecko";

const PortfolioSelector = (props) => {
    const location = useLocation();
    const response = location.state?.response;
    const navigate = useNavigate();
    const [portfolios, setPortfolios] = useState(response.portfolios);
    const [assets, setAssets] = useState(null);
    const [assetMap, setAssetMap] = useState(null);

    const masterList = [
        "bitcoin", "ethereum", "tether",
        "binancecoin", "usd-coin", "ripple",
        "cardano", "solana", "avalanche-2",
        "terra-luna", "polkadot", "dogecoin",
        "binance-usd", "shiba-inu", "matic-network",
        "crypto-com-chain", "terrausd", "wrapped-bitcoin",
        "dai", "cosmos", "litecoin",
        "chainlink", "near", "tron",
        "ftx-token", "algorand", "bitcoin-cash",
        "staked-ether", "okb", "stellar",
        "leo-token", "fantom", "uniswap",
        "decentraland", "hedera-hashgraph", "internet-computer",
        "the-sandbox", "axie-infinity", "ethereum-classic",
        "elrond-erd-2", "vechain", "theta-token",
        "filecoin", "ecomi", "tezos",
        "klay-token", "monero", "compound-ether",
        "cdai", "the-graph"
    ];

    useEffect(() => {
        const fetchData = async () => {

            const response = await coinGecko.get("/coins/markets/", {
                params: {
                    vs_currency: "usd",
                    ids: masterList.join(","),
                },
            });
            setAssets(response.data);
            const calcAssetMap= Object.fromEntries(response.data.map(asset => [asset.id, asset]))
            setAssetMap( calcAssetMap);
        };
        fetchData();
    }, []);


    const handleSelectPortfolio = (portfolio) => {
        console.log(`Selected portfolio:`, portfolio);
        const response = { portfolio: portfolio, portfolios: null, isEmpty: false };
        navigate(`/portfolio`, { state: { response } });
    };

    const handleDeletePortfolio = async (portfolio) => {
        try {
            console.log("our url is");
            console.log(`http://localhost:8080/api/portfolio/${portfolio.portfolioId}`);
            const response = await fetch(`http://localhost:8080/api/portfolio/${portfolio.portfolioId}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    "cp-auth-token": getToken(),
                },
            });

            if (response.ok) {
                console.log(`Deleted portfolio with ID: ${portfolio.portfolioId}`);

                // Remove the deleted portfolio from the displayed list
                const updatedPortfolios = portfolios.filter((p) => p.portfolioId !== portfolio.portfolioId);
                setPortfolios(updatedPortfolios);

                // If no portfolios remain, navigate to /CreatePortfolio
                if (updatedPortfolios.length === 0) {
                    const response = { portfolio: null, portfolios: null, isEmpty: true };
                    // it's clear id now, we can use it for creation of new portfolio
                    navigate(`/portfolio`, { state: { response } });
                }
            } else {
                console.error(`Failed to delete portfolio: ${response.statusText}`);
            }
        } catch (error) {
            console.error("Error deleting portfolio:", error);
        }
    };

    const handleCreateNewPortfolio = () => {
        navigate('/create-portfolio', { state: { assets, assetMap } });
    };

    return (
        <div>
            <h3>Select a Portfolio</h3>
            {portfolios.length > 0 ? (
                <ul>
                    {portfolios.map((portfolio) => (
                        <li key={portfolio.portfolioId}>
                            <button
                                onClick={() => handleSelectPortfolio(portfolio)}
                                style={{
                                    backgroundColor: "purple", // Green button for Select
                                    color: "white",
                                    border: "none",
                                    padding: "10px 20px",
                                    borderRadius: "5px",
                                    cursor: "pointer",
                                    marginRight: "10px", // Space between buttons
                                }}
                            >
                                {portfolio.portfolioName + " BUTTON"}
                            </button>
                            <button
                                onClick={() => handleDeletePortfolio(portfolio)}
                                style={{
                                    backgroundColor: "purple", // Red button for Delete
                                    color: "white",
                                    border: "none",
                                    padding: "10px 20px",
                                    borderRadius: "5px",
                                    cursor: "pointer",
                                }}
                            >
                                Delete
                            </button>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No portfolios available. Redirecting...</p>
            )}

            {/* Button to create a new portfolio */}
            <div>
                <button
                    onClick={handleCreateNewPortfolio}
                    style={{
                        marginTop: "20px",
                        backgroundColor: "purple",  // Purple background color
                        color: "white",              // White text color
                        border: "none",              // Remove default border
                        padding: "10px 20px",        // Add padding for better look
                        borderRadius: "5px",         // Rounded corners
                        cursor: "pointer",          // Change cursor to pointer
                    }}
                >
                    Create New Portfolio
                </button>
            </div>
        </div>
    );
};

export default PortfolioSelector;
