package com.invoiceissuer.service;

import com.invoiceissuer.dao.DescriptionDao;
import com.invoiceissuer.model.DescriptionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

class DescriptionServiceTest {

    @InjectMocks
    private DescriptionService descriptionService;

    @Mock
    private DescriptionDao descriptionDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddingDescription() {
        DescriptionModel description = new DescriptionModel();
        descriptionService.addDescription(description);
        verify(descriptionDao, times(1)).save(description);
    }
}
