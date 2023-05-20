package com.example.wallet.service;

import com.example.wallet.domain.AccountDTO;
import com.example.wallet.domain.TransactionDTO;
import com.example.wallet.domain.walletpayload.WithdrawRequest;
import com.example.wallet.domain.walletpayload.WithdrawResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionDTO savePayment(TransactionDTO transactionDTO);

    List<TransactionDTO> getTransactions();

    Page<TransactionDTO> getFilteredTransactions(BigDecimal amount, LocalDate date, int page, int size);

    WithdrawResponse withDrawWallet(WithdrawRequest withdrawRequest);

    void saveToQueue(AccountDTO accountDTO);

    void transferToaccount(AccountDTO accountDTO);
}
