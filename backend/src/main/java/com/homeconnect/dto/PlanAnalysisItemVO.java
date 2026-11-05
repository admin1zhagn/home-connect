package com.homeconnect.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlanAnalysisItemVO {
    private Long productId;
    private String productName;
    private String operator;
    private String planType;
    private BigDecimal monthlyFee;
    private BigDecimal discountFee;
    private Integer discountMonths;
    private BigDecimal afterDiscountFee;
    private BigDecimal equipmentFee;
    private BigDecimal installationFee;
    private Integer contractMonths;
}
