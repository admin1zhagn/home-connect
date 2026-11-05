package com.homeconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeconnect.common.BizException;
import com.homeconnect.common.Money;
import com.homeconnect.dto.PlanAnalysisItemVO;
import com.homeconnect.dto.PlanAnalysisRequestDTO;
import com.homeconnect.dto.PlanAnalysisResultDTO;
import com.homeconnect.entity.CustomerMember;
import com.homeconnect.entity.CustomerPlan;
import com.homeconnect.entity.PlanAnalysis;
import com.homeconnect.entity.PlanAnalysisItem;
import com.homeconnect.entity.PlanProduct;
import com.homeconnect.mapper.CustomerMemberMapper;
import com.homeconnect.mapper.CustomerPlanMapper;
import com.homeconnect.mapper.PlanAnalysisItemMapper;
import com.homeconnect.mapper.PlanAnalysisMapper;
import com.homeconnect.mapper.PlanProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlanAnalysisService {

    private final CustomerPlanMapper customerPlanMapper;
    private final PlanProductMapper planProductMapper;
    private final PlanAnalysisMapper planAnalysisMapper;
    private final PlanAnalysisItemMapper planAnalysisItemMapper;
    private final CustomerMemberMapper customerMemberMapper;

    public PlanAnalysisService(CustomerPlanMapper customerPlanMapper, PlanProductMapper planProductMapper,
                               PlanAnalysisMapper planAnalysisMapper, PlanAnalysisItemMapper planAnalysisItemMapper,
                               CustomerMemberMapper customerMemberMapper) {
        this.customerPlanMapper = customerPlanMapper;
        this.planProductMapper = planProductMapper;
        this.planAnalysisMapper = planAnalysisMapper;
        this.planAnalysisItemMapper = planAnalysisItemMapper;
        this.customerMemberMapper = customerMemberMapper;
    }

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    /**
     * 纯计算（不落库），供前端实时预览
     */
    public PlanAnalysisResultDTO analyze(PlanAnalysisRequestDTO req) {
        // 1. 当前家庭月支出（仅统计生效中的套餐）
        List<CustomerPlan> currentPlans = customerPlanMapper.selectList(
                new QueryWrapper<CustomerPlan>().eq("customer_id", req.getCustomerId()).eq("effective_status", "active"));
        BigDecimal currentMonthly = ZERO;
        boolean estimated = false;
        for (CustomerPlan p : currentPlans) {
            currentMonthly = currentMonthly.add(Money.nz(p.getMonthlyFee())).add(Money.nz(p.getExtraMonthlyFee()));
            if ("estimated".equals(p.getFeeAccuracy())) {
                estimated = true;
            }
        }

        // 2-4. 推荐组合费用
        List<PlanProduct> products = new ArrayList<>();
        if (req.getSelectedProductIds() != null) {
            for (Long pid : req.getSelectedProductIds()) {
                PlanProduct pp = planProductMapper.selectById(pid);
                if (pp != null) {
                    products.add(pp);
                }
            }
        }
        List<PlanAnalysisItemVO> items = new ArrayList<>();
        BigDecimal recommendedMonthly = ZERO;
        BigDecimal afterDiscountMonthly = ZERO;
        BigDecimal oneTime = ZERO;
        int maxMonths = 0;
        boolean verify = false;
        for (PlanProduct pp : products) {
            BigDecimal standard = Money.nz(pp.getStandardFee());
            BigDecimal discount = pp.getDiscountFee();
            BigDecimal after = pp.getAfterDiscountFee();
            BigDecimal recM = discount != null ? Money.nz(discount) : standard;
            recommendedMonthly = recommendedMonthly.add(recM);
            BigDecimal afterM = after != null ? Money.nz(after) : standard;
            afterDiscountMonthly = afterDiscountMonthly.add(afterM);
            oneTime = oneTime.add(Money.nz(pp.getInstallationFee())).add(Money.nz(pp.getEquipmentFee()));
            int dm = pp.getDiscountMonths() == null ? 0 : pp.getDiscountMonths();
            int cm = pp.getContractMonths() == null ? 0 : pp.getContractMonths();
            maxMonths = Math.max(maxMonths, Math.max(dm, cm));
            if (discount == null || after == null || (dm == 0 && cm == 0)) {
                verify = true;
            }
            PlanAnalysisItemVO vo = new PlanAnalysisItemVO();
            vo.setProductId(pp.getId());
            vo.setProductName(pp.getPlanName());
            vo.setOperator(pp.getOperator());
            vo.setPlanType(pp.getPlanType());
            vo.setMonthlyFee(pp.getStandardFee());
            vo.setDiscountFee(pp.getDiscountFee());
            vo.setDiscountMonths(pp.getDiscountMonths());
            vo.setAfterDiscountFee(pp.getAfterDiscountFee());
            vo.setEquipmentFee(pp.getEquipmentFee());
            vo.setInstallationFee(pp.getInstallationFee());
            vo.setContractMonths(pp.getContractMonths());
            items.add(vo);
        }

        // 5. 对比周期
        int comparisonMonths = req.getComparisonMonths() != null && req.getComparisonMonths() > 0
                ? req.getComparisonMonths()
                : (maxMonths > 0 ? maxMonths : 12);
        if (comparisonMonths <= 0) {
            comparisonMonths = 12;
        }

        // 6. 推荐方案周期总费用
        BigDecimal recommendedTotal = ZERO;
        for (PlanProduct pp : products) {
            int dm = pp.getDiscountMonths() == null ? 0 : pp.getDiscountMonths();
            if (dm > 0) {
                BigDecimal recM = Money.nz(pp.getDiscountFee());
                BigDecimal afterM = Money.nz(pp.getAfterDiscountFee());
                int remaining = Math.max(0, comparisonMonths - dm);
                recommendedTotal = recommendedTotal
                        .add(recM.multiply(BigDecimal.valueOf(dm)))
                        .add(afterM.multiply(BigDecimal.valueOf(remaining)));
            } else {
                recommendedTotal = recommendedTotal.add(
                        Money.nz(pp.getStandardFee()).multiply(BigDecimal.valueOf(comparisonMonths)));
            }
        }
        recommendedTotal = recommendedTotal.add(oneTime);

        // 7-8. 当前方案周期总费用与差额
        BigDecimal currentTotal = currentMonthly.multiply(BigDecimal.valueOf(comparisonMonths));
        BigDecimal estimatedSaving = currentTotal.subtract(recommendedTotal);
        String conclusion = verify ? "VERIFY" : (estimatedSaving.compareTo(ZERO) > 0 ? "SAVING" : "NO_SAVING");

        String reason = buildReason(req.getCustomerId(), currentPlans, products);

        PlanAnalysisResultDTO dto = new PlanAnalysisResultDTO();
        dto.setCustomerId(req.getCustomerId());
        dto.setCurrentMonthlyFee(Money.scale2(currentMonthly));
        dto.setRecommendedMonthlyFee(Money.scale2(recommendedMonthly));
        dto.setAfterDiscountMonthlyFee(Money.scale2(afterDiscountMonthly));
        dto.setOneTimeFee(Money.scale2(oneTime));
        dto.setComparisonMonths(comparisonMonths);
        dto.setCurrentTotalFee(Money.scale2(currentTotal));
        dto.setRecommendedTotalFee(Money.scale2(recommendedTotal));
        dto.setEstimatedSaving(Money.scale2(estimatedSaving));
        dto.setEstimated(estimated);
        dto.setConclusion(conclusion);
        dto.setRecommendationReason(reason);
        dto.setItems(items);
        return dto;
    }

    private String buildReason(Long customerId, List<CustomerPlan> currentPlans, List<PlanProduct> products) {
        List<String> reasons = new ArrayList<>();
        long phoneCount = currentPlans.stream().filter(p -> "手机套餐".equals(p.getPlanType())).count();
        if (phoneCount >= 2) {
            reasons.add("当前家庭有多个手机套餐，可考虑办理家庭组合/共享套餐，统一管理更省心。");
        }
        boolean lowBroadband = currentPlans.stream()
                .anyMatch(p -> "家庭宽带".equals(p.getPlanType())
                        && p.getBroadbandSpeed() != null && p.getBroadbandSpeed() < 100);
        if (lowBroadband) {
            reasons.add("当前宽带速率偏低，结合家庭设备数量可评估提速方案。");
        }
        List<CustomerMember> members = customerMemberMapper.selectList(
                new QueryWrapper<CustomerMember>().eq("customer_id", customerId));
        BigDecimal familyData = members.stream()
                .map(CustomerMember::getMonthlyDataGb).map(Money::nz)
                .reduce(ZERO, BigDecimal::add);
        if (familyData.compareTo(new BigDecimal("50")) >= 0) {
            reasons.add("家庭成员流量需求较高（合计约 " + familyData + "GB），可考虑大流量或流量共享方案。");
        }
        if (products.isEmpty()) {
            reasons.add("尚未选择推荐套餐组合，请在上方勾选合适的套餐进行费用测算。");
        }
        if (reasons.isEmpty()) {
            reasons.add("当前方案与需求基本匹配，可结合客户实际使用情况进一步沟通。");
        }
        return String.join("\n", reasons);
    }

    /**
     * 计算 + 价格快照落库
     */
    public PlanAnalysisResultDTO analyzeAndSave(PlanAnalysisRequestDTO req) {
        PlanAnalysisResultDTO result = analyze(req);
        PlanAnalysis analysis = new PlanAnalysis();
        analysis.setCustomerId(req.getCustomerId());
        analysis.setAnalysisNo(generateNo());
        analysis.setCurrentMonthlyFee(result.getCurrentMonthlyFee());
        analysis.setRecommendedMonthlyFee(result.getRecommendedMonthlyFee());
        analysis.setAfterDiscountMonthlyFee(result.getAfterDiscountMonthlyFee());
        analysis.setOneTimeFee(result.getOneTimeFee());
        analysis.setComparisonMonths(result.getComparisonMonths());
        analysis.setCurrentTotalFee(result.getCurrentTotalFee());
        analysis.setRecommendedTotalFee(result.getRecommendedTotalFee());
        analysis.setEstimatedSaving(result.getEstimatedSaving());
        analysis.setRecommendationReason(result.getRecommendationReason());
        analysis.setCreatedAt(LocalDateTime.now());
        planAnalysisMapper.insert(analysis);

        for (PlanAnalysisItemVO vo : result.getItems()) {
            PlanAnalysisItem item = new PlanAnalysisItem();
            item.setAnalysisId(analysis.getId());
            item.setProductId(vo.getProductId());
            item.setProductName(vo.getProductName());
            item.setOperator(vo.getOperator());
            item.setPlanType(vo.getPlanType());
            item.setMonthlyFee(vo.getMonthlyFee());
            item.setDiscountFee(vo.getDiscountFee());
            item.setDiscountMonths(vo.getDiscountMonths());
            item.setAfterDiscountFee(vo.getAfterDiscountFee());
            item.setEquipmentFee(vo.getEquipmentFee());
            item.setInstallationFee(vo.getInstallationFee());
            item.setContractMonths(vo.getContractMonths());
            planAnalysisItemMapper.insert(item);
        }
        result.setAnalysisId(analysis.getId());
        return result;
    }

    public IPage<PlanAnalysis> history(Page<PlanAnalysis> page, Long customerId) {
        QueryWrapper<PlanAnalysis> w = new QueryWrapper<>();
        if (customerId != null) {
            w.eq("customer_id", customerId);
        }
        w.orderByDesc("created_at");
        return planAnalysisMapper.selectPage(page, w);
    }

    public PlanAnalysis getAnalysis(Long id) {
        return planAnalysisMapper.selectById(id);
    }

    public List<PlanAnalysisItem> getItems(Long analysisId) {
        return planAnalysisItemMapper.selectList(new QueryWrapper<PlanAnalysisItem>().eq("analysis_id", analysisId));
    }

    public void updateFeedback(Long id, String feedback, String dealStatus) {
        PlanAnalysis a = planAnalysisMapper.selectById(id);
        if (a == null) {
            throw new BizException("分析记录不存在");
        }
        a.setCustomerFeedback(feedback);
        a.setDealStatus(dealStatus);
        planAnalysisMapper.updateById(a);
    }

    private String generateNo() {
        return "AN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
