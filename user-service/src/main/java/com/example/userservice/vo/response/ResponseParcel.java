package com.example.userservice.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResponseParcel {

    @Schema(description = "운송장 번호", nullable = false, example = "012584042")
    private String invoiceNo;

    @Schema(description = "배송 제품 이름", nullable = false, example = "강아지 간식")
    private String productName;

    @Schema(description = "수령인 이름", nullable = false, example = "신현식")
    private String receiverName;

    @Schema(description = "배송 완료 여부", nullable = false, example = "false")
    private String state;

    @Schema(description = "수령인 주소", nullable = false, example = "서울 중구 장충로 와르르맨션 205호")
    private String address;

    @Schema(description = "배송 물품 할당 시간", nullable = false, example = "")
    private String createdDate;

    @Schema(description = "배송 기사 이름", nullable = false, example = "신현식")
    private String deliveryName;

    @Schema(description = "택배 회사", nullable = false, example = "CJ대한통운")
    private String company;
}
