package com.example.wallet.Domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="recipient_accounts")
public class RecipientAccountDto {
    @Id
    @GeneratedValue
    private Long accountId;
    private String firstName;
    private String lastName;
    private String routingNumber;
    private String nationalId;
    @Column(unique = true)
    private String accountNo;
}
