package com.example.wallet.domain.walletpayload;

import lombok.Data;

@Data
public class WithdrawRequest {
    private Double amount;
    private Long user_id;

}
