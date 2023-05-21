package com.example.wallet.Domain.AccountPayload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferPayload {
        private Long accountId;
        private Long userId;
        private String currency;
        private BigDecimal amount;
}
