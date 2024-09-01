package com.invoiceissuer.controller;

import com.invoiceissuer.model.InvoiceModel;
import com.invoiceissuer.service.InvoiceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Scheduled(cron = "${cron.expression.invoice.issue}")
    public void issueInvoiceListener() {
        log.info("Starting invoice issue");
        try {
            invoiceService.issueInvoice();
        } catch (Exception e) {
           log.error("Error while issuing invoice", e);
        }
    }

    @PostMapping(path = "addOne")
    public void addInvoice(@RequestBody InvoiceModel invoice) {
        try {
            invoiceService.addInvoice(invoice);
        } catch (Exception e) {
            log.error("Error while adding invoice", e);
        }
    }

    @PostMapping(path = "addMany")
    public void addManyInvoices(@RequestBody List<InvoiceModel> invoiceList) {
        try {
            for (InvoiceModel invoice : invoiceList) {
                invoiceService.addInvoice(invoice);
            }
        } catch (Exception e) {
            log.error("Error while adding invoice list", e);
        }

    }
}
