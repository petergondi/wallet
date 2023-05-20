package com.example.wallet.repository;

import com.example.wallet.domain.TransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<TransactionDTO,Long> {
    Page<TransactionDTO> findByAmountGreaterThanEqualAndCreatedAtGreaterThanEqual(BigDecimal amount, LocalDate date,
                                                                             Pageable pageable);
}
