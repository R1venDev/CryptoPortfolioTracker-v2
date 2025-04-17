import React, { useState, useEffect } from "react";
import axios from "axios";
import { Line } from "react-chartjs-2";

const PortfolioListAsset = ({ asset, assetQuantityMap, timeRange }) => {
    const [showGraph, setShowGraph] = useState(false);
    const [graphData, setGraphData] = useState(null);
    const [loading, setLoading] = useState(false);

    // Fetch graph data for the individual asset
    const fetchGraphData = async () => {
        try {
            setLoading(true);
            const response = await axios.get(
                `https://api.coingecko.com/api/v3/coins/${asset.id}/market_chart`,
                {
                    params: {
                        vs_currency: "usd",
                        days: timeRange,
                    },
                }
            );

            const prices = response.data.prices;
            const labels = prices.map((price) =>
                new Date(price[0]).toLocaleDateString()
            );
            const data = prices.map((price) => price[1]);

            setGraphData({
                labels,
                datasets: [
                    {
                        label: `${asset.name} Price (USD) - Last ${timeRange} days`,
                        data,
                        borderColor: "rgba(75, 192, 192, 1)",
                        backgroundColor: "rgba(75, 192, 192, 0.2)",
                        fill: true,
                        tension: 0.4,
                    },
                ],
            });
            setLoading(false);
        } catch (error) {
            console.error("Error fetching graph data:", error);
            setLoading(false);
        }
    };

    const toggleGraph = () => {
        if (!showGraph) {
            fetchGraphData();
        }
        setShowGraph(!showGraph);
    };

    useEffect(() => {
        if (showGraph) {
            fetchGraphData(); // Re-fetch data when the time range changes
        }
    }, [timeRange]); // Re-fetch when the timeRange prop changes

    const totalValue = (assetQuantityMap[asset.id] * asset.current_price).toFixed(2);

    return (
        <li className="assetlist-item list-group-item d-flex flex-column align-items-start">
            <div className="d-flex justify-content-between w-100 align-items-center">
                <span className="portfolio-asset-item">
                    <img className="assetlist-image" src={asset.image} alt={asset.name} />
                </span>
                <span className="portfolio-asset-item">{asset.name}</span>
                <span className="portfolio-asset-item">{assetQuantityMap[asset.id]}</span>
                <span className="portfolio-asset-item">${totalValue}</span>
                <button
                    className="btn btn-sm btn-primary"
                    onClick={toggleGraph}
                >
                    {showGraph ? "Hide Graph" : "Show Graph"}
                </button>
            </div>
            {showGraph && (
                <div className="w-100 mt-3">
                    {loading ? (
                        <p>Loading...</p>
                    ) : (
                        graphData && <Line data={graphData} options={{ responsive: true }} />
                    )}
                </div>
            )}
        </li>
    );
};

export default PortfolioListAsset;
