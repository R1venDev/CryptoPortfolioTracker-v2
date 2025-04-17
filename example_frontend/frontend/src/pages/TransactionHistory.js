import React, { useState } from 'react';
import { getToken, getUsername, resetUserSession } from '../service/authService';
import TransactionList from "../components/TransactionList";
import DropDownMenu from "../components/DropDownMenu";
import axios from '../apis/cryptoPortfolio';
import { useNavigate, useLocation } from "react-router-dom";
import { APIKey } from "../apis/apiKey";

const TransactionHistory = () => {
    const username = getUsername();
    const token = getToken();
    const navigate = useNavigate();
    const location = useLocation();
    const [message, setMessage] = useState(null);
    const [queryAssetId, setQueryAssetId] = useState('ALL');
    const [transactions, setTransactions] = useState(null);
    const assets = location.state?.assets || [];
    const assetMap = location.state?.assetMap || {};
    const response = location.state?.response
    console.log("here we inside history");
    console.log(response);
    const queryHandler = () => {
        const requestConfig = {
            headers: {
                'x-api-key': APIKey,
                'cp-auth-token': token
            }
        };

        const urlString = `/transactions/${response.portfolio.portfolioId}?assetFlag=${queryAssetId === 'Select an Asset' ? 'ALL' : queryAssetId}`;
        console.log('Request config: ', JSON.stringify(requestConfig));
        console.log('URL: ', urlString);

        axios.get(urlString, requestConfig)
            .then((response) => {
                console.log('Transactions Received')
                console.log(response);
                setTransactions(response.data.transactions);
            })
            .catch((error) => {
                if (error.response.status === 401 || error.response.status === 403) {
                    resetUserSession();
                    navigate('/login'); // Redirect to login on session expiration
                } else {
                    setMessage(error.response?.data?.errorMessage?.split('] ')[1] || 'An error occurred.');
                }
            });
    };

    const backHandler = () => {
        navigate('/portfolio', { state: {response}});
    };

    const logoutHandler = () => {
        resetUserSession();
        navigate('/login'); // Redirect to login
    };

    return (
        <div>
            <div id="alignpage">
                <h5>Transaction History</h5>
                {username}'s Portfolio <br /> <br />
                {transactions && <TransactionList transactions={transactions} assetMap={assetMap} />}
                Transaction Query <br />
            </div>

            <DropDownMenu assets={assets} setAssetId={(e) => setQueryAssetId(e)} /> <br />

            <div id="outer">
                <input className="inner" type="button" value="Transaction Query" onClick={queryHandler} /> <br />
                <input className="inner" type="button" value="Back to Portfolio" onClick={backHandler} /> <br />
                <input className="inner" type="button" value="Logout" onClick={logoutHandler} />
            </div>

            {message && <p className="message">{message}</p>}
        </div>
    );
};

export default TransactionHistory;
