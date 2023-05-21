package com.example.wallet.Service;

import com.example.wallet.Domain.AccountDto;
import com.example.wallet.Domain.TransactionDto;
import com.example.wallet.Domain.WalletPayload.WithdrawRequest;
import com.example.wallet.Domain.WalletPayload.WithdrawResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionDto savePayment(TransactionDto transactionDTO);

    List<TransactionDto> getTransactions();

    Page<TransactionDto> getFilteredTransactions(BigDecimal amount, LocalDate date, int page, int size);

    WithdrawResponse withDrawWallet(WithdrawRequest withdrawRequest);

    void saveToQueue(AccountDto accountDTO);

    void transferToaccount(AccountDto accountDTO);
}
