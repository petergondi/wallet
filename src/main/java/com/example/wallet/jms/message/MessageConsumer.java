package com.example.wallet.jms.message;

import com.example.wallet.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

//@Component
//public class MessageConsumer  {
//    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);
//    @JmsListener(destination = "wallet-queue")
//    public void onMessage(Transaction transaction) {
//        LOGGER.info("Message received! {}", transaction);
//    }
//}