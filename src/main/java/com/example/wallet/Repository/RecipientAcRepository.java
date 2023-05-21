package com.example.wallet.Repository;

import com.example.wallet.Domain.RecipientAccountDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientAcRepository extends JpaRepository<RecipientAccountDto,Long> {
}
