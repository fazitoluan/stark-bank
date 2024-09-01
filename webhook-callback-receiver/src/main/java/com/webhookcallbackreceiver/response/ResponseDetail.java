package com.webhookcallbackreceiver.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDetail {

    private HttpStatus responseStatus;
    private String responseMessage;

    public static ResponseEntity<ResponseDetail> responseDetail(HttpStatus responseStatus, String responseMessage) {
        ResponseDetail responseDetail = new ResponseDetail(responseStatus, responseMessage);
        return ResponseEntity.status(responseStatus).body(responseDetail);
    }
}
