package com.example.wallet.Domain.AccountPayload;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class PaymentInfo {
        private BigDecimal amount;
        private String id;
}
