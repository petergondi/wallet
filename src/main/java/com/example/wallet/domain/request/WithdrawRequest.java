package com.example.wallet.domain.request;

import lombok.Data;

@Data
public class WithdrawRequest {
    private Double amount;
    private Long user_id;

}
