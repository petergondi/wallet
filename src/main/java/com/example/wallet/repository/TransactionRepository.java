package com.example.wallet.repository;

import com.example.wallet.domain.TransactionDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionDTO,Long> {
}
