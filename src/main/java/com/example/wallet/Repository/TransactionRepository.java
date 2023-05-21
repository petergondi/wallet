package com.example.wallet.Repository;

import com.example.wallet.Domain.TransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<TransactionDto,Long> {
    Page<TransactionDto> findByAmountGreaterThanEqualAndCreatedAtGreaterThanEqual(BigDecimal amount, LocalDate date,
                                                                                  Pageable pageable);
}
