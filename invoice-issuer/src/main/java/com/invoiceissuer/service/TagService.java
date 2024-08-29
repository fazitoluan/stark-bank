package com.invoiceissuer.service;

import com.invoiceissuer.dao.TagDao;
import com.invoiceissuer.model.TagModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    private final TagDao tagDao;

    @Autowired
    public TagService(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    public void addTag(TagModel tag) {
        tagDao.save(tag);
    }
}
