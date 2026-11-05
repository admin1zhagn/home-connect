package com.homeconnect.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowUpVO {
    /** CUSTOMER | SERVICE */
    private String sourceType;
    private Long id;
    private Long customerId;
    private String customerName;
    private String title;
    private LocalDateTime dueDate;
    private String status;
    private Boolean overdue;
}
