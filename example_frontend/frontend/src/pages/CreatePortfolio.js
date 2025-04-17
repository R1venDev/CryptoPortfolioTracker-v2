import React, { useState } from 'react';

import { getToken, getUsername, setNewUser, resetUserSession } from '../service/authService';
import DropDownMenu from '../components/DropDownMenu';
import PortfolioList from '../components/PortfolioList';
import axios from '../apis/cryptoPortfolio';
import { useNavigate, useLocation} from "react-router-dom";
import { APIKey } from "../apis/apiKey";

const CreatePortfolio = (props) => {
    const token = getToken();
    const [username, setUsername] = useState(getUsername());
    const [assetId, setAssetId] = useState('');
    const [quantity, setQuantity] = useState('');
    const [message, setMessage] = useState(null);
    const [assetQuantityMap, setAssetQuantityMap] = useState({});
    const [transactions, setTransactions] = useState([]);
    const location = useLocation();
    const assets = location.state?.assets;
    const assetMap = location.state?.assetMap;

    const navigate = useNavigate();  // Use react-router's useNavigate for navigation
    console.log("we are inside createPortfolio");
    console.log(assets);
    console.log(assetMap);
    const addAssetHandler = (event) => {
        event.preventDefault();
        if (assetId.trim() === '' || quantity.trim() === '') {
            setMessage('All fields are required');
            return;
        }
        setMessage(null);

        if (quantity > 0 && !assetQuantityMap[assetId]) {
            const newTransaction = {
                username: username,
                transactionDate: new Date().toISOString(),
                assetId: assetId,
                transactionType: "BUY",
                assetQuantity: quantity,
                transactionValue: assetMap[assetId].current_price * quantity
            };

            setTransactions(transactions => [...transactions, newTransaction]);

            const updatedValue = {};
            updatedValue[assetId] = quantity;
            setAssetQuantityMap(assetQuantityMap => ({
                ...assetQuantityMap,
                ...updatedValue
            }));
        }
    };

    const createPortfolioHandler = (event) => {
        const requestConfig = {
            headers: {
                'x-api-key': APIKey,
                'cp-auth-token': token
            }
        };

        const updated = transactions.map(transaction => {
            const assetQuantity = transaction.assetQuantity;  // Ensure this is a field in each transaction
            const transactionValue = transaction.transactionValue;  // Ensure this is a field in each transaction

            // If both values are available, calculate price and set it along with quantity
            if (assetQuantity && assetQuantity > 0 && transactionValue) {
                transaction.price = transactionValue;  // Set the price
                transaction.quantity = assetQuantity;  // Set quantity
            }

            return transaction;
        });

        const requestBody = {
            username: username,
            assets: assets,
            assetQuantityMap: assetQuantityMap,
            transactions: updated,
            authToken: getToken()
        };


        console.log('Request config', JSON.stringify(requestConfig));
        console.log('Request body', JSON.stringify(requestBody));

        axios.post('/portfolio', requestBody, requestConfig).then((receivedResponse) => {
            console.log('Portfolio Created');
            console.log(receivedResponse);
            const response = {portfolio: receivedResponse.data.portfolio, portfolios: null, isEmpty: false};
            console.log(response);
            navigate(`/portfolio`,  { state: {response} }); // Redirect to the newly created portfolio
        }).catch((error) => {
            if (error.response.status === 401 || error.response.status === 403) {
                resetUserSession();
                //props.logout();
                navigate('/login'); // Redirect to Login view
            } else {
                setMessage(error.response.data.errorMessage.split('] ')[1]);
            }
        });
    };

    const backHandler = () => {
        navigate('/portfolio'); // Redirect back to Portfolio view
    };

    const logoutHandler = () => {
        resetUserSession();
        navigate('/login'); // Redirect to Login view
    };

    return (
        <div>
            <div id="alignpage">
                <h5>Create Portfolio</h5>
                {username}'s Portfolio <br /> <br />
            </div>

            {Object.keys(assetQuantityMap).length > 0 && (
                <PortfolioList assets={assets.filter(asset => assetQuantityMap[asset.id])} assetQuantityMap={assetQuantityMap} />
            )}
            <DropDownMenu assets={assets} setAssetId={(e) => setAssetId(e)} />

            <div id="alignpage">
                Quantity: <input className="qfield" type="text" value={quantity} onChange={event => setQuantity(event.target.value)} /> <br /> <br />
            </div>
            <div id="outer">
                <input className="inner" type="button" onClick={addAssetHandler} value="Add Asset" /> <br />
                <input className="inner" type="button" onClick={createPortfolioHandler} value="Create Portfolio" />
                <input className="inner" type="button" value="Back to Portfolio" onClick={backHandler} /> <br />
                <input className="inner" type="button" value="Logout" onClick={logoutHandler} />
            </div>
            {message && <p className="message">{message}</p>}
        </div>
    );
};

export default CreatePortfolio;
