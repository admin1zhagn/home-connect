package com.homeconnect.controller;

import com.homeconnect.common.R;
import com.homeconnect.dto.FollowUpVO;
import com.homeconnect.service.FollowUpService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpController {

    private final FollowUpService service;

    public FollowUpController(FollowUpService service) {
        this.service = service;
    }

    @GetMapping
    public R<List<FollowUpVO>> list(@RequestParam(required = false, defaultValue = "all") String scope) {
        return R.ok(service.list(scope));
    }
}
