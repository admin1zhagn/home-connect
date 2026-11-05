package com.homeconnect.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("plan_product")
public class PlanProduct {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String planName;
    private String operator;
    private String planType;
    private BigDecimal standardFee;
    private BigDecimal discountFee;
    private Integer discountMonths;
    private BigDecimal afterDiscountFee;
    private BigDecimal dataGb;
    private Integer callMinutes;
    private Integer broadbandSpeed;
    private Integer subCardCount;
    private Integer contractMonths;
    private BigDecimal installationFee;
    private BigDecimal equipmentFee;
    private String conditions;
    private String description;
    private String sourceNote;
    private LocalDateTime verifiedAt;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
