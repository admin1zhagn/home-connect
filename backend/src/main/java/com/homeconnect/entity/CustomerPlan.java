package com.homeconnect.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("customer_plan")
public class CustomerPlan {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long customerId;
    private Long memberId;
    private String planName;
    private String operator;
    private String planType;
    private BigDecimal monthlyFee;
    private BigDecimal extraMonthlyFee;
    private BigDecimal dataGb;
    private Integer callMinutes;
    private Integer broadbandSpeed;
    private LocalDate contractEndDate;
    private Integer contractMonths;
    private BigDecimal equipmentFee;
    private String feeAccuracy;
    private String effectiveStatus;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
