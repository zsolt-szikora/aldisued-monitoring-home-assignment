package com.aldisued.iot.monitoring.controller;

import com.aldisued.iot.monitoring.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS, statements = {
    "INSERT INTO sensors (id, name, type) VALUES ('e3242ea2-0514-46d3-aad8-b2012980c41c', 'Temperature Sensor', 'TEMPERATURE')",
    // an existing alert, so the response would also hit the sensor -> alerts -> sensor loop
    "INSERT INTO alerts (id, message, timestamp, sensor_id) VALUES (1, 'Temperature alert', '2020-06-24 18:23:12', 'e3242ea2-0514-46d3-aad8-b2012980c41c')"
})
public class SensorReadingControllerTest extends IntegrationTestBase {

  private static final String SENSOR_ID = "e3242ea2-0514-46d3-aad8-b2012980c41c";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void postReturnsReadingWithSensorIdOnly() throws Exception {
    mockMvc.perform(post("/sensor-readings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(readingJson(SENSOR_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.value").value(23.45))
        .andExpect(jsonPath("$.sensorId").value(SENSOR_ID))
        .andExpect(jsonPath("$.sensor").doesNotExist());
  }

  @Test
  public void postForUnknownSensorReturnsNotFound() throws Exception {
    mockMvc.perform(post("/sensor-readings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(readingJson("00000000-0000-0000-0000-000000000000")))
        .andExpect(status().isNotFound());
  }

  private static String readingJson(String sensorId) {
    return """
        {"sensorId": "%s", "value": 23.45, "timestamp": "2026-01-01T10:00:00"}
        """.formatted(sensorId);
  }
}
