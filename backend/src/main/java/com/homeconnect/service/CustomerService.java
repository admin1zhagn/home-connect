package com.homeconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeconnect.entity.Customer;
import com.homeconnect.mapper.CustomerMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CustomerService {

    private final CustomerMapper customerMapper;

    public CustomerService(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public IPage<Customer> page(Page<Customer> page, String keyword, String status, String source,
                               LocalDate start, LocalDate end) {
        QueryWrapper<Customer> w = new QueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            w.and(q -> q.like("customer_name", keyword)
                    .or().like("phone", keyword)
                    .or().like("community", keyword));
        }
        if (status != null && !status.isBlank()) {
            w.eq("status", status);
        }
        if (source != null && !source.isBlank()) {
            w.eq("source", source);
        }
        if (start != null) {
            w.ge("created_at", start.atStartOfDay());
        }
        if (end != null) {
            w.le("created_at", end.atTime(23, 59, 59));
        }
        w.orderByDesc("created_at");
        return customerMapper.selectPage(page, w);
    }

    public Customer get(Long id) {
        return customerMapper.selectById(id);
    }

    public void save(Customer c) {
        if (c.getId() == null) {
            customerMapper.insert(c);
        } else {
            customerMapper.updateById(c);
        }
    }

    public void remove(Long id) {
        customerMapper.deleteById(id);
    }

    public Long countAll() {
        return customerMapper.selectCount(new QueryWrapper<>());
    }

    public Long countByStatus(String status) {
        return customerMapper.selectCount(new QueryWrapper<Customer>().eq("status", status));
    }

    public Long countNewThisMonth() {
        LocalDate first = LocalDate.now().withDayOfMonth(1);
        return customerMapper.selectCount(new QueryWrapper<Customer>().ge("created_at", first.atStartOfDay()));
    }
}
