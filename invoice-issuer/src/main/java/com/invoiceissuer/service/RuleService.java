package com.invoiceissuer.service;

import com.invoiceissuer.dao.RuleDao;
import com.invoiceissuer.model.RuleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    private final RuleDao ruleDao;

    @Autowired
    public RuleService(RuleDao ruleDao) {
        this.ruleDao = ruleDao;
    }

    public void addRule(RuleModel rule) {
        ruleDao.save(rule);
    }
}
