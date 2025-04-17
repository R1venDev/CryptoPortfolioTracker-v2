import React from 'react';

const Transaction = ({transaction, assetName}) => {
    return (
        <li className="assetlist-item list-group-item d-flex justify-content-between align-items-center">
            <span className="transaction-item">{new Date(transaction.transactionDate).toLocaleString()}</span>
            <span className="transaction-item">{assetName}</span>
            <span className="transaction-item">{transaction.assetQuantity}</span>
            <span className="transaction-item">{transaction.transactionValue}</span>
            <span className="transaction-item">{transaction.transactionType}</span>
        </li>
    );
};

export default Transaction;
