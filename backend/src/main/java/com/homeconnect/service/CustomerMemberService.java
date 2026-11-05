package com.homeconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homeconnect.entity.CustomerMember;
import com.homeconnect.mapper.CustomerMemberMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerMemberService {

    private final CustomerMemberMapper memberMapper;

    public CustomerMemberService(CustomerMemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public List<CustomerMember> listByCustomer(Long customerId) {
        return memberMapper.selectList(new QueryWrapper<CustomerMember>()
                .eq("customer_id", customerId).orderByAsc("id"));
    }

    public void save(CustomerMember m) {
        if (m.getId() == null) {
            memberMapper.insert(m);
        } else {
            memberMapper.updateById(m);
        }
    }

    public void remove(Long id) {
        memberMapper.deleteById(id);
    }
}
