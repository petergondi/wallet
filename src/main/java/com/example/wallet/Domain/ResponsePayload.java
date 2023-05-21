package com.example.wallet.Domain;

import com.example.wallet.Util.TransactionStatus;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ResponsePayload {
    private Long transactionId;
    private TransactionStatus status;
    private BigDecimal amount;
    private String statusDescription;
}
