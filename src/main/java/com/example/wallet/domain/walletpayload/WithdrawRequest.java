package com.example.wallet.domain.walletpayload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    private BigDecimal amount;
    private Long user_id;

}
