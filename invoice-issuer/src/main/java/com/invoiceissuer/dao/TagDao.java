package com.invoiceissuer.dao;

import com.invoiceissuer.model.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagDao extends JpaRepository<TagModel, Long> {
}
