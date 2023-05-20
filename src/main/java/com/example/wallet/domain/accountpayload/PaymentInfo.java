package com.example.wallet.domain.accountpayload;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class PaymentInfo {
        private BigDecimal amount;
        private String id;
}
