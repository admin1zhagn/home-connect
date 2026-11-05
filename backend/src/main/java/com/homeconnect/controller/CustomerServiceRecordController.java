package com.homeconnect.controller;

import com.homeconnect.common.R;
import com.homeconnect.entity.CustomerServiceRecord;
import com.homeconnect.service.CustomerServiceRecordService;
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
@RequestMapping("/api/records")
public class CustomerServiceRecordController {

    private final CustomerServiceRecordService service;

    public CustomerServiceRecordController(CustomerServiceRecordService service) {
        this.service = service;
    }

    @GetMapping
    public R<List<CustomerServiceRecord>> list(@RequestParam Long customerId) {
        return R.ok(service.listByCustomer(customerId));
    }

    @PostMapping
    public R<Void> add(@RequestBody CustomerServiceRecord r) {
        service.save(r);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody CustomerServiceRecord r) {
        service.save(r);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> del(@PathVariable Long id) {
        service.remove(id);
        return R.ok();
    }
}
