package com.example.wallet.Jms;

import com.example.wallet.Domain.AccountDto;
import com.example.wallet.Service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer  {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TransactionService transactionService;
    @JmsListener(destination = "wallet-queue")
    public void onMessage(String message) {
        try {
            AccountDto accountDTO = objectMapper.readValue(message, AccountDto.class);
            transactionService.transferToaccount(accountDTO);
            LOGGER.info("Message received! {}", accountDTO);
        }catch(Exception e){
            LOGGER.error("An error occured while reading from the queue! {}", e.getMessage());
        }
    }
}