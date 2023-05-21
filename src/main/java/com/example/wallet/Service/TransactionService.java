package com.example.wallet.Service;

import com.example.wallet.Domain.AccountDto;
import com.example.wallet.Domain.AccountPayload.TransferPayload;
import com.example.wallet.Domain.RecipientAccountDto;
import com.example.wallet.Domain.TransactionDto;
import com.example.wallet.Domain.WalletPayload.WithdrawRequest;
import com.example.wallet.Domain.WalletPayload.WithdrawResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    @Transactional
    TransactionDto savePayment(TransferPayload transferPayload, WithdrawResponse withdrawResponse);

    Page<TransactionDto> getFilteredTransactions(BigDecimal amount, LocalDate date, int page, int size);

    WithdrawResponse withDrawWallet(TransferPayload transferPayload);

    void saveToQueue(TransactionDto transactionDto, TransferPayload transferPayload, RecipientAccountDto recipientAccountDto);

    void transferToaccount(AccountDto accountDTO);
}
