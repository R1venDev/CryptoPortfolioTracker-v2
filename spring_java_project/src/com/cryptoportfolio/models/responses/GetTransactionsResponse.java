package com.cryptoportfolio.models.responses;
import com.cryptoportfolio.models.TransactionModel;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetTransactionsResponse {
    private List<TransactionModel> transactions;
}
