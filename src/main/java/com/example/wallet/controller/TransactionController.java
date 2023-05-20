package com.example.wallet.controller;

import com.example.wallet.Util.TransactionStatus;
import com.example.wallet.domain.AccountDTO;
import com.example.wallet.domain.TransactionDTO;
import com.example.wallet.domain.ResponsePayload;
import com.example.wallet.domain.accountpayload.TransferPayload;
import com.example.wallet.domain.walletpayload.WithdrawRequest;
import com.example.wallet.domain.walletpayload.WithdrawResponse;
import com.example.wallet.service.TransactionService;
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
@RequestMapping(path = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    /**withdraw from the wallet
      if success save the request to transaction table
      insert the transaction into the queue
    **/
    @PostMapping
    public ResponseEntity<ResponsePayload> accTransfer(@RequestBody TransferPayload transferPayload) {
        try {
            WithdrawRequest withdrawRequest=new WithdrawRequest();
            withdrawRequest.setAmount(transferPayload.getAmount());
            withdrawRequest.setUser_id(transferPayload.getUserId());
            WithdrawResponse withdrawResponse=transactionService.withDrawWallet(withdrawRequest);
            ResponsePayload responsePayload=new ResponsePayload();
            if(withdrawResponse!=null){
                //save transaction to database
                TransactionDTO transactionDTO =new TransactionDTO();
                transactionDTO.setAmount(transferPayload.getAmount());
                transactionDTO.setUser_id(transferPayload.getUserId());
                transactionDTO.setWallet_transaction_id(withdrawResponse.getWalletTransactionId());
                transactionDTO.setStatus(TransactionStatus.RECEIVED);
                TransactionDTO savedTransactionDTO=transactionService.savePayment(transactionDTO);
                LOGGER.info("SAVED DETAILS"+savedTransactionDTO);

                //prepare response to client
                responsePayload.setStatus(TransactionStatus.RECEIVED);
                responsePayload.setTransactionId(savedTransactionDTO.getWallet_transaction_id());
                responsePayload.setAmount(transferPayload.getAmount());
                responsePayload.setStatusDescription("Transaction Accepted for processing");

                //prepare to insert to queue
                transferPayload.setAmount(savedTransactionDTO.getNewamount());
                AccountDTO accountDTO=new AccountDTO();
                accountDTO.setTransactionId(savedTransactionDTO.getWallet_transaction_id());
                accountDTO.setTransferPayload(transferPayload);

                transactionService.saveToQueue(accountDTO);
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
    public ResponseEntity<Page<TransactionDTO>> getFilteredTransactions(
            @RequestParam(name = "amount") BigDecimal amount,
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<TransactionDTO> transactions = transactionService.getFilteredTransactions(amount, date, page, size);
        return ResponseEntity.ok(transactions);
    }
}
