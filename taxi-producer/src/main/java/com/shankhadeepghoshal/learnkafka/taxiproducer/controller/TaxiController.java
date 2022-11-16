package com.shankhadeepghoshal.learnkafka.taxiproducer.controller;

import com.shankhadeepghoshal.learnkafka.taxiproducer.pojos.Taxi;
import com.shankhadeepghoshal.learnkafka.taxiproducer.service.TaxiProducerService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/taxi")
@Slf4j
@RequiredArgsConstructor
public class TaxiController {

  TaxiProducerService service;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> postTaxiStatus(@Valid @RequestBody Taxi taxi) {
    try {
      service.sendTaxiToBroker(taxi);
      return ResponseEntity.accepted().body("Request accepted for processing");
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Exception in producer", e);
      return ResponseEntity.internalServerError()
          .body("Something went wrong. Please try again later");
    }
  }
}
