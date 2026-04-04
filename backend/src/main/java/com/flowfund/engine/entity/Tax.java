package com.flowfund.engine.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "tax_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tax {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private java.math.BigDecimal annualIncome;
    private java.math.BigDecimal annualTax;
    private java.math.BigDecimal savedTax; // Track already saved tax
    private java.math.BigDecimal monthlyBuffer;
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
