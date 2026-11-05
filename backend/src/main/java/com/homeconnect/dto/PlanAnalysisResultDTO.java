package com.homeconnect.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlanAnalysisResultDTO {
    private Long analysisId;
    private Long customerId;
    private BigDecimal currentMonthlyFee;
    private BigDecimal recommendedMonthlyFee;
    private BigDecimal afterDiscountMonthlyFee;
    private BigDecimal oneTimeFee;
    private Integer comparisonMonths;
    private BigDecimal currentTotalFee;
    private BigDecimal recommendedTotalFee;
    private BigDecimal estimatedSaving;
    private Boolean estimated;
    /** SAVING | NO_SAVING | VERIFY */
    private String conclusion;
    private String recommendationReason;
    private List<PlanAnalysisItemVO> items;
}
