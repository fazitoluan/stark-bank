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

    public void transferToStarkBank(Long invoiceId, Long amountAfterTaxes) {

        try {
            List<Transfer> transfers = new ArrayList<>();
            HashMap<String, Object> data = new HashMap<>();
            data.put(TransferBodyParamEnum.AMOUNT.getValue(), amountAfterTaxes);
            data.put(TransferBodyParamEnum.BANK_CODE.getValue(), "20018183");
            data.put(TransferBodyParamEnum.BRANCH_CODE.getValue(), "0001");
            data.put(TransferBodyParamEnum.ACCOUNT_NUMBER.getValue(), "6341320293482496");
            data.put(TransferBodyParamEnum.TAX_ID.getValue(), "20.018.183/0001-80");
            data.put(TransferBodyParamEnum.NAME.getValue(), "Stark Bank S.A.");
            //data.put("externalId", "my-external-id");
            data.put(TransferBodyParamEnum.TAGS.getValue(), new String[]{"case test"});

            List<Transfer.Rule> rules = new ArrayList<>();
            rules.add(new Transfer.Rule((TransferBodyParamEnum.RESENDING_LIMIT.getValue()), 3));
            data.put(TransferBodyParamEnum.RULES.getValue(), rules);
            data.put(TransferBodyParamEnum.ACCOUNT_TYPE.getValue(), "payment");

            Transfer.create(Collections.singletonList(new Transfer(data)));

            transfers.add(new Transfer(data));
            for (Transfer transfer : transfers){
                System.out.println(transfer);
            }
        } catch (Exception e) {
            log.error("Error while sending transfer the amount {} from invoiceId {} to Stark Bank",
                    amountAfterTaxes, invoiceId, e);
        }
    }

}
