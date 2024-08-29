package com.invoiceissuer.dao;

import com.invoiceissuer.model.DiscountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountDao extends JpaRepository<DiscountModel, Long> {
}
