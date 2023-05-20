package com.example.wallet.domain;

import com.example.wallet.Util.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Entity
public class TransactionDTO {
    @Id
    @GeneratedValue
    private Long wallet_transaction_id;
    private Long user_id;
    private BigDecimal amount;
    private BigDecimal newamount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private BigDecimal transFee;
    @PrePersist
    private void calculatedFields() {
        double doubleValue = 0.01;
        transFee = amount.multiply(BigDecimal.valueOf(doubleValue));
        newamount=amount.subtract(transFee);
    }
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
