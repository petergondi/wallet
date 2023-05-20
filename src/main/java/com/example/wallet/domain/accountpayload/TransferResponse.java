package com.example.wallet.domain.accountpayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransferResponse {
    @JsonProperty("requestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("paymentInfo")
    private PaymentInfo paymentInfo;
}
