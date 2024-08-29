package com.webhookcallbackreceiver.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.starkbank.Project;
import com.starkbank.Settings;
import com.webhookcallbackreceiver.enumeration.LogStatusEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Log4j2
@Service
public class EventService {

    private final TransferService transferService;

    @Autowired
    public EventService(TransferService transferService) {
        this.transferService = transferService;
    }

    public static void generateAuth() throws Exception {

        try {
            String filePath = "/var/opt/resources/privateKey.pem";

            String privateKeyContent = new String(Files.readAllBytes(Paths.get(filePath)));

            Settings.user = new Project(
                    "sandbox",
                    "5357212679536640",
                    privateKeyContent
            );
        } catch (IOException e) {
            log.error("Failed to read private key", e);
        }
    }

    public void transferFromInvoiceEvent(String body) throws Exception {

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

                    JsonObject invoiceObject = getJsonObjectByField("invoice", logObject);
                    if (invoiceObject != null) {
                        Long invoiceId = Objects.requireNonNull(getElement("id", invoiceObject)).getAsLong();
                        Long amountAfterFees = Objects.requireNonNull(getElement("nominalAmount", invoiceObject)).getAsLong();

                        generateAuth();
                        transferService.transferToStarkBank(invoiceId, amountAfterFees);
                        return;
                    }
                    throw new Exception("The request body does not contain expected object called invoice");
                }
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
}
