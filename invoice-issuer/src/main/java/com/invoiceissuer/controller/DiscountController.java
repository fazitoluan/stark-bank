package com.invoiceissuer.controller;

import com.invoiceissuer.model.DiscountModel;
import com.invoiceissuer.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("discount")
public class DiscountController {

    private final DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping(path = "addOne")
    public void addDescription(@RequestBody DiscountModel discount) {
        discountService.addDiscount(discount);
    }

    @PostMapping(path = "addMany")
    public void addDescriptionList(@RequestBody List<DiscountModel> discountList) {
        for(DiscountModel discount : discountList) {
            discountService.addDiscount(discount);
        }
    }
}
