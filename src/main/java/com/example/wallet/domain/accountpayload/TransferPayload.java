package com.example.wallet.domain.accountpayload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferPayload {
        private String accountNo;
        private String routingNo;
        private String firstName;
        private String lastName;
        private String nationalId;
        private Long userId;
        private String currency;
        private BigDecimal amount;
}
