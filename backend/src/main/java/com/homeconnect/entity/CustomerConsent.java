package com.homeconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer_consent")
public class CustomerConsent {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long customerId;
    private String consentType;
    private Integer consentStatus;
    private String consentScope;
    private LocalDateTime consentTime;
    private LocalDateTime revokedAt;
    private String remark;
}
