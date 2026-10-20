package com.aldisued.iot.monitoring.controller;

import com.aldisued.iot.monitoring.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS, statements = {
    "INSERT INTO sensors (id, name, type) VALUES ('" + AlertControllerTest.SENSOR_WITH_ALERTS + "', 'Temperature Sensor', 'TEMPERATURE')",
    "INSERT INTO sensors (id, name, type) VALUES ('" + AlertControllerTest.SENSOR_WITHOUT_ALERTS + "', 'Humidity Sensor', 'HUMIDITY')",

    // two alerts with the same timestamp; the lower id is inserted first,
    // so without the id tie-breaker a plain table scan would return 'Alert 101'
    // (note: index scan on idx_alerts_sensor_latest happens to follow id DESC, too)
    "INSERT INTO alerts (id, message, timestamp, sensor_id) VALUES (101, 'Alert 101', '2020-06-25 23:59:59', '" + AlertControllerTest.SENSOR_WITH_ALERTS + "')",
    "INSERT INTO alerts (id, message, timestamp, sensor_id) VALUES (102, 'Alert 102', '2020-06-25 23:59:59', '" + AlertControllerTest.SENSOR_WITH_ALERTS + "')",

    "INSERT INTO alerts (id, message, timestamp, sensor_id) VALUES (103, 'Alert 103', '2020-06-24 10:00:00', '" + AlertControllerTest.SENSOR_WITH_ALERTS + "')"
})
public class AlertControllerTest extends IntegrationTestBase {

  public static final String SENSOR_WITH_ALERTS = "e3242ea2-0514-46d3-aad8-b2012980c41c";
  public static final String SENSOR_WITHOUT_ALERTS = "ac723c77-955f-469d-9d6a-d56bac39c202";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void sameTimestampReturnsAlertWithHigherId() throws Exception {
    mockMvc.perform(get("/alerts/latest").queryParam("sensorId", SENSOR_WITH_ALERTS))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Alert 102"))
        .andExpect(jsonPath("$.sensorId").value(SENSOR_WITH_ALERTS));
  }

  @Test
  public void existingSensorWithoutAlertsReturnsNotFound() throws Exception {
    mockMvc.perform(get("/alerts/latest").queryParam("sensorId", SENSOR_WITHOUT_ALERTS))
        .andExpect(status().isNotFound());
  }

  @Test
  public void invalidSensorIdReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/alerts/latest").queryParam("sensorId", "not-a-uuid"))
        .andExpect(status().isBadRequest());
  }
}
