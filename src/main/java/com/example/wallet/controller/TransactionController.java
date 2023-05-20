package com.example.wallet.controller;

import com.example.wallet.Util.TransactionStatus;
import com.example.wallet.domain.AccountDTO;
import com.example.wallet.domain.TransactionDTO;
import com.example.wallet.domain.accountpayload.ResponsePayload;
import com.example.wallet.domain.accountpayload.TransferPayload;
import com.example.wallet.domain.walletpayload.WithdrawRequest;
import com.example.wallet.domain.walletpayload.WithdrawResponse;
import com.example.wallet.service.TransactionService;
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
}
