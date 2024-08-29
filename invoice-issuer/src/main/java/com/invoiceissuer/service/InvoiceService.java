package com.invoiceissuer.service;

import com.invoiceissuer.dao.InvoiceDao;
import com.invoiceissuer.enumeration.InvoiceBodyParamEnum;
import com.invoiceissuer.enumeration.StatusIssueEnum;
import com.invoiceissuer.model.InvoiceModel;
import com.invoiceissuer.model.TagModel;
import com.starkbank.Invoice;
import com.starkbank.Project;
import com.starkbank.Settings;
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

    @Value("${limit.issues.per.iteration}")
    private Integer limitIssuedPerIter;

    @Value("${private.key.path}")
    private String privateKeyPath;

    @Value("${environment.app}")
    private String environment;

    @Value("${project.id}")
    private String projectId;

    @Autowired
    public InvoiceService(InvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    public void generateAuth() throws Exception {
        log.info("Invoice generation started for environment {} and project {}", environment, projectId);
        try {
            String privateKeyContent = new String(Files.readAllBytes(Paths.get(privateKeyPath)));

            Settings.user = new Project(
                    environment,
                    projectId,
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
            log.info("Limit of invoice issues per iteration: {}", limitIssuedPerIter);
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
    public Integer issuingInvoice() {

        Optional<InvoiceModel> invoice = Optional.empty();
        try {
            invoice = invoiceDao.getNotIssuedInvoice();
            if (invoice.isPresent()) {
                log.info("Issuing Invoice id {}", invoice.get().getId());
                Invoice.create(Collections.singletonList(new Invoice(buildInvoiceData(invoice))));
                updateInvoiceToIssued(invoice.get().getId());
                log.info("Invoice id {} successfully issued", invoice.get().getId());
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
    public void updateInvoiceToIssued(Long invoiceId) {
        InvoiceModel invoice = invoiceDao.findInvoiceById(invoiceId)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + invoiceId));
        invoice.setIssued("1");
        log.info("Invoice id {} issued field updated to 1", invoiceId);
    }

    @Transactional
    public void incrementInvoiceIssueRetry(Long invoiceId) {
        InvoiceModel invoice = invoiceDao.findInvoiceById(invoiceId)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + invoiceId));
        invoice.setRetries(invoice.getRetries() + 1);
        log.info("Invoice id {} will retry issuing. Number of actual retries: {}", invoice.getId(), invoice.getRetries());
    }

    public HashMap<String, Object> buildInvoiceData(Optional<InvoiceModel> invoiceModel) {
        HashMap<String, Object> data = new HashMap<>();

        List<HashMap<String, Object>> descriptions = new ArrayList<>();
        if (invoiceModel.isPresent() && invoiceModel.get().getDescriptions() != null && !invoiceModel.get().getDescriptions().isEmpty()) {
            for (int i = 0; i < invoiceModel.get().getDescriptions().size(); i++) {
                data.put(InvoiceBodyParamEnum.KEY.getValue(), invoiceModel.get().getDescriptions().get(i).getKey());
                data.put(InvoiceBodyParamEnum.VALUE.getValue(), invoiceModel.get().getDescriptions().get(i).getValue());
                descriptions.add(new HashMap<>(data));
                data.clear();
            }
        }

        List<HashMap<String, Object>> rules = new ArrayList<>();
        if (invoiceModel.isPresent() && invoiceModel.get().getRules() != null && !invoiceModel.get().getRules().isEmpty()) {
            for (int i = 0; i < invoiceModel.get().getRules().size(); i++) {
                data.put(InvoiceBodyParamEnum.KEY.getValue(), invoiceModel.get().getRules().get(i).getKey());
                data.put(InvoiceBodyParamEnum.VALUE.getValue(), new String[] {invoiceModel.get().getRules().get(i).getValue()});
                rules.add(new HashMap<>(data));
                data.clear();
            }
        }

        List<HashMap<String, Object>> discounts = new ArrayList<>();
        if (invoiceModel.isPresent() && invoiceModel.get().getDiscounts() != null && !invoiceModel.get().getDiscounts().isEmpty()) {
            for (int i = 0; i < invoiceModel.get().getDiscounts().size(); i++) {
                data.put(InvoiceBodyParamEnum.PERCENTAGE.getValue(), invoiceModel.get().getDiscounts().get(i).getPercentage());
                data.put(InvoiceBodyParamEnum.DUE.getValue(), invoiceModel.get().getDiscounts().get(i).getDue());
                discounts.add(new HashMap<>(data));
                data.clear();
            }
        }

        data.put(InvoiceBodyParamEnum.AMOUNT.getValue(), invoiceModel.get().getAmount() != null ? invoiceModel.get().getAmount() : null);
        data.put(InvoiceBodyParamEnum.DUE.getValue(), invoiceModel.get().getDue() != null ? invoiceModel.get().getDue() : null);
        data.put(InvoiceBodyParamEnum.TAX_ID.getValue(), invoiceModel.get().getTaxId() != null ? invoiceModel.get().getTaxId() : null);
        data.put(InvoiceBodyParamEnum.NAME.getValue(), invoiceModel.get().getName() != null ? invoiceModel.get().getName() : null);
        data.put(InvoiceBodyParamEnum.EXPIRATION.getValue(), invoiceModel.get().getExpiration() != null ? invoiceModel.get().getExpiration() : null);
        data.put(InvoiceBodyParamEnum.DISCOUNTS.getValue(), invoiceModel.get().getDiscounts() != null ? discounts : null);
        data.put(InvoiceBodyParamEnum.DESCRIPTIONS.getValue(), invoiceModel.get().getDescriptions() != null ? descriptions : null);
        data.put(InvoiceBodyParamEnum.RULES.getValue(), invoiceModel.get().getRules() != null ? rules : null);
        data.put(InvoiceBodyParamEnum.FINE.getValue(), invoiceModel.get().getFine() != null ? invoiceModel.get().getFine() : null);
        data.put(InvoiceBodyParamEnum.INTEREST.getValue(), invoiceModel.get().getInterest() != null ? invoiceModel.get().getInterest() : null);
        data.put(InvoiceBodyParamEnum.TAGS.getValue(), invoiceModel.get().getTags() != null
                ? new String[] {
                        Arrays.toString(invoiceModel.get().getTags().stream().map(TagModel::getValue) .toArray(String[]::new))
                    }
                : null);

        return data;
    }

    public void addInvoice(InvoiceModel invoice) throws Exception {
        if (invoice.getAmount() != null && invoice.getName() != null && invoice.getTaxId() != null) {
            invoice.setRetries(0);
            invoice.setIssued("0");
            invoiceDao.save(invoice);
            return;
        }
        throw new Exception("Cannot save invoice without mandatory fields (amount, name and taxId)");
    }
}
