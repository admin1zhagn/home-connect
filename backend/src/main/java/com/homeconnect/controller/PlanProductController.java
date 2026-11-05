package com.homeconnect.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeconnect.common.R;
import com.homeconnect.entity.PlanProduct;
import com.homeconnect.service.PlanProductService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class PlanProductController {

    private final PlanProductService service;

    public PlanProductController(PlanProductService service) {
        this.service = service;
    }

    @GetMapping("/page")
    public R<IPage<PlanProduct>> page(@RequestParam(defaultValue = "1") long page,
                                     @RequestParam(defaultValue = "10") long size,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String operator,
                                     @RequestParam(required = false) String planType,
                                     @RequestParam(required = false) Integer status) {
        return R.ok(service.page(new Page<>(page, size), keyword, operator, planType, status));
    }

    @GetMapping("/{id}")
    public R<PlanProduct> get(@PathVariable Long id) {
        return R.ok(service.get(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PlanProduct p) {
        service.save(p);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PlanProduct p) {
        service.save(p);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> del(@PathVariable Long id) {
        service.remove(id);
        return R.ok();
    }
}
