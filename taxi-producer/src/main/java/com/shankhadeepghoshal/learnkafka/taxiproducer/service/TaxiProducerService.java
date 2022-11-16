package com.shankhadeepghoshal.learnkafka.taxiproducer.service;

import com.shankhadeepghoshal.learnkafka.taxiproducer.pojos.Taxi;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface TaxiProducerService {
  void sendTaxiToBroker(Taxi taxi)
      throws ExecutionException, InterruptedException, TimeoutException;
}
