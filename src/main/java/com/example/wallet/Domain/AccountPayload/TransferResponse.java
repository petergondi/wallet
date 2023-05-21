package com.example.wallet.Domain.AccountPayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransferResponse {
    @JsonProperty("requestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("paymentInfo")
    private PaymentInfo paymentInfo;
}
