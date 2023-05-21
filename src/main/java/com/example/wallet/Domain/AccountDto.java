package com.example.wallet.Domain;

import com.example.wallet.Domain.AccountPayload.TransferPayload;
import lombok.Data;

import java.io.Serializable;

@Data
public class AccountDto implements Serializable {
    private Long transactionId;
    private TransferPayload transferPayload;
    private RecipientAccountDto recipientAccountDto;
}
