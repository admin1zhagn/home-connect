package com.homeconnect.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeconnect.common.R;
import com.homeconnect.dto.PlanAnalysisRequestDTO;
import com.homeconnect.dto.PlanAnalysisResultDTO;
import com.homeconnect.entity.PlanAnalysis;
import com.homeconnect.entity.PlanAnalysisItem;
import com.homeconnect.service.PlanAnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class PlanAnalysisController {

    private final PlanAnalysisService service;

    public PlanAnalysisController(PlanAnalysisService service) {
        this.service = service;
    }

    @PostMapping("/preview")
    public R<PlanAnalysisResultDTO> preview(@RequestBody PlanAnalysisRequestDTO req) {
        return R.ok(service.analyze(req));
    }

    @PostMapping
    public R<PlanAnalysisResultDTO> save(@RequestBody PlanAnalysisRequestDTO req) {
        return R.ok(service.analyzeAndSave(req));
    }

    @GetMapping("/page")
    public R<IPage<PlanAnalysis>> page(@RequestParam(defaultValue = "1") long page,
                                      @RequestParam(defaultValue = "10") long size,
                                      @RequestParam(required = false) Long customerId) {
        return R.ok(service.history(new Page<>(page, size), customerId));
    }

    @GetMapping("/{id}")
    public R<Map<String, Object>> get(@PathVariable Long id) {
        Map<String, Object> m = new HashMap<>();
        m.put("analysis", service.getAnalysis(id));
        m.put("items", service.getItems(id));
        return R.ok(m);
    }

    @PutMapping("/{id}/feedback")
    public R<Void> feedback(@PathVariable Long id,
                            @RequestParam(required = false) String feedback,
                            @RequestParam(required = false) String dealStatus) {
        service.updateFeedback(id, feedback, dealStatus);
        return R.ok();
    }
}
