package com.example.userservice.client;

import com.example.userservice.vo.response.ResponseParcel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//@FeignClient(name = "delivery-service")
//public interface DeliveryServiceClient {
//
//    @GetMapping("/delivery-service/{userId}/items")
//    List<ResponseParcel> getItems(@PathVariable String userId);
//}
