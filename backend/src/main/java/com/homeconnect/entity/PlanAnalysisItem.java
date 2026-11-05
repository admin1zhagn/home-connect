package com.homeconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("plan_analysis_item")
public class PlanAnalysisItem {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long analysisId;
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
