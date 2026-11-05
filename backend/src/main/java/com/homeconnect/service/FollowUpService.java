package com.homeconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homeconnect.dto.FollowUpVO;
import com.homeconnect.entity.Customer;
import com.homeconnect.entity.CustomerServiceRecord;
import com.homeconnect.mapper.CustomerMapper;
import com.homeconnect.mapper.CustomerServiceRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FollowUpService {

    private final CustomerMapper customerMapper;
    private final CustomerServiceRecordMapper recordMapper;

    public FollowUpService(CustomerMapper customerMapper, CustomerServiceRecordMapper recordMapper) {
        this.customerMapper = customerMapper;
        this.recordMapper = recordMapper;
    }

    /**
     * scope: today | overdue | all
     */
    public List<FollowUpVO> list(String scope) {
        LocalDate today = LocalDate.now();
        List<FollowUpVO> list = new ArrayList<>();

        List<Customer> customers = customerMapper.selectList(new QueryWrapper<Customer>()
                .and(w -> w.isNotNull("next_contact_at").or().eq("status", "待跟进")));
        for (Customer c : customers) {
            FollowUpVO v = new FollowUpVO();
            v.setSourceType("CUSTOMER");
            v.setId(c.getId());
            v.setCustomerId(c.getId());
            v.setCustomerName(c.getCustomerName());
            v.setDueDate(c.getNextContactAt());
            v.setTitle("待跟进".equals(c.getStatus()) ? "待跟进客户" : "约定下次联系");
            v.setStatus(c.getStatus());
            boolean overdue = c.getNextContactAt() != null
                    && c.getNextContactAt().toLocalDate().isBefore(today);
            v.setOverdue(overdue);
            if (matchScope(scope, c.getNextContactAt(), today, overdue)) {
                list.add(v);
            }
        }

        List<CustomerServiceRecord> recs = recordMapper.selectList(new QueryWrapper<CustomerServiceRecord>()
                .isNotNull("follow_up_date").ne("status", "已完成"));
        Map<Long, String> nameMap = new HashMap<>();
        for (Customer cc : customerMapper.selectList(null)) {
            nameMap.put(cc.getId(), cc.getCustomerName());
        }
        for (CustomerServiceRecord r : recs) {
            FollowUpVO v = new FollowUpVO();
            v.setSourceType("SERVICE");
            v.setId(r.getId());
            v.setCustomerId(r.getCustomerId());
            v.setCustomerName(nameMap.getOrDefault(r.getCustomerId(), ""));
            v.setDueDate(r.getFollowUpDate());
            v.setTitle(r.getServiceType());
            v.setStatus(r.getStatus());
            boolean overdue = r.getFollowUpDate() != null
                    && r.getFollowUpDate().toLocalDate().isBefore(today);
            v.setOverdue(overdue);
            if (matchScope(scope, r.getFollowUpDate(), today, overdue)) {
                list.add(v);
            }
        }

        list.sort((a, b) -> {
            if (a.getDueDate() == null) {
                return 1;
            }
            if (b.getDueDate() == null) {
                return -1;
            }
            return a.getDueDate().compareTo(b.getDueDate());
        });
        return list;
    }

    private boolean matchScope(String scope, LocalDateTime due, LocalDate today, boolean overdue) {
        if (scope == null || "all".equals(scope)) {
            return true;
        }
        if (due == null) {
            return "overdue".equals(scope) && overdue;
        }
        LocalDate d = due.toLocalDate();
        if ("today".equals(scope)) {
            return d.isEqual(today);
        }
        if ("overdue".equals(scope)) {
            return d.isBefore(today);
        }
        return true;
    }
}
