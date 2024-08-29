package com.invoiceissuer.service;

import com.starkbank.Invoice;
import com.starkbank.Project;
import com.starkbank.Settings;
import com.invoiceissuer.dao.InvoiceDao;
import com.invoiceissuer.enumeration.StatusIssueEnum;
import com.invoiceissuer.model.InvoiceModel;
import com.invoiceissuer.model.TagModel;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
@Log4j2
public class InvoiceService {

    private final InvoiceDao invoiceDao;

    @Value("${limit.issued.per.iter}")
    private Integer limitIssuedPerIter;

    @Autowired
    public InvoiceService(InvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    public void addInvoice(InvoiceModel invoice) {
        invoice.setRetries(0);
        invoiceDao.save(invoice);
    }

    public static void generateAuth() throws Exception {

        try {
            String filePath = "/var/opt/resources/privateKey.pem";

            String privateKeyContent = new String(Files.readAllBytes(Paths.get(filePath)));

            Settings.user = new Project(
                    "sandbox",
                    "5357212679536640",
                    privateKeyContent
            );
        } catch (IOException e) {
            log.error("Failed to read private key", e);
        }
    }

    @Transactional
    public void issueInvoice() throws Exception {
        try {
            generateAuth();

            int count = 0;
            Integer issuingInvoiceReturn;
            while (count < limitIssuedPerIter) {
                issuingInvoiceReturn = issuingInvoice();

                count = !issuingInvoiceReturn.equals(StatusIssueEnum.NO_PENDING.getValue())
                            ? count + issuingInvoiceReturn : limitIssuedPerIter;
            }

        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public Integer issuingInvoice() throws Exception {

        Optional<InvoiceModel> invoice = Optional.empty();
        try {
            invoice = invoiceDao.getNotIssuedInvoice();
            if (invoice.isPresent()) {
                Invoice.create(Collections.singletonList(new Invoice(buildInvoiceData(invoice))));
                updateInvoiceToIssued(invoice.get().getId());
                log.info("Invoice id {} issued", invoice.get().getId());
                return StatusIssueEnum.SUCCESSFUL.getValue();
            }
            log.info("No pending invoice issuing found, wait for next schedule");
            return StatusIssueEnum.NO_PENDING.getValue();
        } catch (Exception e) {
            if (invoice.isPresent()) {
                log.error("Error issuing invoice id {}", invoice.get().getId(), e);
                incrementInvoiceIssueRetry(invoice.get().getId());
            }
        }
        return StatusIssueEnum.ERROR.getValue();
    }

    @Transactional
    public void updateInvoiceToIssued(Long invoiceId) throws Exception {
        InvoiceModel invoice = invoiceDao.findInvoiceById(invoiceId)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + invoiceId));
        invoice.setIssued("1");
    }

    @Transactional
    public void incrementInvoiceIssueRetry(Long invoiceId) throws Exception {
        InvoiceModel invoice = invoiceDao.findInvoiceById(invoiceId)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + invoiceId));
        invoice.setRetries(invoice.getRetries()+1);
    }

    public HashMap<String, Object> buildInvoiceData(Optional<InvoiceModel> invoiceModel) throws Exception {

        HashMap<String, Object> data = new HashMap<>();
        List<HashMap<String, Object>> descriptions = new ArrayList<>();
        if (invoiceModel.isPresent() && invoiceModel.get().getDescriptions() != null && !invoiceModel.get().getDescriptions().isEmpty()) {
            data.put("key", invoiceModel.get().getDescriptions().get(0).getKey());
            data.put("value", invoiceModel.get().getDescriptions().get(0).getValue());
            descriptions.add(new HashMap<>(data));
            data.clear();
        }

        List<HashMap<String, Object>> rules = new ArrayList<>();
        if (invoiceModel.isPresent() && invoiceModel.get().getRules() != null && !invoiceModel.get().getRules().isEmpty()) {
            data.put("key", invoiceModel.get().getRules().get(0).getKey());
            data.put("value", new String[]{invoiceModel.get().getRules().get(0).getValue()});
            rules.add(new HashMap<>(data));
            data.clear();
        }

        List<HashMap<String, Object>> discounts = new ArrayList<>();
        if (invoiceModel.isPresent() && invoiceModel.get().getDiscounts() != null && !invoiceModel.get().getDiscounts().isEmpty()) {
            data.put("percentage", invoiceModel.get().getDiscounts().get(0).getPercentage());
            data.put("due", invoiceModel.get().getDiscounts().get(0).getDue());
            discounts.add(new HashMap<>(data));
            data.clear();
        }

        data.put("amount", invoiceModel.get().getAmount() != null ? invoiceModel.get().getAmount() : null);
        data.put("due", invoiceModel.get().getDue() != null ? invoiceModel.get().getDue() : null);
        data.put("taxId", invoiceModel.get().getTaxId() != null ? invoiceModel.get().getTaxId() : null);
        data.put("name", invoiceModel.get().getName() != null ? invoiceModel.get().getName() : null);
        data.put("expiration", invoiceModel.get().getExpiration() != null ? invoiceModel.get().getExpiration() : null);
        data.put("discounts", invoiceModel.get().getDiscounts() != null ? discounts : null);
        data.put("descriptions", invoiceModel.get().getDescriptions() != null ? descriptions : null);
        data.put("rules", invoiceModel.get().getRules() != null ? rules : null);
        data.put("fine", invoiceModel.get().getFine() != null ? invoiceModel.get().getFine() : null);
        data.put("interest", invoiceModel.get().getInterest() != null ? invoiceModel.get().getInterest() : null);
        data.put("tags", invoiceModel.get().getTags() != null
                ? new String[] {
                        Arrays.toString(invoiceModel.get().getTags().stream().map(TagModel::getValue) .toArray(String[]::new))
                    }
                : null);

        return data;
    }
}
