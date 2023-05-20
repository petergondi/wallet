package com.example.wallet.jms.message;

import com.example.wallet.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

//public class MessagePublisher {
//    @Autowired
//    private JmsTemplate jmsTemplate;
//    private static final Logger LOGGER = LoggerFactory.getLogger(MessagePublisher.class);
//    public void insertToQueue(Transaction transaction) {
//        try {
//            jmsTemplate.convertAndSend("wallet-queue", transaction);
//            LOGGER.error("Inserted to the queue<==>"+transaction);
//        } catch (Exception e) {
//           LOGGER.error("An error occured while inserting to the queue<==>"+e.getMessage());
//        }
//    }
//}
