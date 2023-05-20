package com.example.wallet.service;

import com.example.wallet.domain.AccountDTO;
import com.example.wallet.domain.TransactionDTO;
import com.example.wallet.domain.walletpayload.WithdrawRequest;
import com.example.wallet.domain.walletpayload.WithdrawResponse;

public interface TransactionService {

    TransactionDTO savePayment(TransactionDTO transactionDTO);

    WithdrawResponse withDrawWallet(WithdrawRequest withdrawRequest);

    void saveToQueue(AccountDTO accountDTO);
}
