package com.example.wallet.jms;

import com.example.wallet.domain.AccountDTO;
import com.example.wallet.service.TransactionService;
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
            AccountDTO accountDTO = objectMapper.readValue(message, AccountDTO.class);
            transactionService.transferToaccount(accountDTO);
            LOGGER.info("Message received! {}", accountDTO);
        }catch(Exception e){
            LOGGER.error("An error occured while reading from the queue! {}", e.getMessage());
        }
    }
}