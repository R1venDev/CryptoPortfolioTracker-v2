import React, { useState, useEffect } from "react";
import axios from "axios";
import { Line } from "react-chartjs-2";
import PortfolioListAsset from "./PortfolioListAsset";

const PortfolioList = ({ assets = [], assetQuantityMap = {} }) => {
    const [showPLGraph, setShowPLGraph] = useState(false);
    const [plGraphData, setPLGraphData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [timeRange, setTimeRange] = useState("365"); // Default to 1 year

    // Fetch portfolio-level data (P&L graph)
    const fetchPLGraphData = async () => {
        try {
            setLoading(true);

            const graphData = await Promise.all(
                assets.map((asset) =>
                    axios
                        .get(
                            `https://api.coingecko.com/api/v3/coins/${asset.id}/market_chart`,
                            {
                                params: {
                                    vs_currency: "usd",
                                    days: timeRange,
                                },
                            }
                        )
                        .then((response) => ({
                            id: asset.id,
                            prices: response.data.prices,
                        }))
                )
            );

            const combinedPrices = graphData[0].prices.map(([timestamp]) => {
                const date = new Date(timestamp).toLocaleDateString();
                const totalValue = graphData.reduce((sum, { id, prices }) => {
                    const assetPrice = prices.find(([t]) => t === timestamp)?.[1] || 0;
                    const quantity = assetQuantityMap[id] || 0;
                    return sum + quantity * assetPrice;
                }, 0);
                return { date, totalValue };
            });

            const labels = combinedPrices.map(({ date }) => date);
            const data = combinedPrices.map(({ totalValue }) => totalValue);

            setPLGraphData({
                labels,
                datasets: [
                    {
                        label: `Portfolio Value (USD) - Last ${timeRange} days`,
                        data,
                        borderColor: "rgba(54, 162, 235, 1)",
                        backgroundColor: "rgba(54, 162, 235, 0.2)",
                        fill: true,
                        tension: 0.4,
                    },
                ],
            });

            setLoading(false);
        } catch (error) {
            console.error("Error fetching portfolio graph data:", error);
            setLoading(false);
        }
    };

    const togglePLGraph = () => {
        if (!showPLGraph) {
            fetchPLGraphData();
        }
        setShowPLGraph(!showPLGraph);
    };

    const handleTimeRangeChange = (range) => {
        setTimeRange(range);
        if (showPLGraph) {
            fetchPLGraphData(); // Fetch new data when the range changes if the graph is open
        }
    };

    return (
        <div>
            <div className="d-flex justify-content-center mb-3">
                <select
                    className="form-select form-select-sm me-2"
                    value={timeRange}
                    onChange={(e) => handleTimeRangeChange(e.target.value)}
                >
                    <option value="1">Day</option>
                    <option value="30">Month</option>
                    <option value="365">Year</option>
                </select>
                <button className="btn btn-sm btn-primary" onClick={togglePLGraph}>
                    {showPLGraph ? "Hide P&L Graph" : "Show P&L Graph"}
                </button>
            </div>

            {showPLGraph && (
                <div className="mb-4">
                    {loading ? (
                        <p>Loading P&L Graph...</p>
                    ) : (
                        plGraphData && <Line data={plGraphData} options={{ responsive: true }} />
                    )}
                </div>
            )}

            <ul className="list-group mt-2">
                <li className="assetlist-item list-group-item d-flex justify-content-between align-items-center">
                    <span className="portfolio-asset-item"></span>
                    <span className="portfolio-asset-item">Asset</span>
                    <span className="portfolio-asset-item">Quantity</span>
                    <span className="portfolio-asset-item">Value</span>
                </li>
                {assets.map((asset) => (
                    <PortfolioListAsset
                        key={asset.id}
                        asset={asset}
                        assetQuantityMap={assetQuantityMap}
                        timeRange={timeRange} // Pass the time range to individual assets
                    />
                ))}
            </ul>
        </div>
    );
};

export default PortfolioList;
