package com.homeconnect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homeconnect.entity.CustomerConsent;
import com.homeconnect.mapper.CustomerConsentMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerConsentService {

    private final CustomerConsentMapper consentMapper;

    public CustomerConsentService(CustomerConsentMapper consentMapper) {
        this.consentMapper = consentMapper;
    }

    public List<CustomerConsent> listByCustomer(Long customerId) {
        return consentMapper.selectList(new QueryWrapper<CustomerConsent>()
                .eq("customer_id", customerId).orderByDesc("consent_time"));
    }

    public void save(CustomerConsent c) {
        if (c.getId() == null) {
            if (c.getConsentStatus() == null) {
                c.setConsentStatus(1);
            }
            if (c.getConsentTime() == null) {
                c.setConsentTime(LocalDateTime.now());
            }
            consentMapper.insert(c);
        } else {
            consentMapper.updateById(c);
        }
    }

    public void revoke(Long id) {
        CustomerConsent c = consentMapper.selectById(id);
        if (c != null) {
            c.setConsentStatus(0);
            c.setRevokedAt(LocalDateTime.now());
            consentMapper.updateById(c);
        }
    }

    public void remove(Long id) {
        consentMapper.deleteById(id);
    }
}
