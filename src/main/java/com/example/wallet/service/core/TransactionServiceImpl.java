package com.example.wallet.service.core;

import com.example.wallet.domain.Transaction;
import com.example.wallet.domain.request.WithdrawRequest;
import com.example.wallet.domain.request.WithdrawResponse;
import com.example.wallet.jms.message.MessageConsumer;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.service.interfaces.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Value("${ontop.wallet.url}")
    private String ontopUrl;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    public Transaction createPayment(Transaction transaction){
        return transactionRepository.save(transaction);
    }
    @Override
    public WithdrawResponse withDrawWallet(WithdrawRequest withdrawRequest){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<WithdrawRequest> requestEntity = new HttpEntity<>(withdrawRequest, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<WithdrawResponse> responseEntity = restTemplate.exchange(
                    ontopUrl + "/wallets/transactions",
                    HttpMethod.POST,
                    requestEntity,
                    WithdrawResponse.class
            );
            if(responseEntity.getStatusCode().value()==200){
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException ex) {
            LOGGER.error("Error occurred: " + ex.getStatusCode().value() + " - " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            LOGGER.error("An error occurred: " + ex.getMessage());
        }
        return null;
    }

}
