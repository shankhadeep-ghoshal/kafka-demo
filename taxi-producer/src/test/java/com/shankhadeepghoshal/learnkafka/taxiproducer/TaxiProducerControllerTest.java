package com.shankhadeepghoshal.learnkafka.taxiproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shankhadeepghoshal.learnkafka.taxiproducer.pojos.Taxi;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaxiProducerControllerTest {
  @NonFinal @Autowired protected MockMvc mockMvc;

  @Test
  void testValidMessagePosting() throws Exception {
    final var taxi = new Taxi(2L, 1.5, 2.6);
    final var result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/taxi")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsBytes(taxi)))
            .andExpect(MockMvcResultMatchers.status().isAccepted())
            .andReturn();

    Assertions.assertEquals(HttpStatus.ACCEPTED.value(), result.getResponse().getStatus());
  }

  @Test
  void testInvalidPayloadMessageResponse() throws Exception {
    final var taxi = new Taxi(null, 1.5, 2.6);
    final var result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/taxi")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsBytes(taxi)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();

    Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
  }
}
