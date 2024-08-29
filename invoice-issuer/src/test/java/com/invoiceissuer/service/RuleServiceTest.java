package com.invoiceissuer.service;

import com.invoiceissuer.dao.RuleDao;
import com.invoiceissuer.model.RuleModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RuleServiceTest {

    @InjectMocks
    private RuleService ruleService;

    @Mock
    private RuleDao ruleDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddingRule() {
        RuleModel rule = new RuleModel();
        ruleService.addRule(rule);
        verify(ruleDao, times(1)).save(rule);
    }
}
