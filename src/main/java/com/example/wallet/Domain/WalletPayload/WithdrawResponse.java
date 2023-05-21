package com.example.wallet.Domain.WalletPayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawResponse {
    @JsonProperty("wallet_transaction_id")
    private Long walletTransactionId;
    private BigDecimal amount;
    @JsonProperty("user_id")
    private Long userId;
}
