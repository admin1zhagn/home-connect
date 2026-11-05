package com.homeconnect.controller;

import com.homeconnect.common.R;
import com.homeconnect.dto.DashboardVO;
import com.homeconnect.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public R<DashboardVO> stats() {
        return R.ok(service.stats());
    }
}
