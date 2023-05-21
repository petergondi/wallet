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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
            ResponsePayload responsePayload=new ResponsePayload();
            RecipientAccountDto recipientAccountDto=recipientAcService.findAc(transferPayload.getAccountId());
            if(recipientAccountDto==null){
                responsePayload.setStatus(TransactionStatus.REJECTED);
                responsePayload.setAmount(transferPayload.getAmount());
                responsePayload.setStatusDescription("Transaction rejected A/C not found!");
                return new ResponseEntity<>(responsePayload, HttpStatus.BAD_REQUEST);
            }
            WithdrawResponse withdrawResponse=transactionService.withDrawWallet(transferPayload);

            if(withdrawResponse!=null){
                //save transaction to database
                TransactionDto savedTransactionDto =transactionService.savePayment(transferPayload,withdrawResponse);
                LOGGER.info("SAVED DETAILS"+ savedTransactionDto);

                //prepare response to client
                responsePayload.setStatus(TransactionStatus.RECEIVED);
                responsePayload.setTransactionId(savedTransactionDto.getWalletTransactionId());
                responsePayload.setAmount(transferPayload.getAmount());
                responsePayload.setStatusDescription("Transaction Accepted for processing");

                //prepare to insert to queue
                //we are updating the new fee amount to be transferred after calculation
                transferPayload.setAmount(savedTransactionDto.getNewAmount());
                transactionService.saveToQueue(savedTransactionDto,transferPayload,recipientAccountDto);
                return new ResponseEntity<>(responsePayload, HttpStatus.CREATED);
            }
            responsePayload.setStatus(TransactionStatus.FAILED);
            responsePayload.setAmount(transferPayload.getAmount());
            responsePayload.setStatusDescription("Transaction Failed to initiate");
            return new ResponseEntity<>(responsePayload, HttpStatus.BAD_REQUEST);

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
