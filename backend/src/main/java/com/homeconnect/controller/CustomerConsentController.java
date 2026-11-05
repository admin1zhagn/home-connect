package com.homeconnect.controller;

import com.homeconnect.common.R;
import com.homeconnect.entity.CustomerConsent;
import com.homeconnect.service.CustomerConsentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/consents")
public class CustomerConsentController {

    private final CustomerConsentService service;

    public CustomerConsentController(CustomerConsentService service) {
        this.service = service;
    }

    @GetMapping
    public R<List<CustomerConsent>> list(@RequestParam Long customerId) {
        return R.ok(service.listByCustomer(customerId));
    }

    @PostMapping
    public R<Void> add(@RequestBody CustomerConsent c) {
        service.save(c);
        return R.ok();
    }

    @PutMapping("/{id}/revoke")
    public R<Void> revoke(@PathVariable Long id) {
        service.revoke(id);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> del(@PathVariable Long id) {
        service.remove(id);
        return R.ok();
    }
}
