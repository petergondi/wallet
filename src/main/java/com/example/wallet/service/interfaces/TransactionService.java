package com.example.wallet.service.interfaces;

import com.example.wallet.domain.Transaction;
import com.example.wallet.domain.walletpayload.WithdrawRequest;
import com.example.wallet.domain.walletpayload.WithdrawResponse;

public interface TransactionService {
    Transaction createPayment(Transaction transaction);

    WithdrawResponse withDrawWallet(WithdrawRequest withdrawRequest);
}
