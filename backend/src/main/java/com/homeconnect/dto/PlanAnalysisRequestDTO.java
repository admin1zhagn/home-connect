package com.homeconnect.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlanAnalysisRequestDTO {
    private Long customerId;
    private List<Long> selectedProductIds;
    private Integer comparisonMonths;
}
