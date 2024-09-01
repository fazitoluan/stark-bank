package com.webhookcallbackreceiver.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class EventServiceTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitialization() {
        TransferService transferService = mock(TransferService.class);
        EventService eventService = new EventService(transferService);
        Assert.assertNotNull(eventService);
    }

    @Test
    public void testEventOriginManagerThrowsException() {
        String bodyContent = "{test:2329}";
        String signature = "Mskdjak23213099Eq=231ewq/weoidsw2";

        assertThrows(Exception.class, () -> {
            eventService.eventOriginManager(bodyContent, signature);
        });
    }

}