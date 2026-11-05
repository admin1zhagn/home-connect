package com.homeconnect.controller;

import com.homeconnect.common.R;
import com.homeconnect.entity.CustomerPlan;
import com.homeconnect.service.CustomerPlanService;
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
@RequestMapping("/api/plans")
public class CustomerPlanController {

    private final CustomerPlanService service;

    public CustomerPlanController(CustomerPlanService service) {
        this.service = service;
    }

    @GetMapping
    public R<List<CustomerPlan>> list(@RequestParam Long customerId) {
        return R.ok(service.listByCustomer(customerId));
    }

    @PostMapping
    public R<Void> add(@RequestBody CustomerPlan p) {
        service.save(p);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody CustomerPlan p) {
        service.save(p);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> del(@PathVariable Long id) {
        service.remove(id);
        return R.ok();
    }
}
