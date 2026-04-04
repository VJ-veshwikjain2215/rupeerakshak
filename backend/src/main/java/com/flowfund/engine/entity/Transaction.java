package com.flowfund.engine.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@SuppressWarnings("unused")
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private java.time.LocalDate date;
    private String description;
    private java.math.BigDecimal amount;
    private String type; // INCOME / EXPENSE
    private Boolean isTaxable;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
