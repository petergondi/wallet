package com.example.wallet.service.interfaces;

import com.example.wallet.domain.Transaction;
import com.example.wallet.domain.request.WithdrawRequest;
import com.example.wallet.domain.request.WithdrawResponse;

public interface TransactionService {
    Transaction createPayment(Transaction transaction);

    WithdrawResponse withDrawWallet(WithdrawRequest withdrawRequest);
}
