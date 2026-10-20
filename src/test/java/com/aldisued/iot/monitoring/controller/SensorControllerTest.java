package com.aldisued.iot.monitoring.controller;

import com.aldisued.iot.monitoring.IntegrationTestBase;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SensorControllerTest extends IntegrationTestBase {

  private static final String SENSOR_JSON = """
      {"name": "Race Sensor", "type": "TEMPERATURE"}
      """;

  @Autowired
  private MockMvc mockMvc;

  @MockitoSpyBean
  private SensorRepository sensorRepository;

  @AfterEach
  public void tearDown() {
    sensorRepository.deleteAll();
  }

  @Test
  public void duplicateNamePassingExistsByNameCheckReturnsConflict() throws Exception {
    // simulate two concurrent requests by making existsByName check always pass
    // --> the second insert will be rejected by the unique constraint of the DB
    doReturn(false).when(sensorRepository).existsByName(any());

    mockMvc.perform(post("/sensors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(SENSOR_JSON))
        .andExpect(status().isOk());

    mockMvc.perform(post("/sensors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(SENSOR_JSON))
        .andExpect(status().isConflict());

    verify(sensorRepository, times(2)).existsByName("Race Sensor");
  }
}
