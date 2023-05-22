package com.example.wallet.Service;

import com.example.wallet.Domain.AccountPayload.TransferPayload;
import com.example.wallet.Domain.RecipientAccountDto;
import com.example.wallet.Domain.ResponsePayload;
import com.example.wallet.Util.TransactionStatus;
import com.example.wallet.Domain.AccountDto;
import com.example.wallet.Domain.TransactionDto;
import com.example.wallet.Domain.AccountPayload.TransferResponse;
import com.example.wallet.Domain.WalletPayload.WithdrawRequest;
import com.example.wallet.Domain.WalletPayload.WithdrawResponse;
import com.example.wallet.Jms.MessagePublisher;
import com.example.wallet.Repository.TransactionRepository;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public TransactionDto savePayment(TransferPayload transferPayload, WithdrawResponse withdrawResponse){
        try {
            TransactionDto transactionDTO = new TransactionDto();
            transactionDTO.setAmount(transferPayload.getAmount());
            transactionDTO.setUserId(transferPayload.getUserId());
            transactionDTO.setWalletTransactionId(withdrawResponse.getWalletTransactionId());
            transactionDTO.setAccountId(transferPayload.getAccountId());
            transactionDTO.setStatus(TransactionStatus.RECEIVED);
            return transactionRepository.save(transactionDTO);
        }catch(Exception e){
            LOGGER.error("An error occurred while saving transaction: {}", e.getMessage());
            return null;
        }
    }
    @Override
    public ResponsePayload createResponsePayload(TransactionStatus status, String statusDescription, BigDecimal amount,Long transactionId) {
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatus(status);
        responsePayload.setAmount(amount);
        responsePayload.setTransactionId(transactionId);
        responsePayload.setStatusDescription(statusDescription);
        return responsePayload;
    }
   @Override
    public Page<TransactionDto> getFilteredTransactions(BigDecimal amount, LocalDate date, int page, int size) {
       LocalDateTime startOfDay = date.atStartOfDay();
       LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

       Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
       return transactionRepository.findByAmountAndCreatedAtBetween(amount, startOfDay, endOfDay, pageable);

   }
    public void updateTransaction(Long id, TransactionStatus transactionStatus){
        Optional<TransactionDto> transactionDTO=transactionRepository.findById(id);
        if (transactionDTO.isPresent()) {
           transactionDTO.get().setStatus(transactionStatus);
            transactionRepository.save(transactionDTO.get());
        }
    }
    @Override
    public WithdrawResponse withDrawWallet(TransferPayload transferPayload){
        try {
            WithdrawRequest withdrawRequest=new WithdrawRequest();
            withdrawRequest.setAmount(transferPayload.getAmount());
            withdrawRequest.setUser_id(transferPayload.getUserId());

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
            LOGGER.error("HttpClient Error occurred: {} - {}", ex.getStatusCode().value(), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            LOGGER.error("An error occurred: {}", ex.getMessage());
        }
        return null;
    }
    @Override
    public boolean saveToQueue(TransactionDto transactionDto, TransferPayload transferPayload, RecipientAccountDto recipientAccountDto){
        AccountDto accountDTO=new AccountDto();
        accountDTO.setTransactionId(transactionDto.getWalletTransactionId());
        accountDTO.setTransferPayload(transferPayload);
        accountDTO.setRecipientAccountDto(recipientAccountDto);
        return messagePublisher.insertToQueue(accountDTO);
    }
    @Override
    public void transferToaccount(AccountDto accountDTO){

            RestTemplate restTemplate = new RestTemplate();
            String url = ontopUrl + "/api/v1/payments";
            TransactionStatus transactionStatus = TransactionStatus.FAILED;
            String recipientFullName = accountDTO.getRecipientAccountDto().getFirstName() + " " + accountDTO.getRecipientAccountDto().getLastName();
            String recipientAcc = accountDTO.getRecipientAccountDto().getAccountNo();
            String recipientRouting = accountDTO.getRecipientAccountDto().getRoutingNumber();
            String currency = accountDTO.getTransferPayload().getCurrency();
            BigDecimal amount = accountDTO.getTransferPayload().getAmount();
            Long transactionId = accountDTO.getTransactionId();
            try {
            String requestPayload = "{\n" +
                    "    \"source\": {\n" +
                    "        \"type\": \"COMPANY\",\n" +
                    "        \"sourceInformation\": {\n" +
                    "            \"name\": \"" + ontopAccName + "\"\n" +
                    "        },\n" +
                    "        \"account\": {\n" +
                    "            \"accountNumber\": \"" + ontopAc + "\",\n" +
                    "            \"currency\": \"" + ontopCurrency + "\",\n" +
                    "            \"routingNumber\": \"" + ontopAccRouting + "\"\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"destination\": {\n" +
                    "        \"name\": \"" + recipientFullName + "\",\n" +
                    "        \"account\": {\n" +
                    "            \"accountNumber\": \"" + recipientAcc + "\",\n" +
                    "            \"currency\": \"" + currency + "\",\n" +
                    "            \"routingNumber\": \"" + recipientRouting + "\"\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"amount\": " + amount + "\n" +
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
            if (response.getStatusCode().value() == 200) {
                transactionStatus = TransactionStatus.COMPLETED;
            }

            updateTransaction(transactionId, transactionStatus);
            LOGGER.info("RESPONSE FROM A/C TRANSFER Status: " + responseBody.getRequestInfo().getStatus());
            LOGGER.info("RESPONSE FROM A/C TRANSFER Amount: " + responseBody.getPaymentInfo().getAmount());
            LOGGER.info("RESPONSE FROM A/C TRANSFER ID: " + responseBody.getPaymentInfo().getId());

        }catch(Exception e){
            LOGGER.error("An error occured while doing account transfer"+e.getMessage());
            transactionStatus=TransactionStatus.REFUND;
            updateTransaction(transactionId, transactionStatus);
        }
    }

}
