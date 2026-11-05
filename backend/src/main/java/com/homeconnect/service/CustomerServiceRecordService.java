package com.homeconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homeconnect.entity.CustomerServiceRecord;
import com.homeconnect.mapper.CustomerServiceRecordMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceRecordService {

    private final CustomerServiceRecordMapper recordMapper;

    public CustomerServiceRecordService(CustomerServiceRecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    public List<CustomerServiceRecord> listByCustomer(Long customerId) {
        return recordMapper.selectList(new QueryWrapper<CustomerServiceRecord>()
                .eq("customer_id", customerId).orderByDesc("service_time"));
    }

    public void save(CustomerServiceRecord r) {
        if (r.getId() == null) {
            recordMapper.insert(r);
        } else {
            recordMapper.updateById(r);
        }
    }

    public void remove(Long id) {
        recordMapper.deleteById(id);
    }
}
