package com.homeconnect.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homeconnect.entity.Customer;
import com.homeconnect.entity.CustomerConsent;
import com.homeconnect.entity.CustomerMember;
import com.homeconnect.entity.CustomerPlan;
import com.homeconnect.entity.CustomerServiceRecord;
import com.homeconnect.entity.PlanAnalysis;
import com.homeconnect.entity.PlanAnalysisItem;
import com.homeconnect.entity.PlanProduct;
import com.homeconnect.entity.SysUser;
import com.homeconnect.mapper.CustomerConsentMapper;
import com.homeconnect.mapper.CustomerMapper;
import com.homeconnect.mapper.CustomerMemberMapper;
import com.homeconnect.mapper.CustomerPlanMapper;
import com.homeconnect.mapper.CustomerServiceRecordMapper;
import com.homeconnect.mapper.PlanAnalysisItemMapper;
import com.homeconnect.mapper.PlanAnalysisMapper;
import com.homeconnect.mapper.PlanProductMapper;
import com.homeconnect.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final CustomerMapper customerMapper;
    private final CustomerMemberMapper customerMemberMapper;
    private final CustomerPlanMapper customerPlanMapper;
    private final PlanProductMapper planProductMapper;
    private final CustomerServiceRecordMapper customerServiceRecordMapper;
    private final CustomerConsentMapper customerConsentMapper;
    private final PlanAnalysisMapper planAnalysisMapper;
    private final PlanAnalysisItemMapper planAnalysisItemMapper;

    private final String adminUsername;
    private final String adminPassword;
    private final boolean demoSeed;

    public DataInitializer(SysUserMapper sysUserMapper,
                           PasswordEncoder passwordEncoder,
                           CustomerMapper customerMapper,
                           CustomerMemberMapper customerMemberMapper,
                           CustomerPlanMapper customerPlanMapper,
                           PlanProductMapper planProductMapper,
                           CustomerServiceRecordMapper customerServiceRecordMapper,
                           CustomerConsentMapper customerConsentMapper,
                           PlanAnalysisMapper planAnalysisMapper,
                           PlanAnalysisItemMapper planAnalysisItemMapper,
                           @Value("${app.security.admin-username}") String adminUsername,
                           @Value("${app.security.admin-password}") String adminPassword,
                           @Value("${app.demo.seed:true}") boolean demoSeed) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.customerMapper = customerMapper;
        this.customerMemberMapper = customerMemberMapper;
        this.customerPlanMapper = customerPlanMapper;
        this.planProductMapper = planProductMapper;
        this.customerServiceRecordMapper = customerServiceRecordMapper;
        this.customerConsentMapper = customerConsentMapper;
        this.planAnalysisMapper = planAnalysisMapper;
        this.planAnalysisItemMapper = planAnalysisItemMapper;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.demoSeed = demoSeed;
    }

    @Override
    public void run(String... args) {
        if (sysUserMapper.selectCount(null) == 0) {
            SysUser user = new SysUser();
            user.setUsername(adminUsername);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setEnabled(1);
            sysUserMapper.insert(user);
        }
        if (demoSeed) {
            // 哨兵：演示客户"张伟"不存在时才播种，保证只跑一次（不会覆盖用户后续录入的数据）
            Customer sentinel = customerMapper.selectOne(
                    new QueryWrapper<Customer>().eq("customer_name", "张伟").last("limit 1"));
            if (sentinel == null) {
                seedDemoData();
            }
        }
    }

    private void seedDemoData() {
        // 清空既有业务数据（数据库无外键约束，可安全整体重建）
        customerMapper.delete(null);
        customerMemberMapper.delete(null);
        customerPlanMapper.delete(null);
        planProductMapper.delete(null);
        customerServiceRecordMapper.delete(null);
        customerConsentMapper.delete(null);
        planAnalysisMapper.delete(null);
        planAnalysisItemMapper.delete(null);

        final LocalDateTime now = LocalDateTime.now();

        // ============ 1. 运营商套餐库 ============
        PlanProduct p1 = product("中国移动", "全家享套餐199", "手机套餐",
                new BigDecimal("199"), new BigDecimal("159"), 12, new BigDecimal("199"),
                new BigDecimal("60"), 1000, 300, 2, 12, BigDecimal.ZERO, BigDecimal.ZERO,
                "合约期12个月，含2张副卡+300M宽带", "主卡+2张副卡+300M宽带融合", "营业厅2026-10核实");
        PlanProduct p2 = product("中国移动", "宽带融合129", "家庭宽带",
                new BigDecimal("129"), new BigDecimal("99"), 12, new BigDecimal("129"),
                null, null, 300, 0, 12, BigDecimal.ZERO, BigDecimal.ZERO,
                "合约期12个月", "单宽带融合套餐", "营业厅2026-10核实");
        PlanProduct p3 = product("中国联通", "冰激凌99元套餐", "手机套餐",
                new BigDecimal("99"), new BigDecimal("79"), 6, new BigDecimal("99"),
                new BigDecimal("30"), 500, null, 0, 12, BigDecimal.ZERO, BigDecimal.ZERO,
                "前6个月优惠价79", "大流量手机套餐", "官网2026-10核实");
        PlanProduct p4 = product("中国电信", "十全十美199融合", "手机套餐",
                new BigDecimal("199"), new BigDecimal("169"), 12, new BigDecimal("199"),
                new BigDecimal("80"), 1500, 500, 0, 24, BigDecimal.ZERO, BigDecimal.ZERO,
                "合约期24个月，含500M宽带", "全家通信+宽带一体", "营业厅2026-10核实");
        PlanProduct p5 = product("中国移动", "家庭副卡10元", "家庭副卡",
                new BigDecimal("10"), new BigDecimal("10"), 0, new BigDecimal("10"),
                null, null, null, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO,
                "需主卡办理，无合约", "共享主卡资源", "营业厅2026-10核实");
        PlanProduct p6 = product("中国电信", "宽带500M 159", "家庭宽带",
                new BigDecimal("159"), new BigDecimal("159"), 12, new BigDecimal("159"),
                null, null, 500, 0, 12, BigDecimal.ZERO, BigDecimal.ZERO,
                "合约期12个月", "单宽带高带宽", "官网2026-10核实");
        PlanProduct p7 = product("中国联通", "大王卡19元", "手机套餐",
                new BigDecimal("19"), new BigDecimal("19"), 12, new BigDecimal("19"),
                BigDecimal.ZERO, 0, null, 0, 12, BigDecimal.ZERO, BigDecimal.ZERO,
                "定向免流，通用流量另计", "低消费保号套餐", "官网2026-10核实");
        PlanProduct p8 = product("中国移动", "5G智享299", "手机套餐",
                new BigDecimal("299"), new BigDecimal("239"), 12, new BigDecimal("299"),
                new BigDecimal("100"), 2000, 1000, 0, 24, BigDecimal.ZERO, BigDecimal.ZERO,
                "合约期24个月，含1000M宽带", "高端全家享", "营业厅2026-10核实");
        for (PlanProduct p : new PlanProduct[]{p1, p2, p3, p4, p5, p6, p7, p8}) {
            planProductMapper.insert(p);
        }

        // ============ 2. 客户 + 家庭成员 + 当前套餐 ============
        // 张伟（厦门 思明区，家庭4人，套餐偏贵，强省钱潜力）
        Customer c1 = customer("张伟", "13805920001", "瑞景社区", "自主拓展", "新客户",
                now.plusDays(3), "业主，家中4口，对降费感兴趣");
        customerMapper.insert(c1);
        Long c1m1 = addMember(c1.getId(), "张伟", "本人", "13805920001", "移动", new BigDecimal("99"), new BigDecimal("40"), 800, "主卡");
        addMember(c1.getId(), "林芳", "配偶", "13805920002", "移动", new BigDecimal("59"), new BigDecimal("15"), 300, "副卡");
        addMember(c1.getId(), "张小宝", "儿子", "13805920003", "移动", new BigDecimal("39"), new BigDecimal("20"), 200, "副卡/学生");
        addMember(c1.getId(), "王秀英", "母亲", "13805920004", "移动", new BigDecimal("19"), new BigDecimal("5"), 100, "老年机");
        addPlan(c1.getId(), c1m1, "全球通128", "中国移动", "手机套餐",
                new BigDecimal("128"), new BigDecimal("20"), new BigDecimal("30"), 500, null,
                LocalDate.now().plusMonths(8), 12, BigDecimal.ZERO, "exact", "active", "主卡套餐");
        addPlan(c1.getId(), null, "中国电信200M宽带", "中国电信", "家庭宽带",
                new BigDecimal("80"), BigDecimal.ZERO, null, null, 200,
                LocalDate.now().plusMonths(5), 12, BigDecimal.ZERO, "exact", "active", "家庭宽带");
        addPlan(c1.getId(), null, "中国移动副卡", "中国移动", "家庭副卡",
                new BigDecimal("10"), BigDecimal.ZERO, null, null, null,
                null, 0, BigDecimal.ZERO, "exact", "active", "两张副卡");
        addServiceRecord(c1.getId(), "套餐咨询", now.minusDays(6), "上门介绍全家享融合方案", "已出初步方案", now.plusDays(3), "已完成");
        addConsent(c1.getId(), "套餐分析", 1, "当前套餐与家庭成员用量", now.minusDays(6), "客户口头同意");

        // 李娜（厦门 湖里区，三口之家）
        Customer c2 = customer("李娜", "13905920002", "金山社区", "熟人介绍", "已沟通",
                now.plusDays(1), "朋友介绍，关注宽带提速");
        customerMapper.insert(c2);
        Long c2m1 = addMember(c2.getId(), "李娜", "本人", "13905920002", "联通", new BigDecimal("89"), new BigDecimal("25"), 600, "主卡");
        addMember(c2.getId(), "陈浩", "配偶", "13905920003", "联通", new BigDecimal("49"), new BigDecimal("10"), 200, "副卡");
        addMember(c2.getId(), "陈乐乐", "女儿", "13905920004", "联通", new BigDecimal("29"), new BigDecimal("8"), 100, "儿童手表");
        addPlan(c2.getId(), c2m1, "联通99元套餐", "中国联通", "手机套餐",
                new BigDecimal("99"), BigDecimal.ZERO, new BigDecimal("20"), 400, null,
                LocalDate.now().plusMonths(3), 12, BigDecimal.ZERO, "exact", "active", "主卡");
        addPlan(c2.getId(), null, "中国移动200M宽带", "中国移动", "家庭宽带",
                new BigDecimal("80"), BigDecimal.ZERO, null, null, 200,
                LocalDate.now().plusMonths(6), 12, BigDecimal.ZERO, "exact", "active", "家庭宽带");
        addServiceRecord(c2.getId(), "续约提醒", now.minusDays(10), "宽带即将到期提醒", "已告知新套餐", now.plusDays(1), "待跟进");
        addConsent(c2.getId(), "信息收集", 1, "联系方式与套餐信息", now.minusDays(10), "签字确认");

        // 王强（厦门 集美区，夫妻二人）
        Customer c3 = customer("王强", "13705920003", "杏林社区", "客户主动咨询", "待跟进",
                now.plusDays(7), "主动来电，想对比携号转网方案");
        customerMapper.insert(c3);
        Long c3m1 = addMember(c3.getId(), "王强", "本人", "13705920003", "移动", new BigDecimal("109"), new BigDecimal("35"), 700, "主卡");
        addMember(c3.getId(), "赵丽", "配偶", "13705920004", "移动", new BigDecimal("59"), new BigDecimal("12"), 150, "副卡");
        addPlan(c3.getId(), c3m1, "移动88元套餐", "中国移动", "手机套餐",
                new BigDecimal("88"), BigDecimal.ZERO, new BigDecimal("20"), 300, null,
                LocalDate.now().plusMonths(10), 12, BigDecimal.ZERO, "exact", "active", "主卡");
        addServiceRecord(c3.getId(), "携号转网咨询", now.minusDays(2), "咨询转网政策与资费", "待进一步测算", null, "进行中");

        // 陈静（厦门 海沧区，单亲+孩子）
        Customer c4 = customer("陈静", "13605920004", "未来海岸社区", "公司工作接触", "新客户",
                now.plusDays(2), "公司同事，单亲妈妈，预算敏感");
        customerMapper.insert(c4);
        Long c4m1 = addMember(c4.getId(), "陈静", "本人", "13605920004", "电信", new BigDecimal("79"), new BigDecimal("18"), 400, "主卡");
        addMember(c4.getId(), "陈果", "儿子", "13605920005", "电信", new BigDecimal("29"), new BigDecimal("10"), 100, "儿童手表");
        addPlan(c4.getId(), c4m1, "电信59元套餐", "中国电信", "手机套餐",
                new BigDecimal("59"), BigDecimal.ZERO, new BigDecimal("15"), 200, null,
                LocalDate.now().plusMonths(4), 12, BigDecimal.ZERO, "exact", "active", "主卡");
        addPlan(c4.getId(), null, "电信100M宽带", "中国电信", "家庭宽带",
                new BigDecimal("60"), BigDecimal.ZERO, null, null, 100,
                LocalDate.now().plusMonths(4), 12, BigDecimal.ZERO, "exact", "active", "家庭宽带");
        addConsent(c4.getId(), "套餐分析", 1, "降费方案测算", now.minusDays(1), "微信确认");

        // 刘洋（厦门 同安区，三代同堂，已成交）
        Customer c5 = customer("刘洋", "13505920005", "大同社区", "自主拓展", "已成交",
                now.plusDays(14), "已办理全家享套餐，满意度高");
        customerMapper.insert(c5);
        Long c5m1 = addMember(c5.getId(), "刘洋", "本人", "13505920005", "移动", new BigDecimal("129"), new BigDecimal("45"), 900, "主卡");
        addMember(c5.getId(), "刘父", "父亲", "13505920006", "移动", new BigDecimal("19"), new BigDecimal("5"), 80, "老年机");
        addMember(c5.getId(), "刘母", "母亲", "13505920007", "移动", new BigDecimal("19"), new BigDecimal("5"), 80, "老年机");
        addPlan(c5.getId(), c5m1, "全家享199", "中国移动", "手机套餐",
                new BigDecimal("159"), BigDecimal.ZERO, new BigDecimal("60"), 1000, 300,
                LocalDate.now().plusMonths(11), 12, BigDecimal.ZERO, "exact", "active", "已办理优惠套餐");
        addServiceRecord(c5.getId(), "上门办理", now.minusDays(20), "完成全家享套餐办理", "已激活使用", null, "已完成");
        addConsent(c5.getId(), "服务记录", 1, "办理过程记录", now.minusDays(20), "签字确认");

        // 赵敏（厦门 翔安区，小店主，暂无需求）
        Customer c6 = customer("赵敏", "13405920006", "新店社区", "其他", "暂无需求",
                null, "经营小超市，暂时不需要家庭套餐");
        customerMapper.insert(c6);
        Long c6m1 = addMember(c6.getId(), "赵敏", "本人", "13405920006", "联通", new BigDecimal("69"), new BigDecimal("20"), 300, "主卡");
        addPlan(c6.getId(), c6m1, "联通59元套餐", "中国联通", "手机套餐",
                new BigDecimal("59"), BigDecimal.ZERO, new BigDecimal("10"), 200, null,
                LocalDate.now().plusMonths(9), 12, BigDecimal.ZERO, "exact", "active", "主卡");

        // ============ 3. 套餐分析记录（推荐历史） ============
        // 张伟 - 省钱方案（SAVING）
        List<PlanAnalysisItem> items11 = new ArrayList<>();
        items11.add(item(p1.getId(), p1.getPlanName(), p1.getOperator(), p1.getPlanType(),
                p1.getStandardFee(), p1.getDiscountFee(), p1.getDiscountMonths(),
                p1.getAfterDiscountFee(), p1.getEquipmentFee(), p1.getInstallationFee(), p1.getContractMonths()));
        analysis(c1.getId(), "AN" + ymd(now.minusDays(5)) + "001",
                new BigDecimal("238"), new BigDecimal("159"), new BigDecimal("199"), BigDecimal.ZERO, 12,
                new BigDecimal("2856"), new BigDecimal("1908"), new BigDecimal("948"),
                "当前家庭手机+宽带+副卡月支出238元，办理全家享199（优惠期159/月，含2张副卡与300M宽带）可统一管理并降费。",
                "客户考虑中，约3天后回访", "跟进中", now.minusDays(5), items11);

        // 张伟 - 需核实方案（VERIFY，副卡单独办理需核实条件）
        List<PlanAnalysisItem> items12 = new ArrayList<>();
        items12.add(item(p1.getId(), p1.getPlanName(), p1.getOperator(), p1.getPlanType(),
                p1.getStandardFee(), p1.getDiscountFee(), p1.getDiscountMonths(),
                p1.getAfterDiscountFee(), p1.getEquipmentFee(), p1.getInstallationFee(), p1.getContractMonths()));
        items12.add(item(p5.getId(), p5.getPlanName(), p5.getOperator(), p5.getPlanType(),
                p5.getStandardFee(), p5.getDiscountFee(), p5.getDiscountMonths(),
                p5.getAfterDiscountFee(), p5.getEquipmentFee(), p5.getInstallationFee(), p5.getContractMonths()));
        analysis(c1.getId(), "AN" + ymd(now.minusDays(3)) + "002",
                new BigDecimal("238"), new BigDecimal("169"), new BigDecimal("209"), BigDecimal.ZERO, 12,
                new BigDecimal("2856"), new BigDecimal("2028"), new BigDecimal("828"),
                "在全家享基础上增办副卡，需向客户核实副卡办理条件与合约细节。",
                "待核实副卡政策", "未成交", now.minusDays(3), items12);

        // 李娜 - 融合提速方案（SAVING，已成交）
        List<PlanAnalysisItem> items21 = new ArrayList<>();
        items21.add(item(p4.getId(), p4.getPlanName(), p4.getOperator(), p4.getPlanType(),
                p4.getStandardFee(), p4.getDiscountFee(), p4.getDiscountMonths(),
                p4.getAfterDiscountFee(), p4.getEquipmentFee(), p4.getInstallationFee(), p4.getContractMonths()));
        analysis(c2.getId(), "AN" + ymd(now.minusDays(2)) + "001",
                new BigDecimal("179"), new BigDecimal("169"), new BigDecimal("199"), BigDecimal.ZERO, 12,
                new BigDecimal("2148"), new BigDecimal("2028"), new BigDecimal("120"),
                "当前手机99+宽带80共179元，办理十全十美199（优惠期169/月，含500M宽带）可提速并小幅降费。",
                "接受推荐", "已成交", now.minusDays(2), items21);
    }

    // ---------------- 实体构建辅助 ----------------
    private PlanProduct product(String operator, String name, String type,
                                BigDecimal std, BigDecimal disc, int dm, BigDecimal after,
                                BigDecimal dataGb, Integer callMin, Integer bb, Integer sub, int cm,
                                BigDecimal install, BigDecimal equip, String cond, String desc, String note) {
        PlanProduct p = new PlanProduct();
        p.setOperator(operator);
        p.setPlanName(name);
        p.setPlanType(type);
        p.setStandardFee(std);
        p.setDiscountFee(disc);
        p.setDiscountMonths(dm);
        p.setAfterDiscountFee(after);
        p.setDataGb(dataGb);
        p.setCallMinutes(callMin);
        p.setBroadbandSpeed(bb);
        p.setSubCardCount(sub);
        p.setContractMonths(cm);
        p.setInstallationFee(install);
        p.setEquipmentFee(equip);
        p.setConditions(cond);
        p.setDescription(desc);
        p.setSourceNote(note);
        p.setStatus(1);
        p.setVerifiedAt(LocalDateTime.now());
        return p;
    }

    private Customer customer(String name, String phone, String community, String source,
                              String status, LocalDateTime next, String remark) {
        Customer c = new Customer();
        c.setCustomerName(name);
        c.setPhone(phone);
        c.setCommunity(community);
        c.setSource(source);
        c.setStatus(status);
        c.setNextContactAt(next);
        c.setRemark(remark);
        return c;
    }

    private Long addMember(Long cid, String name, String rel, String phone, String operator,
                           BigDecimal fee, BigDecimal data, Integer call, String remark) {
        CustomerMember m = new CustomerMember();
        m.setCustomerId(cid);
        m.setMemberName(name);
        m.setRelation(rel);
        m.setPhone(phone);
        m.setOperator(operator);
        m.setFee(fee);
        m.setMonthlyDataGb(data);
        m.setMonthlyCallMinutes(call);
        m.setRemark(remark);
        customerMemberMapper.insert(m);
        return m.getId();
    }

    private void addPlan(Long cid, Long mid, String name, String operator, String type,
                         BigDecimal monthly, BigDecimal extra, BigDecimal data, Integer call, Integer bb,
                         LocalDate contractEnd, Integer cm, BigDecimal equip, String feeAcc, String eff, String remark) {
        CustomerPlan p = new CustomerPlan();
        p.setCustomerId(cid);
        p.setMemberId(mid);
        p.setPlanName(name);
        p.setOperator(operator);
        p.setPlanType(type);
        p.setMonthlyFee(monthly);
        p.setExtraMonthlyFee(extra);
        p.setDataGb(data);
        p.setCallMinutes(call);
        p.setBroadbandSpeed(bb);
        p.setContractEndDate(contractEnd);
        p.setContractMonths(cm);
        p.setEquipmentFee(equip);
        p.setFeeAccuracy(feeAcc);
        p.setEffectiveStatus(eff);
        p.setRemark(remark);
        customerPlanMapper.insert(p);
    }

    private void addServiceRecord(Long cid, String type, LocalDateTime time, String content,
                                  String result, LocalDateTime follow, String status) {
        CustomerServiceRecord r = new CustomerServiceRecord();
        r.setCustomerId(cid);
        r.setServiceType(type);
        r.setServiceTime(time);
        r.setServiceContent(content);
        r.setServiceResult(result);
        r.setFollowUpDate(follow);
        r.setStatus(status);
        customerServiceRecordMapper.insert(r);
    }

    private void addConsent(Long cid, String type, int status, String scope,
                            LocalDateTime time, String remark) {
        CustomerConsent c = new CustomerConsent();
        c.setCustomerId(cid);
        c.setConsentType(type);
        c.setConsentStatus(status);
        c.setConsentScope(scope);
        c.setConsentTime(time);
        c.setRemark(remark);
        customerConsentMapper.insert(c);
    }

    private PlanAnalysisItem item(Long pid, String name, String operator, String type,
                                  BigDecimal std, BigDecimal disc, int dm, BigDecimal after,
                                  BigDecimal equip, BigDecimal install, int cm) {
        PlanAnalysisItem it = new PlanAnalysisItem();
        it.setProductId(pid);
        it.setProductName(name);
        it.setOperator(operator);
        it.setPlanType(type);
        it.setMonthlyFee(std);
        it.setDiscountFee(disc);
        it.setDiscountMonths(dm);
        it.setAfterDiscountFee(after);
        it.setEquipmentFee(equip);
        it.setInstallationFee(install);
        it.setContractMonths(cm);
        return it;
    }

    private void analysis(Long cid, String no, BigDecimal curMon, BigDecimal recMon, BigDecimal afterDisc,
                          BigDecimal oneTime, int months, BigDecimal curTot, BigDecimal recTot, BigDecimal saving,
                          String reason, String feedback, String deal, LocalDateTime created, List<PlanAnalysisItem> items) {
        PlanAnalysis a = new PlanAnalysis();
        a.setCustomerId(cid);
        a.setAnalysisNo(no);
        a.setCurrentMonthlyFee(curMon);
        a.setRecommendedMonthlyFee(recMon);
        a.setAfterDiscountMonthlyFee(afterDisc);
        a.setOneTimeFee(oneTime);
        a.setComparisonMonths(months);
        a.setCurrentTotalFee(curTot);
        a.setRecommendedTotalFee(recTot);
        a.setEstimatedSaving(saving);
        a.setRecommendationReason(reason);
        a.setCustomerFeedback(feedback);
        a.setDealStatus(deal);
        a.setCreatedAt(created);
        planAnalysisMapper.insert(a);
        for (PlanAnalysisItem it : items) {
            it.setAnalysisId(a.getId());
            planAnalysisItemMapper.insert(it);
        }
    }

    private String ymd(LocalDateTime t) {
        return String.format("%04d%02d%02d", t.getYear(), t.getMonthValue(), t.getDayOfMonth());
    }
}
