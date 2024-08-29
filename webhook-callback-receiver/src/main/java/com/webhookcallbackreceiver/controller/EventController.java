package com.webhookcallbackreceiver.controller;

import com.google.gson.JsonObject;
import com.webhookcallbackreceiver.service.EventService;
import com.webhookcallbackreceiver.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("fromStarkBank/event")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(path = "invoice")
    public String fromStarkBank(@RequestBody String body) throws Exception {

        try {
            eventService.transferFromInvoiceEvent(body);
        } catch (Exception e) {
            throw e;
        }
        return body;
    }
}
