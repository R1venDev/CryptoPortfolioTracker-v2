import React, { useState } from 'react';
import { getToken, getUsername, resetUserSession } from '../service/authService';
import DropDownMenu from '../components/DropDownMenu';
import PortfolioList from '../components/PortfolioList';
import axios from '../apis/cryptoPortfolio';
import { APIKey } from '../apis/apiKey';
import { useNavigate, useLocation } from 'react-router-dom';

const UpdatePortfolio = () => {
    const token = getToken();
    const navigate = useNavigate();
    const location = useLocation();

    const [username] = useState(getUsername());
    const [assetId, setAssetId] = useState('');
    const [quantity, setQuantity] = useState('');
    const [message, setMessage] = useState(null);
    const [transactions, setTransactions] = useState([]);

    const assets = location.state?.assets || [];
    const assetMap = location.state?.assetMap || {};
    const [assetQuantityMap, setAssetQuantityMap] = useState(location.state?.assetQuantityMap || {});
    const receivedResponse = location.state?.response;
    console.log("here are incoming response");
    console.log(receivedResponse);

    const addAssetHandler = (event) => {
        event.preventDefault();
        if (!assetId.trim() || !quantity.trim()) {
            setMessage('All fields are required');
            return;
        }
        setMessage(null);
        console.log(assetId);
        console.log(assetQuantityMap[assetId]);
        console.log(quantity);
        if (quantity > 0 && !assetQuantityMap[assetId]) {
            const newTransaction = {
                username: username,
                portfolioId: receivedResponse?.portfolio?.portfolioId,
                assetId: assetId,
                price: assetMap[assetId]?.current_price * quantity,
                transactionType: 'BUY',
                quantity: quantity
            };

            setTransactions((prevTransactions) => [...prevTransactions, newTransaction]);

            setAssetQuantityMap((prevMap) => ({
                ...prevMap,
                [assetId]: quantity,
            }));
        }
        console.log(transactions);
        console.log(assetQuantityMap);
        console.log("vot tak vod dobavili resurse");
    };

    const updateAssetHandler = (event) => {
        event.preventDefault();
        if (!assetId.trim() || !quantity.trim()) {
            setMessage('All fields are required');
            return;
        }
        setMessage(null);

        const quantityTransacted = Math.abs(quantity - assetQuantityMap[assetId]);
        if (quantityTransacted < 1e-9) return;

        const newTransaction = {
            username: username,
            portfolioId: receivedResponse?.portfolio?.portfolioId,
            assetId: assetId,
            price: assetMap[assetId]?.current_price * quantityTransacted,
            transactionType: quantity > assetQuantityMap[assetId] ? 'BUY' : 'SELL',
            quantity: quantityTransacted,
        };
        console.log(newTransaction);
        console.log("new transaction ");
        setTransactions((prevTransactions) => [...prevTransactions, newTransaction]);

        setAssetQuantityMap((prevMap) => {
            const updatedMap = { ...prevMap };
            if (quantity <= 0) {
                delete updatedMap[assetId];
            } else {
                updatedMap[assetId] = quantity;
            }
            return updatedMap;
        });
        console.log(transactions);
        console.log(assetQuantityMap);
        console.log("finish logging of addAsset Method");
    };

    const updatePortfolioHandler = () => {
        const requestConfig = {
            headers: {
                'x-api-key': APIKey,
                'cp-auth-token': token,
            },
        };

        const requestBody = {
            username,
            assetQuantityMap,
            transactions,
        };

        axios.put(`/portfolio/${receivedResponse?.portfolio?.portfolioId}`, requestBody, requestConfig)
            .then(() => {
                console.log('Portfolio Updated');
                const portfolio = {portfolioId: receivedResponse?.portfolio?.portfolioId, username: receivedResponse?.portfolio?.username, assetQuantityMap: assetQuantityMap, transactions:transactions}
                const response = {portfolio: portfolio, portfolios: null, isEmpty: false};
                console.log("here are updated porfolio");
                console.log(portfolio)
                console.log(response);
                navigate('/portfolio', { state: {response}});
            })
            .catch((error) => {
                resetUserSession();
                navigate('/login');
            });
    };

    const logoutHandler = () => {
        resetUserSession();
        navigate('/login');
    };

    return (
        <div>
            <div id="alignpage">
                <h5>Update Portfolio</h5>
                {username}'s Portfolio <br /> <br />
            </div>

            <PortfolioList
                assets={assets.filter((asset) => assetQuantityMap[asset.id])}
                assetQuantityMap={assetQuantityMap}
            />
            <DropDownMenu assets={assets} setAssetId={(e) => setAssetId(e)} />

            <div id="alignpage">
                Quantity: <input
                className="qfield"
                type="text"
                value={quantity}
                onChange={(event) => setQuantity(event.target.value)}
            />{' '}
                <br /> <br />
            </div>
            <div id="outer">
                <input
                    className="inner"
                    type="button"
                    onClick={addAssetHandler}
                    value="Add Asset"
                />
                <input
                    className="inner"
                    type="button"
                    onClick={updateAssetHandler}
                    value="Update Asset"
                />
                <input
                    className="inner"
                    type="button"
                    onClick={updatePortfolioHandler}
                    value="Update Portfolio"
                />
                <input
                    className="inner"
                    type="button"
                    value="Logout"
                    onClick={logoutHandler}
                />
            </div>
            {message && <p className="message">{message}</p>}
        </div>
    );
};

export default UpdatePortfolio;
