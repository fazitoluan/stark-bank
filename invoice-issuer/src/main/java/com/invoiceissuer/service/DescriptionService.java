package com.invoiceissuer.service;

import com.invoiceissuer.dao.DescriptionDao;
import com.invoiceissuer.model.DescriptionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DescriptionService {

    private final DescriptionDao descriptionDao;

    @Autowired
    public DescriptionService(DescriptionDao descriptionDao) {
        this.descriptionDao = descriptionDao;
    }

    public void addDescription(DescriptionModel description) {
        descriptionDao.save(description);
    }
}
