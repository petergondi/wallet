package com.example.wallet.service.core;

import com.example.wallet.domain.Transaction;
import com.example.wallet.domain.walletpayload.WithdrawRequest;
import com.example.wallet.domain.walletpayload.WithdrawResponse;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.service.interfaces.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Value("${ontop.wallet.url}")
    private String ontopUrl;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    public Transaction savePayment(Transaction transaction){
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
//    public void transferToaccount(){
//        String url = "https://example.com/payment-endpoint"; // Replace with the actual endpoint URL
//        // Create the request JSON payload
//        String requestPayload = "{\n" +
//                "    \"source\": {\n" +
//                "        \"type\": \"COMPANY\",\n" +
//                "        \"sourceInformation\": {\n" +
//                "            \"name\": \"ONTOP INC\"\n" +
//                "        },\n" +
//                "        \"account\": {\n" +
//                "            \"accountNumber\": \"0245253419\",\n" +
//                "            \"currency\": \"USD\",\n" +
//                "            \"routingNumber\": \"028444018\"\n" +
//                "        }\n" +
//                "    },\n" +
//                "    \"destination\": {\n" +
//                "        \"name\": \"TONY STARK\",\n" +
//                "        \"account\": {\n" +
//                "            \"accountNumber\": \"1885226711\",\n" +
//                "            \"currency\": \"USD\",\n" +
//                "            \"routingNumber\": \"211927207\"\n" +
//                "        }\n" +
//                "    },\n" +
//                "    \"amount\": 1000\n" +
//                "}";
//
//        // Set the headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Create the HTTP entity with headers and payload
//        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
//
//        // Send the POST request and get the response
//        ResponseEntity<ResponseJson> response = restTemplate.postForEntity(url, entity, ResponseJson.class);
//
//        // Extract the response body
//        ResponseJson responseBody = response.getBody();
//
//        // Print the fields from the response
//        System.out.println("Status: " + responseBody.getRequestInfo().getStatus());
//        System.out.println("Amount: " + responseBody.getPaymentInfo().getAmount());
//        System.out.println("ID: " + responseBody.getPaymentInfo().getId());
//    }

}
