package com.webhookcallbackreceiver.controller;

import com.webhookcallbackreceiver.service.EventService;
import com.webhookcallbackreceiver.controller.EventController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

public class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Test
    public void testEventController() {
        MockitoAnnotations.openMocks(this);
    }
}