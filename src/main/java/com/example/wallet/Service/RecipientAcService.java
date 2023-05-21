package com.example.wallet.Service;

import com.example.wallet.Domain.RecipientAccountDto;
import org.springframework.transaction.annotation.Transactional;

public interface RecipientAcService {
    @Transactional
    RecipientAccountDto saveAccount(RecipientAccountDto recipientAccountDto);

    RecipientAccountDto findAc(Long id);
}
