package com.invoiceissuer.controller;

import com.invoiceissuer.model.RuleModel;
import com.invoiceissuer.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("rule")
public class RuleController {

    private final RuleService ruleService;

    @Autowired
    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping(path = "addOne")
    public void addDescription(@RequestBody RuleModel rule) {
        ruleService.addRule(rule);
    }

    @PostMapping(path = "addMany")
    public void addDescriptionList(@RequestBody List<RuleModel> ruleList) {
        for(RuleModel rule : ruleList) {
            ruleService.addRule(rule);
        }
    }
}
