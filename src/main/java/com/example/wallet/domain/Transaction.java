package com.example.wallet.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private Long wallet_transaction_id;
    private Long user_id;
    private BigDecimal amount;
    private String status;
    private BigDecimal transFee;
    @PostLoad
    private void calculatedFields() {
        double doubleValue = 0.1;
        transFee = amount.multiply(BigDecimal.valueOf(doubleValue));
        amount=amount.subtract(transFee);
    }
    private BigDecimal runningBalance;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
