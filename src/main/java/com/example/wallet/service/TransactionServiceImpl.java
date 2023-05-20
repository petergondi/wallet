package com.example.wallet.service;

import com.example.wallet.Util.TransactionStatus;
import com.example.wallet.domain.AccountDTO;
import com.example.wallet.domain.TransactionDTO;
import com.example.wallet.domain.accountpayload.TransferResponse;
import com.example.wallet.domain.walletpayload.WithdrawRequest;
import com.example.wallet.domain.walletpayload.WithdrawResponse;
import com.example.wallet.jms.MessagePublisher;
import com.example.wallet.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private MessagePublisher messagePublisher;
    @Value("${ontop.wallet.url}")
    private String ontopUrl;
    @Value("${ontop.debit.account}")
    private String ontopAc;
    @Value("${ontop.account.name}")
    private String ontopAccName;
    @Value("${ontop.account.routingnumber}")
    private String ontopAccRouting;
    @Value("${ontop.account.currency}")
    private String ontopCurrency;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    @Transactional
    public TransactionDTO savePayment(TransactionDTO transactionDTO){
            return transactionRepository.save(transactionDTO);
    }
    @Override
    public List<TransactionDTO> getTransactions(){
        return transactionRepository.findAll();
    }
    @Override
    public Page<TransactionDTO> getFilteredTransactions(BigDecimal amount, LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationAt").descending());
        return transactionRepository.findByAmountGreaterThanEqualAndCreatedAtGreaterThanEqual(amount, date, pageable);
    }
    public void updateTransaction(Long id, TransactionStatus transactionStatus){
        Optional<TransactionDTO> transactionDTO=transactionRepository.findById(id);
        if (transactionDTO.isPresent()) {
           transactionDTO.get().setStatus(transactionStatus);
            transactionRepository.save(transactionDTO.get());
        }
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
    @Override
    public void saveToQueue(AccountDTO accountDTO){
        messagePublisher.insertToQueue(accountDTO);

    }
    @Override
    public void transferToaccount(AccountDTO accountDTO){
        RestTemplate restTemplate = new RestTemplate();
        String url = ontopUrl+"/api/v1/payments";
        TransactionStatus transactionStatus=TransactionStatus.FAILED;
        String recipientFullName=accountDTO.getTransferPayload().getFirstName()+" "+accountDTO.getTransferPayload().getLastName();
        String recipientAcc=accountDTO.getTransferPayload().getAccountNo();
        String recipientRouting=accountDTO.getTransferPayload().getRoutingNo();
        String currency=accountDTO.getTransferPayload().getCurrency();
        BigDecimal amount=accountDTO.getTransferPayload().getAmount();
        Long transactionId= accountDTO.getTransactionId();
        String requestPayload = "{\n" +
                "    \"source\": {\n" +
                "        \"type\": \"COMPANY\",\n" +
                "        \"sourceInformation\": {\n" +
                "            \"name\": \""+ontopAccName+"\"\n" +
                "        },\n" +
                "        \"account\": {\n" +
                "            \"accountNumber\": \""+ontopAc+"\",\n" +
                "            \"currency\": \""+ontopCurrency+"\",\n" +
                "            \"routingNumber\": \""+ontopAccRouting+"\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"destination\": {\n" +
                "        \"name\": \""+recipientFullName+"\",\n" +
                "        \"account\": {\n" +
                "            \"accountNumber\": \""+recipientAcc+"\",\n" +
                "            \"currency\": \""+currency+"\",\n" +
                "            \"routingNumber\": \""+recipientRouting+"\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"amount\": "+amount+"\n" +
                "}";

        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity with headers and payload
        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        // Send the POST request and get the response
        ResponseEntity<TransferResponse> response = restTemplate.postForEntity(url, entity, TransferResponse.class);

        // Extract the response body
        TransferResponse responseBody = response.getBody();
        if(response.getStatusCode().value()==200) {
            transactionStatus = TransactionStatus.COMPLETED;
        }

        updateTransaction(transactionId,transactionStatus);

        // Print the fields from the response
        System.out.println("Status: " + responseBody.getRequestInfo().getStatus());
        System.out.println("Amount: " + responseBody.getPaymentInfo().getAmount());
        System.out.println("ID: " + responseBody.getPaymentInfo().getId());
    }

}
