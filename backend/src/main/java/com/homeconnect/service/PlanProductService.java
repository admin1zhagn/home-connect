package com.homeconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeconnect.entity.PlanProduct;
import com.homeconnect.mapper.PlanProductMapper;
import org.springframework.stereotype.Service;

@Service
public class PlanProductService {

    private final PlanProductMapper productMapper;

    public PlanProductService(PlanProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public IPage<PlanProduct> page(Page<PlanProduct> page, String keyword, String operator,
                                  String planType, Integer status) {
        QueryWrapper<PlanProduct> w = new QueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            w.like("plan_name", keyword).or().like("description", keyword);
        }
        if (operator != null && !operator.isBlank()) {
            w.eq("operator", operator);
        }
        if (planType != null && !planType.isBlank()) {
            w.eq("plan_type", planType);
        }
        if (status != null) {
            w.eq("status", status);
        }
        w.orderByDesc("created_at");
        return productMapper.selectPage(page, w);
    }

    public PlanProduct get(Long id) {
        return productMapper.selectById(id);
    }

    public void save(PlanProduct p) {
        if (p.getId() == null) {
            if (p.getStatus() == null) {
                p.setStatus(1);
            }
            productMapper.insert(p);
        } else {
            productMapper.updateById(p);
        }
    }

    public void remove(Long id) {
        productMapper.deleteById(id);
    }
}
