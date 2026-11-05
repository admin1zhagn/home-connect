package com.homeconnect.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardVO {
    private Long customerTotal;
    private Long customerNewThisMonth;
    private Long followUpPending;
    private Long dealCount;
    private Long recommendCountThisMonth;
    private Long recommendSuccessCount;
    private String recommendSuccessRate;
    private String conversionRate;
    private List<com.homeconnect.entity.Customer> recentCustomers;
    private List<FollowUpVO> recentFollowUps;
}
