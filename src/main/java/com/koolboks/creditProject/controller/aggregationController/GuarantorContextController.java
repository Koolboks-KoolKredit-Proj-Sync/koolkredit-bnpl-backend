package com.koolboks.creditProject.controller.aggregationController;

import com.koolboks.creditProject.service.aggregationService.GuarantorContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guarantor")
public class GuarantorContextController {

    private final GuarantorContextService service;

    public GuarantorContextController(GuarantorContextService service) {
        this.service = service;
    }

    @GetMapping("/context")
    public ResponseEntity<?> getContext(@RequestParam String bvn) {
        return ResponseEntity.ok(service.getContextByBvn(bvn));
    }
}
