package com.example.wallet.Jms;

import com.example.wallet.Domain.AccountDto;
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
    public void insertToQueue(AccountDto accountDTO) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(accountDTO);
            jmsTemplate.convertAndSend("wallet-queue", jsonPayload);
            LOGGER.info("Inserted to the queue<==>"+jsonPayload);
        } catch (Exception e) {
           LOGGER.error("An error occured while inserting to the queue<==>"+e.getMessage());
        }
    }
}
