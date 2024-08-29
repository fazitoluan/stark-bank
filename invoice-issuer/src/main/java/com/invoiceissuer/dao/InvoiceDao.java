package com.invoiceissuer.dao;


import com.invoiceissuer.model.InvoiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceDao extends JpaRepository<InvoiceModel, Long> {

    @Query("SELECT inv FROM Invoice inv WHERE inv.issued = '0' and inv.retries < 2 ORDER BY inv.id LIMIT 1")
    Optional<InvoiceModel> getNotIssuedInvoice();

    @Query("SELECT inv FROM Invoice inv WHERE inv.id = ?1")
    Optional<InvoiceModel> findInvoiceById(Long invoiceId);
}
