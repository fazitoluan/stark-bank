package com.webhookcallbackreceiver.controller;

import com.webhookcallbackreceiver.service.EventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("event")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(path = "fromStarkBank")
    public void fromStarkBank(@RequestBody String body) throws Exception {

        log.info("Capturing event from Stark Bank");
        try {
            eventService.transferFromInvoiceEvent(body);
        } catch (Exception e) {
            log.error("Error while capturing event from Stark Bank", e);
        }
    }

    @Scheduled(cron = "${cron.expression.to.sync}")
    public void synchronizeUndeliveredEvents() throws Exception {

        try {
            log.info("Starting synchronization of Undelivered Events");
            eventService.updateEventToDelivered();
            log.info("Finished synchronization of Undelivered Events");
        } catch (Exception e) {
            log.error("Error while synchronizing undelivered events", e);
        }

    }
}
