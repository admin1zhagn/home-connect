package com.homeconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeconnect.dto.DashboardVO;
import com.homeconnect.dto.FollowUpVO;
import com.homeconnect.entity.Customer;
import com.homeconnect.entity.PlanAnalysis;
import com.homeconnect.mapper.PlanAnalysisMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final CustomerService customerService;
    private final PlanAnalysisMapper planAnalysisMapper;
    private final FollowUpService followUpService;

    public DashboardService(CustomerService customerService, PlanAnalysisMapper planAnalysisMapper,
                            FollowUpService followUpService) {
        this.customerService = customerService;
        this.planAnalysisMapper = planAnalysisMapper;
        this.followUpService = followUpService;
    }

    public DashboardVO stats() {
        DashboardVO vo = new DashboardVO();
        vo.setCustomerTotal(customerService.countAll());
        vo.setCustomerNewThisMonth(customerService.countNewThisMonth());
        vo.setFollowUpPending(customerService.countByStatus("待跟进"));
        vo.setDealCount(customerService.countByStatus("已成交"));

        LocalDate first = LocalDate.now().withDayOfMonth(1);
        vo.setRecommendCountThisMonth(planAnalysisMapper.selectCount(
                new QueryWrapper<PlanAnalysis>().ge("created_at", first.atStartOfDay())));
        vo.setRecommendSuccessCount(planAnalysisMapper.selectCount(
                new QueryWrapper<PlanAnalysis>().eq("customer_feedback", "接受推荐")));

        Long totalAnalyses = planAnalysisMapper.selectCount(
                new QueryWrapper<PlanAnalysis>().isNotNull("customer_feedback"));
        Long accepted = vo.getRecommendSuccessCount();
        if (totalAnalyses != null && totalAnalyses > 0) {
            BigDecimal rate = BigDecimal.valueOf(accepted)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalAnalyses), 1, RoundingMode.HALF_UP);
            vo.setRecommendSuccessRate(rate + "%");
        } else {
            vo.setRecommendSuccessRate("—");
        }

        Long total = vo.getCustomerTotal();
        Long deal = vo.getDealCount();
        if (total != null && total > 0) {
            BigDecimal cr = BigDecimal.valueOf(deal)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
            vo.setConversionRate(cr + "%");
        } else {
            vo.setConversionRate("—");
        }

        Page<Customer> p = new Page<>(1, 5);
        vo.setRecentCustomers(customerService.page(p, null, null, null, null, null).getRecords());
        vo.setRecentFollowUps(followUpService.list("all").stream().limit(5).collect(Collectors.toList()));
        return vo;
    }
}
