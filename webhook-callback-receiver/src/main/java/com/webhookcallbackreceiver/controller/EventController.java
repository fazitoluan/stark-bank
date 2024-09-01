package com.webhookcallbackreceiver.controller;

import com.webhookcallbackreceiver.response.ResponseDetail;
import com.webhookcallbackreceiver.service.EventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.webhookcallbackreceiver.response.ResponseDetail.responseDetail;

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
    public ResponseEntity<ResponseDetail> fromStarkBank(@RequestBody String bodyContent, @RequestHeader Map<String, String> headers) {

        log.info("Capturing new log event created by Stark Bank");
        try {

            if (headers.get("digital-signature") == null) {
                return responseDetail(HttpStatus.BAD_REQUEST, "Digital-Signature Header was not provided.");
            }
            eventService.eventOriginManager(
                    bodyContent,
                    headers.get("digital-signature"));
            return responseDetail(HttpStatus.OK, "Event received and processed successfully.");
        } catch (Exception e) {
            log.error("Error while capturing event from Stark Bank", e);
            return responseDetail(HttpStatus.BAD_REQUEST, "Invalid Stark Bank request.");
        }

    }

    @Scheduled(cron = "${cron.expression.to.sync}")
    public void synchronizeUndeliveredEventsListener() throws Exception {

        try {
            log.info("Starting synchronization of Undelivered Events");
            eventService.updateEventListToDelivered(eventService.getUndeliveredEvent());
            log.info("Finished synchronization of Undelivered Events");
        } catch (Exception e) {
            log.error("Error while synchronizing undelivered events", e);
        }

    }
}
