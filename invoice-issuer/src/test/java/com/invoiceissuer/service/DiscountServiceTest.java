package com.invoiceissuer.service;

import com.invoiceissuer.dao.DiscountDao;
import com.invoiceissuer.model.DiscountModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DiscountServiceTest {

    @InjectMocks
    private DiscountService discountService;

    @Mock
    private DiscountDao discountDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddingDiscount() {
        DiscountModel discount = new DiscountModel();
        discountService.addDiscount(discount);
        verify(discountDao, times(1)).save(discount);
    }
}
