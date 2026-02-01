package com.koolboks.creditProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StaticPageController {

    @GetMapping("/guarantor/confirm/{guarantorId}")
    public String confirmGuarantorPage(@PathVariable Long guarantorId) {
        return "forward:/guarantor-confirm.html";
    }
}