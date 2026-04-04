package com.flowfund.engine.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "buffers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Buffer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Monthly Allocations
    @Builder.Default
    private BigDecimal taxBuffer = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal medicalBuffer = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal emergencyBuffer = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal safeToSpend = BigDecimal.ZERO;
    
    // Cumulative Funds (Stateful tracked)
    @Builder.Default
    private BigDecimal totalEmergencyFund = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal totalMedicalFund = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal totalTaxSaved = BigDecimal.ZERO;

    // Status Flags
    @Builder.Default
    private String riskLevel = "STABLE"; 
    @Builder.Default
    private Boolean isUnstable = false;
    @Builder.Default
    private Boolean isDeficit = false;
    @Builder.Default
    private Boolean runwayCompleted = false;
    
    private BigDecimal volatility;
    private Double score; // Financial health score
    private BigDecimal forecast; // Current month projected income
    
    @ElementCollection
    private List<BigDecimal> forecastSeries; // 6-month projection
    
    @ElementCollection
    private List<String> insights; // AI-generated insights
    
    private LocalDateTime lastUpdated;
    
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    // MANUAL ACCESSORS FOR BUILD STABILITY
    public void setForecastSeries(List<BigDecimal> series) {
        this.forecastSeries = series;
    }
    public List<BigDecimal> getForecastSeries() {
        return forecastSeries;
    }
    public BigDecimal getEmergencyBuffer() {
        return emergencyBuffer != null ? emergencyBuffer : BigDecimal.ZERO;
    }
    public BigDecimal getSafeToSpend() {
        return safeToSpend != null ? safeToSpend : BigDecimal.ZERO;
    }
    public boolean getIsDeficit() {
        return isDeficit != null ? isDeficit : false;
    }
    public BigDecimal getVolatility() {
        return volatility != null ? volatility : BigDecimal.ZERO;
    }
}
