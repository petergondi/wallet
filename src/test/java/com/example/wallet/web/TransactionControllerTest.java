package com.example.wallet.web;

import com.example.wallet.Controller.TransactionController;
import com.example.wallet.Domain.AccountPayload.TransferPayload;
import com.example.wallet.Domain.RecipientAccountDto;
import com.example.wallet.Domain.ResponsePayload;
import com.example.wallet.Domain.TransactionDto;
import com.example.wallet.Domain.WalletPayload.WithdrawResponse;
import com.example.wallet.Service.RecipientAcService;
import com.example.wallet.Service.TransactionService;
import com.example.wallet.Util.TransactionStatus;
import com.example.wallet.WalletApplicationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.junit.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest extends WalletApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipientAcService recipientAcService;
    @MockBean
    private TransactionService transactionService;

    private static final String url="http://localhost:8081/v1/transfer";


    @Test
    public void testGetFilteredTransactions() throws Exception {
        // Prepare the test data
        BigDecimal amount = new BigDecimal("1000.00");
        LocalDate date = LocalDate.parse("2023-05-20");
        int page = 0;
        int size = 10;

        // Create a list of TransactionDto objects for the mock response
        List<TransactionDto> transactionList = new ArrayList<>();
        // Add some transaction objects to the list

        // Create a Page object with the list of transactions
        Page<TransactionDto> transactionPage = new PageImpl<>(transactionList, PageRequest.of(page, size), transactionList.size());

        when(transactionService.getFilteredTransactions(amount, date, page, size)).thenReturn(transactionPage);

        // Perform the GET request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("amount", amount.toString())
                        .param("date", date.toString())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(transactionList.size())))
                // Add more assertions for the expected response structure if needed
                .andReturn();
    }
    @Test
    public void testAccTransfer() throws Exception {
        // Mock the behavior of the dependencies
        RecipientAccountDto recipientAccountDtoMock = new RecipientAccountDto();
        recipientAccountDtoMock.setAccountId(1L);
        recipientAccountDtoMock.setFirstName("John");
        recipientAccountDtoMock.setLastName("Doe");
        recipientAccountDtoMock.setRoutingNumber("123456789");
        recipientAccountDtoMock.setNationalId("ABC123");
        recipientAccountDtoMock.setAccountNo("1234567890");
        when(recipientAcService.findAc(anyLong())).thenReturn(recipientAccountDtoMock);

        WithdrawResponse withdrawResponseMock = new WithdrawResponse();
        withdrawResponseMock.setWalletTransactionId(1L);
        withdrawResponseMock.setAmount(BigDecimal.valueOf(100.00));
        withdrawResponseMock.setUserId(2L);
        when(transactionService.withDrawWallet(Mockito.any(TransferPayload.class))).thenReturn(withdrawResponseMock);

        TransactionDto savedTransactionDtoMock = new TransactionDto();
        savedTransactionDtoMock.setWalletTransactionId(1L);
        savedTransactionDtoMock.setUserId(2L);
        savedTransactionDtoMock.setAmount(BigDecimal.valueOf(100.00));
        savedTransactionDtoMock.setAccountId(3L);
        savedTransactionDtoMock.setNewAmount(BigDecimal.valueOf(90.00));
        savedTransactionDtoMock.setStatus(TransactionStatus.RECEIVED);
        savedTransactionDtoMock.setTransFee(BigDecimal.valueOf(10.00));
        when(transactionService.savePayment(Mockito.any(TransferPayload.class), Mockito.any(WithdrawResponse.class))).thenReturn(savedTransactionDtoMock);

        boolean insertToQueueMock = true;
        when(transactionService.saveToQueue(Mockito.any(TransactionDto.class), Mockito.any(TransferPayload.class), Mockito.any(RecipientAccountDto.class))).thenReturn(insertToQueueMock);

        // Prepare the request payload
        TransferPayload transferPayload = new TransferPayload();
        transferPayload.setAccountId(1L);
        transferPayload.setUserId(2L);
        transferPayload.setCurrency("USD");
        transferPayload.setAmount(BigDecimal.valueOf(100.00));

        // Perform the request
        MvcResult result = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transferPayload)))
                .andReturn();

        // Verify the status code
        int statusCode = result.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.OK.value(), statusCode);

        // Verify interactions with the mocked dependencies
        verify(recipientAcService, Mockito.times(1)).findAc(anyLong());
        verify(transactionService, Mockito.times(1)).withDrawWallet(Mockito.any(TransferPayload.class));
        verify(transactionService, Mockito.times(1)).savePayment(Mockito.any(TransferPayload.class), Mockito.any(WithdrawResponse.class));
        verify(transactionService, Mockito.times(1)).saveToQueue(Mockito.any(TransactionDto.class), Mockito.any(TransferPayload.class), Mockito.any(RecipientAccountDto.class));
    }

    @Test
    public void testAccountNotFound() throws Exception {
        when(recipientAcService.findAc(anyLong())).thenReturn(null);

        // Perform the POST request
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\": 123, \"userId\": 456, \"currency\": \"USD\", \"amount\": 100.00}"))
                .andExpect(status().isBadRequest());
    }
}
