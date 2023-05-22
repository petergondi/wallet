package com.example.wallet.Controller;


import com.example.wallet.Domain.RecipientAccountDto;
import com.example.wallet.Service.RecipientAcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecipientAcController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientAcController.class);
    @Autowired
    RecipientAcService recipientAcService;
    @PostMapping
    public ResponseEntity<RecipientAccountDto> createAccount(@RequestBody RecipientAccountDto recipientAccountDto) {
        try{
            RecipientAccountDto recipientAccountDto1=recipientAcService.saveAccount(recipientAccountDto);
            return new ResponseEntity<>(recipientAccountDto1, HttpStatus.CREATED);
        }catch(Exception e){
            LOGGER.info("An error occured while saving the ac"+e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
