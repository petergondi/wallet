package com.example.wallet.Service;

import com.example.wallet.Domain.RecipientAccountDto;
import com.example.wallet.Repository.RecipientAcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RecipientAcServiceImpl implements RecipientAcService{
    @Autowired
    RecipientAcRepository recipientAcRepository;
    @Override
    @Transactional
    public RecipientAccountDto saveAccount(RecipientAccountDto recipientAccountDto){
        return recipientAcRepository.save(recipientAccountDto);
    }
    @Override
    public RecipientAccountDto findAc(Long id){
        Optional<RecipientAccountDto> recipientAccountDto= recipientAcRepository.findById(id);
        return recipientAccountDto.orElse(null);
    }
}
