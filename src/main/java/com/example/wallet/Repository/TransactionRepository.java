package com.example.wallet.Repository;

import com.example.wallet.Domain.TransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<TransactionDto,Long> {
    Page<TransactionDto> findByAmountAndCreatedAtBetween(BigDecimal amount, LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
}
