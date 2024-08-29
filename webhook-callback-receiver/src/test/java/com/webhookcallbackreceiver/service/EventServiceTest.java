package com.webhookcallbackreceiver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEmptyJson() throws Exception {
        String json = "{}";
        eventService.transferFromInvoiceEvent(json);
    }

    @Test
    void testJsonWithoutEvent() throws Exception {
        String logJson = "{\"log\": {}}";
        eventService.transferFromInvoiceEvent(logJson);
    }

    @Test
    void testJsonWithEventButWithoutLog() throws Exception {
        String eventJson = "{\"event\": {}}";
        eventService.transferFromInvoiceEvent(eventJson);
    }

    @Test
    void testJsonWithEventLogButWithoutInvoice() throws Exception {
        String eventJson = "{\"event\": {\"log\": {\"type\": \"PAID\"}}}";
        eventService.transferFromInvoiceEvent(eventJson);
    }

    @Test
    void testValidJson() throws Exception {
        String fullBodyJson = "{\"event\": {\"log\": {\"type\": \"PAID\", \"invoice\": {\"id\": 321938219, \"nominalAmount\": 982100, \"discountAmount\": 88700}}}}";
        eventService.transferFromInvoiceEvent(fullBodyJson);
        assertDoesNotThrow(() -> eventService.transferFromInvoiceEvent(fullBodyJson));
    }
}
