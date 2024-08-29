package com.invoiceissuer.dao;

import com.invoiceissuer.model.RuleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleDao extends JpaRepository<RuleModel, Long> {
}
