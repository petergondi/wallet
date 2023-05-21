package com.example.wallet.Jms;

import com.example.wallet.Domain.AccountDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {
    @Autowired
    private JmsTemplate jmsTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagePublisher.class);
    public boolean insertToQueue(AccountDto accountDTO) {
        try {
            String jsonPayload = new ObjectMapper().writeValueAsString(accountDTO);
            jmsTemplate.convertAndSend("wallet-queue", jsonPayload);
            LOGGER.info("Inserted accountDTO to the wallet-queue: {}", jsonPayload);
            return true;
        } catch (JsonProcessingException e) {
            LOGGER.error("Error occurred while converting accountDTO to JSON: {}", e.getMessage());
            return false;
        }
    }
}
