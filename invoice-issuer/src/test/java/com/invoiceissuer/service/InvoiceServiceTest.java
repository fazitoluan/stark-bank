package com.invoiceissuer.service;

import com.invoiceissuer.dao.InvoiceDao;
import com.invoiceissuer.model.InvoiceModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private InvoiceDao invoiceDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddingInvalidInvoice() throws Exception {
        InvoiceModel invoice = new InvoiceModel();
        assertThrows(Exception.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    public void testAddingInvoiceWithoutAmount() throws Exception {
        InvoiceModel invoice = new InvoiceModel();
        invoice.setName("Igor");
        invoice.setTaxId("9389328432");
        assertThrows(Exception.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    public void testAddingInvoiceWithoutTaxId() throws Exception {
        InvoiceModel invoice = new InvoiceModel();
        invoice.setName("Igor");
        invoice.setAmount(921900L);
        assertThrows(Exception.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    public void testAddingInvoiceWithoutName() throws Exception {
        InvoiceModel invoice = new InvoiceModel();
        invoice.setTaxId("23198201398");
        invoice.setAmount(921900L);
        assertThrows(Exception.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    public void testAddingInvoiceOnlyAmount() throws Exception {
        InvoiceModel invoice = new InvoiceModel();
        invoice.setAmount(921900L);
        assertThrows(Exception.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    public void testAddingValidInvoice() throws Exception {
        InvoiceModel invoice = new InvoiceModel();
        invoice.setName("Thomas");
        invoice.setTaxId("23198201398");
        invoice.setAmount(921900L);

        invoiceService.addInvoice(invoice);
        verify(invoiceDao, times(1)).save(invoice);
    }
}
