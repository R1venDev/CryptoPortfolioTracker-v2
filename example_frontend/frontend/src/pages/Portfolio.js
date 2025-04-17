import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from '../apis/cryptoPortfolio';
import axios2 from '../apis/coinGecko';
import { getToken, resetUserSession } from "../service/authService";
import PortfolioList from "../components/PortfolioList";
import PortfolioChart from "../components/PortfolioChart"; // You can still keep this if needed.
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

const calculateCombinedPriceDifferences = (transactions, assetMap) => {
    if (!transactions || !assetMap) return { totalValue: 0, history: {} };

    const allDates = new Set();
    const assetData = {};
    const history = {}; // To store the history of changes

    Object.entries(transactions).forEach(([assetId, assetTransactions]) => {
        if (assetTransactions) {
            assetTransactions.forEach(transaction => {
                allDates.add(transaction.transactionDate);
            });
        }
    });

    const sortedDates = Array.from(allDates).sort((a, b) => new Date(a) - new Date(b));

    Object.keys(transactions).forEach(assetId => {
        assetData[assetId] = {
            cumulativeQuantity: 0,
            weightedPrice: 0,
        };
        history[assetId] = []; // Initialize history for each asset
    });

    let totalValue = 0;

    sortedDates.forEach(date => {
        Object.entries(transactions).forEach(([assetId, assetTransactions]) => {
            const asset = assetMap[assetId];
            if (!asset) return;

            let quantityChange = 0;
            let weightedPriceChange = 0;

            assetTransactions?.forEach(transaction => {
                if (transaction.transactionDate === date) {
                    const assetQuantity = transaction.assetQuantity;
                    const priceForThatQuantity = transaction.transactionValue;
                    const fullAssetPrice = priceForThatQuantity / assetQuantity;
                    const transactionType = transaction.transactionType;

                    if (transactionType === "BUY") {
                        quantityChange += assetQuantity;
                        weightedPriceChange += (fullAssetPrice * assetQuantity);
                    } else {
                        quantityChange -= assetQuantity;
                        weightedPriceChange -= (fullAssetPrice * assetQuantity);
                    }
                }
            });

            const prevQuantity = assetData[assetId].cumulativeQuantity;
            const prevWeightedPrice = assetData[assetId].weightedPrice;

            const newQuantity = prevQuantity + quantityChange;
            let newWeightedPrice = 0;

            if (newQuantity === 0) {
                newWeightedPrice = 0;
            } else {
                newWeightedPrice = (weightedPriceChange + (prevWeightedPrice * prevQuantity)) / newQuantity;
            }

            assetData[assetId] = {
                cumulativeQuantity: newQuantity,
                weightedPrice: newWeightedPrice,
            };

            // Record the state in the history
            history[assetId].push({
                date,
                cumulativeQuantity: newQuantity,
                weightedPrice: newWeightedPrice,
            });
        });
    });

    Object.entries(assetData).forEach(([assetId, data]) => {
        const asset = assetMap[assetId];
        const cumulativeQuantity = data.cumulativeQuantity;
        const weightedPrice = data.weightedPrice;

        if (cumulativeQuantity > 0 && weightedPrice > 0) {
            totalValue += cumulativeQuantity * weightedPrice;
        }
    });

    return { totalValue, history };
};

const Portfolio = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const response = location.state?.response;

    const [assets, setAssets] = useState(null);
    const [assetMap, setAssetMap] = useState(null);
    const [assetQuantityMap, setAssetQuantityMap] = useState(null);
    const [transactions, setTransactions] = useState(null);
    const [resourceHistory, setResourceHistory] = useState(null);
    const [assetDataHistory, setAssetDataHistory] = useState(null); // New state for asset data history
    const [message, setMessage] = useState(null);
    const [loading, setLoading] = useState(false);
    const [pnl, setPnl] = useState(null);
    const [percentageDiff, setPercentageDiff] = useState(null);

    const masterList = [
        "bitcoin", "ethereum", "tether", "binancecoin", "usd-coin", "ripple", "cardano", "solana",
        "avalanche-2", "terra-luna", "polkadot", "dogecoin", "binance-usd", "shiba-inu", "matic-network",
        "crypto-com-chain", "terrausd", "wrapped-bitcoin", "dai", "cosmos", "litecoin", "chainlink",
        "near", "tron", "ftx-token", "algorand", "bitcoin-cash", "staked-ether", "okb", "stellar",
        "leo-token", "fantom", "uniswap", "decentraland", "hedera-hashgraph", "internet-computer",
        "the-sandbox", "axie-infinity", "ethereum-classic", "elrond-erd-2", "vechain", "theta-token",
        "filecoin", "ecomi", "tezos", "klay-token", "monero", "compound-ether", "cdai", "the-graph"
    ];

    const fetchDataWithRetry = async (retries = 3, delay = 60000) => {
        try {
            setLoading(true);
            const { data } = await axios2.get("/coins/markets/", {
                params: { vs_currency: "usd", ids: masterList.join(",") },
                headers: { "Content-Type": "application/json" }
            });
            setAssets(data);
            setAssetMap(Object.fromEntries(data.map(asset => [asset.id, asset]))); // Creating the map of assets
            setLoading(false);
        } catch (error) {
            if (retries > 0) {
                setMessage("Retrying to fetch data...");
                setTimeout(() => fetchDataWithRetry(retries - 1, delay), delay);
            } else {
                setMessage("Failed to fetch asset data. Please try again later.");
                setLoading(false);
            }
        }
    };

    const fetchTransactions = async () => {
        const APIKey = "YOUR_API_KEY"; // Replace with actual API key
        const token = getToken();
        const requestConfig = {
            headers: {
                'x-api-key': APIKey,
                'cp-auth-token': token
            }
        };

        if (!assets || !response) {
            setMessage("No assets or portfolio data found.");
            return;
        }

        try {
            const assetTransactions = {};
            for (const asset of assets) {
                if (assetQuantityMap[asset.id]) {
                    const urlString = `/transactions/${response.portfolio.portfolioId}?assetFlag=${asset.id}`;
                    const { data } = await axios.get(urlString, requestConfig);
                    assetTransactions[asset.id] = data.transactions;
                }
            }
            setTransactions(assetTransactions);
            setMessage("");
        } catch (error) {
            console.log(error);
            if (error.response?.status === 401 || error.response?.status === 403) {
                resetUserSession();
                navigate("/login");
            } else {
                setMessage(error.response?.data?.errorMessage?.split("] ")[1] || "An error occurred.");
            }
        }
    };

    useEffect(() => {
        if (transactions && assetMap && assetQuantityMap) {
            const { totalValue, history } = calculateCombinedPriceDifferences(transactions, assetMap);
            setAssetDataHistory(history);

            let totalCurrentValue = 0;
            Object.keys(assetQuantityMap).forEach(assetId => {
                const asset = assetMap[assetId];
                const quantity = assetQuantityMap[assetId];
                totalCurrentValue += asset.current_price * quantity;
            });

            const pnlValue = totalValue - totalCurrentValue;
            setPnl(pnlValue);

            const percentageDiff = totalCurrentValue !== 0 ? (pnlValue / totalCurrentValue) * 100 : 0;
            setPercentageDiff(percentageDiff);
        }
    }, [transactions, assetMap, assetQuantityMap]);

    useEffect(() => {
        fetchDataWithRetry();
    }, []);

    useEffect(() => {
        if (response?.portfolio?.assetQuantityMap) {
            setAssetQuantityMap(response.portfolio.assetQuantityMap);
            fetchTransactions();
        }
    }, [response, assets]);

    const showResourceHistory = (asset) => {
        const assetTransactions = transactions[asset.id];
        if (!assetTransactions) return;

        const historyData = [];
        let cumulativeQuantity = 0;

        assetTransactions.forEach(transaction => {
            const quantityChange = transaction.transactionType === "BUY" ? transaction.assetQuantity : -transaction.assetQuantity;
            cumulativeQuantity += quantityChange;
            historyData.push({
                date: transaction.transactionDate,
                quantity: cumulativeQuantity
            });
        });

        setResourceHistory(historyData);
    };

    const createHandler = () => navigate("/create-portfolio", { state: { assets, assetMap } });
    const updateHandler = () => navigate("/updatePortfolio", { state: { assets, assetMap, assetQuantityMap, response } });
    const transactionHandler = () => navigate("/transactionHistory", { state: { assets: assets.filter(asset => assetQuantityMap[asset.id]), assetMap, response } });
    const logoutHandler = () => {
        resetUserSession();
        navigate("/login");
    };

    return (
        <div>
            {loading && <div className="loading-spinner">Loading...</div>}
            {pnl !== null && (
                <>
                    <p
                        style={{
                            color: pnl >= 0 ? 'green' : 'red',
                            fontWeight: 'bold'
                        }}
                    >
                        P&L: ${pnl.toFixed(2)}
                    </p>
                    <p
                        style={{
                            color: percentageDiff >= 0 ? 'green' : 'red',
                            fontWeight: 'bold'
                        }}
                    >
                        Percentage Difference: {percentageDiff >= 0 ? '+' : ''}{percentageDiff.toFixed(2)}%
                    </p>
                </>
            )}
            {assetDataHistory && (
                <div className="asset-history">
                    <h3>Asset History</h3>
                    {Object.entries(assetDataHistory).map(([assetId, history]) => {
                        const chartData = {
                            labels: history.map(entry => entry.date),
                            datasets: [{
                                label: `Cumulative Quantity of ${assetMap[assetId]?.name || assetId}`,
                                data: history.map(entry => entry.cumulativeQuantity),
                                borderColor: 'rgb(75, 192, 192)',
                                tension: 0.1,
                            }]
                        };

                        return (
                            <div key={assetId}>
                                <h4>{assetMap[assetId]?.name || assetId}</h4>
                                <Line data={chartData} />
                            </div>
                        );
                    })}
                </div>
            )}
            {assets && assetQuantityMap && (
                <>
                    <PortfolioChart assets={assets.filter(asset => assetQuantityMap[asset.id])} assetQuantityMap={assetQuantityMap} />
                    <PortfolioList
                        assets={assets.filter(asset => assetQuantityMap[asset.id])}
                        assetQuantityMap={assetQuantityMap}
                        showResourceHistory={showResourceHistory}
                    />
                </>
            )}
            {resourceHistory && <div className="resource-history">
                <h3>Resource History</h3>
                <ul>
                    {resourceHistory.map((entry, index) => (
                        <li key={index}>{entry.date}: {entry.quantity}</li>
                    ))}
                </ul>
            </div>}
            {!assets && !assetQuantityMap && !loading && <div>Loading...</div>}
            <div id="outer">
                {response?.isEmpty && <input className="inner" type="button" value="Create Portfolio" onClick={createHandler} />}
                {!response?.isEmpty && (
                    <>
                        <input className="inner" type="button" value="Update Portfolio" onClick={updateHandler} />
                        <input className="inner" type="button" value="Transaction History" onClick={transactionHandler} />
                    </>
                )}
                <input className="inner" type="button" value="Logout" onClick={logoutHandler} />
            </div>
            {message && <p className="message">{message}</p>}
        </div>
    );
};

export default Portfolio;
