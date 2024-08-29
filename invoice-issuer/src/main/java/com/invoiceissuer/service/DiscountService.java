package com.invoiceissuer.service;

import com.invoiceissuer.dao.DiscountDao;
import com.invoiceissuer.model.DiscountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    private final DiscountDao discountDao;

    @Autowired
    public DiscountService(DiscountDao discountDao) {
        this.discountDao = discountDao;
    }

    public void addDiscount(DiscountModel discount) {
        discountDao.save(discount);
    }
}
