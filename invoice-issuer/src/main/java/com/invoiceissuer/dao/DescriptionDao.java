package com.invoiceissuer.dao;

import com.invoiceissuer.model.DescriptionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescriptionDao extends JpaRepository<DescriptionModel, Long> {
}
