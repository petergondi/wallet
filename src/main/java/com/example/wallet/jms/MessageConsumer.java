package com.example.wallet.jms;

import com.example.wallet.domain.AccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer  {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);
    @JmsListener(destination = "wallet-queue")
    public void onMessage(String id) {
        LOGGER.info("Message received! {}", id);
    }
}