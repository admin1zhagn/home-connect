package com.homeconnect.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer_service_record")
public class CustomerServiceRecord {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long customerId;
    private String serviceType;
    private LocalDateTime serviceTime;
    private String serviceContent;
    private String serviceResult;
    private LocalDateTime followUpDate;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
