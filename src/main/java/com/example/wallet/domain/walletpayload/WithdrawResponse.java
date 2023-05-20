package com.example.wallet.domain.walletpayload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WithdrawResponse {
    @JsonProperty("wallet_transaction_id")
    private int walletTransactionId;
    private int amount;
    @JsonProperty("user_id")
    private int userId;
}
