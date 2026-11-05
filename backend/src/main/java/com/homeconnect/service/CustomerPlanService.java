package com.homeconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homeconnect.entity.CustomerPlan;
import com.homeconnect.mapper.CustomerPlanMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerPlanService {

    private final CustomerPlanMapper planMapper;

    public CustomerPlanService(CustomerPlanMapper planMapper) {
        this.planMapper = planMapper;
    }

    public List<CustomerPlan> listByCustomer(Long customerId) {
        return planMapper.selectList(new QueryWrapper<CustomerPlan>()
                .eq("customer_id", customerId).orderByAsc("id"));
    }

    public void save(CustomerPlan p) {
        if (p.getId() == null) {
            planMapper.insert(p);
        } else {
            planMapper.updateById(p);
        }
    }

    public void remove(Long id) {
        planMapper.deleteById(id);
    }
}
