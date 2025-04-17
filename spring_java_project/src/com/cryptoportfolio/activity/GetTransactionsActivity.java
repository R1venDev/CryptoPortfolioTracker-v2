package com.cryptoportfolio.activity;

import com.cryptoportfolio.converter.ModelConverter;
import com.cryptoportfolio.exceptions.TransactionsNotFoundException;
import com.cryptoportfolio.models.TransactionModel;
import com.cryptoportfolio.models.requests.GetTransactionsRequest;
import com.cryptoportfolio.models.responses.GetTransactionsResponse;
import com.cryptoportfolio.postgressDb.dao.TransactionDao;
import com.cryptoportfolio.postgressDb.models.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTransactionsActivity {

    private final Logger log = LogManager.getLogger();
    private final TransactionDao transactionDao;
    private final ModelConverter modelConverter;

    @Autowired
    public GetTransactionsActivity(TransactionDao transactionDao, ModelConverter modelConverter) {
        this.transactionDao = transactionDao;
        this.modelConverter = modelConverter;
    }

    public GetTransactionsResponse execute(GetTransactionsRequest getTransactionsRequest) {
        Long portfolioId = getTransactionsRequest.getPortfolioId();
        // Authenticate the user using the token

        String assetFlag = getTransactionsRequest.getAssetFlag();

        // Retrieve transactions based on username and asset flag
        List<Transaction> transactions = transactionDao.findByPortfolioIdAndAssetId(portfolioId, assetFlag);

        // If no transactions were found, throw exception
        if (transactions == null || transactions.isEmpty()) {
            throw new TransactionsNotFoundException("Resource not found: Could not find Transaction History");
        }

        // Convert the list of Transaction entities to TransactionModel objects
        List<TransactionModel> transactionModelList = modelConverter.toTransactionModelList(transactions);

        // Return the response containing the list of transaction models
        return GetTransactionsResponse.builder()
                .transactions(transactionModelList)
                .build();
    }
}
