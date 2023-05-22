package com.example.wallet.web;

import com.example.wallet.Domain.RecipientAccountDto;
import com.example.wallet.Service.RecipientAcService;
import com.example.wallet.WalletApplicationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class RecipientAcControllerTest extends WalletApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipientAcService recipientAcService;

    @Test
    public void testCreateAccount() throws Exception {
        // Prepare the request payload
        RecipientAccountDto requestDto = new RecipientAccountDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setRoutingNumber("123456789");
        requestDto.setNationalId("123456789");
        requestDto.setAccountNo("987654321");

        // Set up the desired behavior of the mocked recipientAcService
        RecipientAccountDto savedAccountDto = new RecipientAccountDto();
        savedAccountDto.setAccountId(1L);
        savedAccountDto.setFirstName(requestDto.getFirstName());
        savedAccountDto.setLastName(requestDto.getLastName());
        savedAccountDto.setRoutingNumber(requestDto.getRoutingNumber());
        savedAccountDto.setNationalId(requestDto.getNationalId());
        savedAccountDto.setAccountNo(requestDto.getAccountNo());

        Mockito.when(recipientAcService.saveAccount(Mockito.any(RecipientAccountDto.class))).thenReturn(savedAccountDto);

        // Perform the POST request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8081/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(savedAccountDto.getAccountId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(savedAccountDto.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(savedAccountDto.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.routingNumber").value(savedAccountDto.getRoutingNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nationalId").value(savedAccountDto.getNationalId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNo").value(savedAccountDto.getAccountNo()))
                .andReturn();
    }

    // Helper method to convert an object to JSON string
    private static String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
