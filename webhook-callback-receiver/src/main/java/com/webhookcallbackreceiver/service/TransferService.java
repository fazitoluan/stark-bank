package com.webhookcallbackreceiver.service;

import com.starkbank.Transfer;
import com.webhookcallbackreceiver.enumeration.TransferBodyParamEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Log4j2
@Service
public class TransferService {

    public void transferToStarkBank(String invoiceId, Long amountAfterTaxes) {

        String accountNumber = "6341320293482496";
        String accountName = "Stark Bank S.A.";

        try {

            HashMap<String, Object> data = new HashMap<>();
            data.put(TransferBodyParamEnum.AMOUNT.getValue(), amountAfterTaxes);
            data.put(TransferBodyParamEnum.BANK_CODE.getValue(), "20018183");
            data.put(TransferBodyParamEnum.BRANCH_CODE.getValue(), "0001");
            data.put(TransferBodyParamEnum.ACCOUNT_NUMBER.getValue(), accountNumber);
            data.put(TransferBodyParamEnum.TAX_ID.getValue(), "20.018.183/0001-80");
            data.put(TransferBodyParamEnum.NAME.getValue(), accountName);
            data.put(TransferBodyParamEnum.EXTERNAL_ID.getValue(), "invoiceId" + invoiceId);
            data.put(TransferBodyParamEnum.TAGS.getValue(), new String[]{"case test"});

            List<Transfer.Rule> rules = new ArrayList<>();
            rules.add(new Transfer.Rule((TransferBodyParamEnum.RESENDING_LIMIT.getValue()), 3));
            data.put(TransferBodyParamEnum.RULES.getValue(), rules);
            data.put(TransferBodyParamEnum.ACCOUNT_TYPE.getValue(), "payment");

            Transfer.create(Collections.singletonList(new Transfer(data)));
            log.info("Transfer to {}, account {} created successfully", accountName, accountNumber);
        } catch (Exception e) {
            log.error("Error while sending transfer the amount {} from invoiceId {} to Stark Bank",
                    amountAfterTaxes, invoiceId, e);
        }
    }

}
