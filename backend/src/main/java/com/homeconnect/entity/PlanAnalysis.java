package com.homeconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("plan_analysis")
public class PlanAnalysis {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long customerId;
    private String analysisNo;
    private BigDecimal currentMonthlyFee;
    private BigDecimal recommendedMonthlyFee;
    private BigDecimal afterDiscountMonthlyFee;
    private BigDecimal oneTimeFee;
    private Integer comparisonMonths;
    private BigDecimal currentTotalFee;
    private BigDecimal recommendedTotalFee;
    private BigDecimal estimatedSaving;
    private String recommendationReason;
    private String customerFeedback;
    private String dealStatus;
    private LocalDateTime createdAt;
}
