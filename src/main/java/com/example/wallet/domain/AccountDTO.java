package com.example.wallet.domain;

import com.example.wallet.domain.accountpayload.TransferPayload;
import lombok.Data;

import java.io.Serializable;

@Data
public class AccountDTO implements Serializable {
    private Long transactionId;
    private TransferPayload transferPayload;
}
