package com.webhookcallbackreceiver.service;

import com.starkbank.*;
import com.starkbank.utils.Generator;
import com.webhookcallbackreceiver.enumeration.EventBodyParamEnum;
import com.webhookcallbackreceiver.enumeration.LogStatusEnum;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Log4j2
@Service
public class EventService {

    @Value("${private.key.path}")
    protected String privateKeyPath;

    @Value("${environment.app}")
    protected String environment;

    @Value("${project.id}")
    protected String projectId;

    private final TransferService transferService;

    @Autowired
    public EventService(TransferService transferService) {
        this.transferService = transferService;
    }

    public void generateAuth() throws Exception {
        try {
            String privateKeyContent = new String(Files.readAllBytes(Paths.get(privateKeyPath)));

            Settings.user = new Project(
                    environment,
                    projectId,
                    privateKeyContent
            );
        } catch (IOException e) {
            log.error("Failed to read private key", e);
        }
    }

    @PostConstruct
    public void init() throws Exception {
        generateAuth();
        Generator<Event> undeliveredEventGenerator = getUndeliveredEvent();

        for (Event undeliveredEvent : undeliveredEventGenerator) {
            transferFromInvoiceEvent(undeliveredEvent);
        }

        updateEventListToDelivered(undeliveredEventGenerator);
    }

    public void eventOriginManager(String bodyContent, String signature) throws Exception {
        try {
            Event event = Event.parse(bodyContent, signature);
            switch (event.subscription) {
                case "invoice": {
                    transferFromInvoiceEvent(event);
                    break;
                }
                //for future implementation
            }
        } catch (Exception e) {
            log.error("Failed while parsing event content body and digital signature");
            throw e;
        }
    }

    public void transferFromInvoiceEvent(Event event) throws Exception {

        long amountAfterFees = 0, fees;
        try {
            Invoice.Log eventLog = ((Event.InvoiceEvent) event).log;

            if (eventLog != null) {
                if (LogStatusEnum.PAID.getValue().equals(eventLog.type)) {
                    log.info("Log type equals to PAID. Initializing transfer from invoice event");

                    if (eventLog.invoice != null) {
                        String invoiceId = eventLog.invoice.id;
                        fees = eventLog.invoice.discountAmount.intValue()
                                + eventLog.invoice.fineAmount.intValue()
                                + eventLog.invoice.interestAmount.intValue()
                                + 50;

                        amountAfterFees = eventLog.invoice.nominalAmount.intValue() - fees;

                        if (amountAfterFees < 0) {
                            throw new Exception("Negative amount after fee. Transfer will not be sent");
                        }

                        if (!hasEnoughBalance(amountAfterFees)) {
                            throw new Exception("Not enough balance to complete transfer. Aborting process");
                        }

                        log.info("Transfer origin from invoiceId {}. Total amount that will be sent: {}", invoiceId, amountAfterFees);
                        transferService.transferToStarkBank(invoiceId, amountAfterFees);

                        return;
                    }
                    throw new Exception("Could not find invoice object inside log object");
                }
                log.info("Log type different from PAID. Aborting transfer from invoice event");
            } else {
                throw new Exception("Could not find log object inside event object");
            }

        } catch (Exception e) {
            log.error("Error while executing method transferFromInvoiceEvent: {}", e.getMessage());
            throw e;
        }
    }

    private boolean hasEnoughBalance(Long amountAfterFees) throws Exception {
        return Balance.get().amount >= amountAfterFees;
    }

    public Generator<Event> getUndeliveredEvent() throws Exception {
        log.info("Searching for undelivered events");

        HashMap<String, Object> params = new HashMap<>();
        params.put(EventBodyParamEnum.IS_DELIVERED.getValue(), false);
        params.put(EventBodyParamEnum.BEFORE.getValue(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return Event.query(params);
    }

    public void updateEventListToDelivered(Generator<Event> undeliveredEventGenerator) throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put(EventBodyParamEnum.IS_DELIVERED.getValue(), true);

        try {
            List<Event> undeliveredEventList = new ArrayList<>();
            undeliveredEventGenerator.forEach(undeliveredEventList::add);
            if (undeliveredEventList.isEmpty()) {
                log.info("Could not find any undelivered events");
                return;
            }

            undeliveredEventList.parallelStream()
                    .forEach(undeliveredEvent -> {
                        try {
                            Event.update(undeliveredEvent.id, params);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        log.info("Event id {} set as delivered", undeliveredEvent.id);
                    });

        } catch (Exception e) {
            log.error("Error while executing method updateEventListToDelivered: {}", e.getMessage());
            throw e;
        }

    }
}
