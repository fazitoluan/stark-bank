package com.invoiceissuer.controller;

import com.invoiceissuer.model.InvoiceModel;
import com.invoiceissuer.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void issueInvoice() throws Exception {
        invoiceService.issueInvoice();
    }

    @PostMapping(path = "addOne")
    public void addInvoice(@RequestBody InvoiceModel invoice) {
        invoiceService.addInvoice(invoice);
    }

    @PostMapping(path = "addMany")
    public void addManyInvoices(@RequestBody List<InvoiceModel> invoiceList) {
        for (InvoiceModel invoice : invoiceList) {
            invoiceService.addInvoice(invoice);
        }
    }
}
