package com.example.wallet.controller;

import com.example.wallet.domain.Transaction;
import com.example.wallet.domain.accountpayload.ResponsePayload;
import com.example.wallet.domain.accountpayload.TransferPayload;
import com.example.wallet.domain.walletpayload.WithdrawRequest;
import com.example.wallet.domain.walletpayload.WithdrawResponse;
import com.example.wallet.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @PostMapping
    public ResponseEntity<ResponsePayload> accTransfer(@RequestBody TransferPayload transferPayload) {
        try {
            WithdrawRequest withdrawRequest=new WithdrawRequest();
            withdrawRequest.setAmount(transferPayload.getAmount());
            withdrawRequest.setUser_id(transferPayload.getUserId());
            WithdrawResponse withdrawResponse=transactionService.withDrawWallet(withdrawRequest);
            if(withdrawResponse!=null){
                Transaction transaction=new Transaction();
                transaction.setAmount(transferPayload.getAmount());
                transaction.setUser_id(transferPayload.getUserId());
                transaction.setWallet_transaction_id(withdrawResponse.getWalletTransactionId());
                transaction.setStatus("RECEIVED");
                transactionService.savePayment(transaction);

            }
            //withdraw from the wallet
            //if success save the request to transaction table
            //insert the transaction into the queue
            //
            return new ResponseEntity<>(saveUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
