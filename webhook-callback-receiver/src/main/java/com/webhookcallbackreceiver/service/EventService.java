package com.webhookcallbackreceiver.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.starkbank.Event;
import com.starkbank.Project;
import com.starkbank.Settings;
import com.starkbank.utils.Generator;
import com.webhookcallbackreceiver.enumeration.EventBodyParamEnum;
import com.webhookcallbackreceiver.enumeration.LogStatusEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public void transferFromInvoiceEvent(String body) {

        try {
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            if (jsonObject.isEmpty()) {
                throw new Exception("The request body is empty");
            }

            JsonObject eventObject = getJsonObjectByField("event", jsonObject);
            if (eventObject == null || eventObject.isEmpty()) {
                throw new Exception("Could not find event object or is empty");
            }

            JsonObject logObject = getJsonObjectByField("log", eventObject);
            if (logObject != null) {
                String logType = Objects.requireNonNull(getElement("type", logObject)).getAsString();
                if (LogStatusEnum.PAID.getValue().equals(logType)) {
                    log.info("Log Type PAID. Initializing transfer from invoice event");

                    JsonObject invoiceObject = getJsonObjectByField("invoice", logObject);
                    if (invoiceObject != null) {
                        Long invoiceId = Objects.requireNonNull(getElement("id", invoiceObject)).getAsLong();
                        Long amountAfterFees = Objects.requireNonNull(getElement("nominalAmount", invoiceObject)).getAsLong();
                        if (invoiceObject.has("discountAmount")) {
                            amountAfterFees -= Objects.requireNonNull(getElement("discountAmount", invoiceObject)).getAsLong();
                        }

                        generateAuth();

                        log.info("Transfer origin from invoiceId {}. Total amount that will be sent: {}", invoiceId, amountAfterFees);
                        transferService.transferToStarkBank(invoiceId, amountAfterFees);

                        return;
                    }
                    throw new Exception("The request body does not contain expected object called invoice");
                }
                log.info("Log Type is not PAID. Aborting transfer from invoice event");
            } else {
                throw new Exception("The request body does not contain expected object called log");
            }

        } catch (JsonSyntaxException e) {
            log.error("Syntax JSON error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error while executing method transferFromInvoiceEvent: {}", e.getMessage());
        }
    }

    public static boolean hasJsonField(String fieldName, JsonObject jsonObject) {
        return jsonObject != null
                && !jsonObject.isEmpty()
                && jsonObject.has(fieldName)
                && !jsonObject.get(fieldName).isJsonNull();
    }

    public static JsonObject getJsonObjectByField(String fieldName, JsonObject jsonObject) {
        if (hasJsonField(fieldName, jsonObject) && jsonObject.get(fieldName).isJsonObject()) {
            return jsonObject.getAsJsonObject(fieldName);
        }
        return null;
    }

    public static JsonElement getElement(String fieldName, JsonObject jsonObject) {
        if (hasJsonField(fieldName, jsonObject)) {
            return jsonObject.get(fieldName);
        }
        return null;
    }

    private Generator<Event> getUndeliveredEvent() throws Exception {
        log.info("Searching for undelivered events");

        HashMap<String, Object> params = new HashMap<>();
        params.put(EventBodyParamEnum.IS_DELIVERED.getValue(), false);
        params.put(EventBodyParamEnum.BEFORE.getValue(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return Event.query(params);
    }

    public void updateEventToDelivered() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put(EventBodyParamEnum.IS_DELIVERED.getValue(), true);

        try {
            generateAuth();
            Generator<Event> undeliveredEventGenerator = getUndeliveredEvent();

            List<Event> undeliveredEventList = new ArrayList<>();
            undeliveredEventGenerator.forEach(undeliveredEventList::add);
            if (undeliveredEventList.isEmpty()) {
                log.info("Could not find any undelivered events");
                return;
            }

            for (Event undeliveredEvent : undeliveredEventList) {
                Event.update(undeliveredEvent.id, params);
                log.info("Event id {} set as delivered", undeliveredEvent.id);
            }
        } catch (Exception e) {
            log.error("Error while executing method updateEventToDelivered: {}", e.getMessage());
            throw e;
        }

    }
}
