package com.flowfund.engine.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String provider;

    // Monthly Expenses
    @Builder.Default
    private BigDecimal rent = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal groceries = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal utilities = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal internet = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal transport = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal monthlyDebt = BigDecimal.ZERO; // EMI / Loan Repayments
    @Builder.Default
    private BigDecimal inflationRate = new BigDecimal("0.06"); // 6% Default

    // Financial Profile
    private String profession;
    private String incomeType;
    @Builder.Default
    private BigDecimal baseIncome = BigDecimal.ZERO;

    // Risk Multipliers
    @Builder.Default
    private Integer age = 25;
    @Builder.Default
    private Integer dependents = 0;
    
    @Builder.Default
    private String taxRegime = "NEW"; // NEW vs OLD

    @Builder.Default
    private Double incomeConcentration = 0.5; // 1.0 = Single Client Risk

    @Builder.Default
    private boolean onboarded = false;
    @Builder.Default
    private String role = "USER";

    // MANUAL ACCESSORS FOR STABILITY
    public String getTaxRegime() { return taxRegime != null ? taxRegime : "NEW"; }
    public Integer getAge() { return age != null ? age : 25; }
    public Integer getDependents() { return dependents != null ? dependents : 0; }
    public BigDecimal getMonthlyDebt() { return monthlyDebt != null ? monthlyDebt : BigDecimal.ZERO; }
    public BigDecimal getInflationRate() { return inflationRate != null ? inflationRate : new BigDecimal("0.06"); }
    public BigDecimal getRent() { return rent != null ? rent : BigDecimal.ZERO; }
    public BigDecimal getGroceries() { return groceries != null ? groceries : BigDecimal.ZERO; }
    public BigDecimal getUtilities() { return utilities != null ? utilities : BigDecimal.ZERO; }
    public Double getIncomeConcentration() { return incomeConcentration != null ? incomeConcentration : 0.5; }
    public BigDecimal getBaseIncome() { return baseIncome != null ? baseIncome : BigDecimal.ZERO; }
}
