package com.example.wallet.Domain;

import com.example.wallet.Util.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name="transactions")
public class TransactionDto {
    @Id
    @GeneratedValue
    private Long walletTransactionId;
    private Long userId;
    private BigDecimal amount;
    private Long accountId;
    private BigDecimal newAmount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private BigDecimal transFee;
    @PrePersist
    private void calculatedFields() {
        double doubleValue = 0.01;
        transFee = amount.multiply(BigDecimal.valueOf(doubleValue));
        newAmount=amount.subtract(transFee);
    }
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
