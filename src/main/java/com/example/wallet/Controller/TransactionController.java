package com.example.wallet.Controller;

import com.example.wallet.Domain.RecipientAccountDto;
import com.example.wallet.Service.RecipientAcService;
import com.example.wallet.Util.TransactionStatus;
import com.example.wallet.Domain.TransactionDto;
import com.example.wallet.Domain.ResponsePayload;
import com.example.wallet.Domain.AccountPayload.TransferPayload;
import com.example.wallet.Domain.WalletPayload.WithdrawResponse;
import com.example.wallet.Service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@RestController
@RequestMapping(path = "/v1/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @Autowired
    RecipientAcService recipientAcService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    /**withdraw from the wallet
      if success save the request to transaction table
      insert the transaction into the queue
    **/
    @PostMapping
    public ResponseEntity<ResponsePayload> accTransfer(@RequestBody TransferPayload transferPayload) {
        try {
            RecipientAccountDto recipientAccountDto = recipientAcService.findAc(transferPayload.getAccountId());

            if (recipientAccountDto == null) {
                return ResponseEntity.badRequest()
                        .body(transactionService.createResponsePayload(TransactionStatus.REJECTED, "Transaction rejected A/C not found!", transferPayload.getAmount()));
            }

            WithdrawResponse withdrawResponse = transactionService.withDrawWallet(transferPayload);

            if (withdrawResponse != null) {
                TransactionDto savedTransactionDto = transactionService.savePayment(transferPayload, withdrawResponse);

                if (savedTransactionDto != null) {
                    ResponsePayload responsePayload = new ResponsePayload();
                    responsePayload.setTransactionId(savedTransactionDto.getWalletTransactionId());
                    transferPayload.setAmount(savedTransactionDto.getNewAmount());
                    boolean insertToQueue = transactionService.saveToQueue(savedTransactionDto, transferPayload, recipientAccountDto);

                    if (insertToQueue) {
                        return ResponseEntity.ok()
                                .body(transactionService.createResponsePayload(TransactionStatus.RECEIVED, "Transaction Accepted for processing!", transferPayload.getAmount()));
                    } else {
                        return ResponseEntity.ok()
                                .body(transactionService.createResponsePayload(TransactionStatus.REFUND, "Transaction failed refund initiated!", transferPayload.getAmount()));
                    }
                } else {
                    return ResponseEntity.ok()
                            .body(transactionService.createResponsePayload(TransactionStatus.REFUND, "Transaction Could not be completed refund initiated!", transferPayload.getAmount()));
                }
            }
            return ResponseEntity.badRequest()
                    .body(transactionService.createResponsePayload(TransactionStatus.FAILED, "Transaction Could not be processed refund initiated!", transferPayload.getAmount()));

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDto>> getFilteredTransactions(
            @RequestParam(name = "amount") BigDecimal amount,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<TransactionDto> transactions = transactionService.getFilteredTransactions(amount, date, page, size);
        return ResponseEntity.ok(transactions);
    }
}
