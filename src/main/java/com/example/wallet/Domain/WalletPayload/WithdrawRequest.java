package com.example.wallet.Domain.WalletPayload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    private BigDecimal amount;
    private Long user_id;

}
