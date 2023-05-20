package com.example.wallet.jms;

import com.example.wallet.domain.AccountDTO;
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
    public void insertToQueue(AccountDTO accountDTO) {
        try {
            jmsTemplate.convertAndSend("wallet-queue", accountDTO.getTransactionId());
            LOGGER.info("Inserted to the queue<==>"+accountDTO);
        } catch (Exception e) {
           LOGGER.error("An error occured while inserting to the queue<==>"+e.getMessage());
        }
    }
}
