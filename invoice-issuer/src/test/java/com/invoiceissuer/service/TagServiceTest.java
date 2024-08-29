package com.invoiceissuer.service;

import com.invoiceissuer.dao.TagDao;
import com.invoiceissuer.model.TagModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private TagDao tagDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddingTag() {
        TagModel tag = new TagModel();
        tagService.addTag(tag);
        verify(tagDao, times(1)).save(tag);
    }
}
