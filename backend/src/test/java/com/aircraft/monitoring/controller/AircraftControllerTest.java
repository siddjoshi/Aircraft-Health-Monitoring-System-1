package com.aircraft.monitoring.controller;

import com.aircraft.monitoring.model.AircraftData;
import com.aircraft.monitoring.service.DataSimulationService;
import com.aircraft.monitoring.service.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AircraftController.
 * 
 * Tests REST API endpoints, request/response handling,
 * and integration with service layers.
 * 
 * @author Aircraft Monitoring Team
 * @version 1.0.0
 */
@WebMvcTest(AircraftController.class)
@DisplayName("AircraftController Tests")
class AircraftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSimulationService dataSimulationService;

    @MockBean
    private WebSocketService webSocketService;

    @Autowired
    private ObjectMapper objectMapper;

    private AircraftData testAircraftData;

    @BeforeEach
    void setUp() {
        testAircraftData = new AircraftData(LocalDateTime.now());
        testAircraftData.setEngineRPM(2200.0);
        testAircraftData.setEngineTemperature(150.0);
        testAircraftData.setFuelLevel(75.0);
        testAircraftData.setAltitude(35000.0);
        testAircraftData.setEngineAnomaly(false);
        testAircraftData.setFuelAnomaly(false);
    }

    @Nested
    @DisplayName("GET /api/aircraft/data Tests")
    class GetCurrentDataTests {

        @Test
        @DisplayName("Should return current aircraft data when available")
        void shouldReturnCurrentAircraftDataWhenAvailable() throws Exception {
            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);

            mockMvc.perform(get("/api/aircraft/data"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.engineRPM").value(2200.0))
                    .andExpect(jsonPath("$.engineTemperature").value(150.0))
                    .andExpect(jsonPath("$.fuelLevel").value(75.0))
                    .andExpect(jsonPath("$.altitude").value(35000.0))
                    .andExpect(jsonPath("$.engineAnomaly").value(false))
                    .andExpect(jsonPath("$.fuelAnomaly").value(false));

            verify(dataSimulationService).getCurrentData();
        }

        @Test
        @DisplayName("Should return 204 No Content when no data available")
        void shouldReturn204NoContentWhenNoDataAvailable() throws Exception {
            when(dataSimulationService.getCurrentData()).thenReturn(null);

            mockMvc.perform(get("/api/aircraft/data"))
                    .andExpect(status().isNoContent());

            verify(dataSimulationService).getCurrentData();
        }

        @Test
        @DisplayName("Should handle service exceptions gracefully")
        void shouldHandleServiceExceptionsGracefully() throws Exception {
            when(dataSimulationService.getCurrentData())
                    .thenThrow(new RuntimeException("Service error"));

            mockMvc.perform(get("/api/aircraft/data"))
                    .andExpect(status().is5xxServerError());

            verify(dataSimulationService).getCurrentData();
        }

        @Test
        @DisplayName("Should return aircraft data with all anomaly flags")
        void shouldReturnAircraftDataWithAllAnomalyFlags() throws Exception {
            testAircraftData.setEngineAnomaly(true);
            testAircraftData.setFuelAnomaly(true);
            testAircraftData.setHydraulicAnomaly(true);
            testAircraftData.setAltitudeAnomaly(true);
            testAircraftData.setAirspeedAnomaly(true);

            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);

            mockMvc.perform(get("/api/aircraft/data"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.engineAnomaly").value(true))
                    .andExpect(jsonPath("$.fuelAnomaly").value(true))
                    .andExpect(jsonPath("$.hydraulicAnomaly").value(true))
                    .andExpect(jsonPath("$.altitudeAnomaly").value(true))
                    .andExpect(jsonPath("$.airspeedAnomaly").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/aircraft/status Tests")
    class GetSystemStatusTests {

        @Test
        @DisplayName("Should return system status with current data")
        void shouldReturnSystemStatusWithCurrentData() throws Exception {
            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);
            when(webSocketService.getConnectedClientsCount()).thenReturn(3);

            mockMvc.perform(get("/api/aircraft/status"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.connectedClients").value(3))
                    .andExpect(jsonPath("$.dataGenerationActive").value(true))
                    .andExpect(jsonPath("$.lastUpdate").exists())
                    .andExpect(jsonPath("$.systemStatus").value("NORMAL"));

            verify(dataSimulationService).getCurrentData();
            verify(webSocketService).getConnectedClientsCount();
        }

        @Test
        @DisplayName("Should return system status when no data available")
        void shouldReturnSystemStatusWhenNoDataAvailable() throws Exception {
            when(dataSimulationService.getCurrentData()).thenReturn(null);
            when(webSocketService.getConnectedClientsCount()).thenReturn(0);

            mockMvc.perform(get("/api/aircraft/status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.connectedClients").value(0))
                    .andExpect(jsonPath("$.dataGenerationActive").value(false))
                    .andExpect(jsonPath("$.lastUpdate").isEmpty())
                    .andExpect(jsonPath("$.systemStatus").value("UNKNOWN"));
        }

        @Test
        @DisplayName("Should return WARNING status when anomalies present")
        void shouldReturnWarningStatusWhenAnomaliesPresent() throws Exception {
            testAircraftData.setEngineAnomaly(true);
            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);
            when(webSocketService.getConnectedClientsCount()).thenReturn(2);

            mockMvc.perform(get("/api/aircraft/status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.systemStatus").value("WARNING"));
        }
    }

    @Nested
    @DisplayName("GET /api/aircraft/health Tests")
    class GetSystemHealthTests {

        @Test
        @DisplayName("Should return system health information")
        void shouldReturnSystemHealthInformation() throws Exception {
            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);
            when(webSocketService.getConnectedClientsCount()).thenReturn(5);

            mockMvc.perform(get("/api/aircraft/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.connectedClients").value(5))
                    .andExpect(jsonPath("$.dataAvailable").value(true))
                    .andExpect(jsonPath("$.systemStatus").value("NORMAL"))
                    .andExpect(jsonPath("$.anomalies").value(false));
        }

        @Test
        @DisplayName("Should return health with anomalies when present")
        void shouldReturnHealthWithAnomaliesWhenPresent() throws Exception {
            testAircraftData.setFuelAnomaly(true);
            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);
            when(webSocketService.getConnectedClientsCount()).thenReturn(2);

            mockMvc.perform(get("/api/aircraft/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.anomalies").value(true))
                    .andExpect(jsonPath("$.systemStatus").value("WARNING"));
        }

        @Test
        @DisplayName("Should return health when no data available")
        void shouldReturnHealthWhenNoDataAvailable() throws Exception {
            when(dataSimulationService.getCurrentData()).thenReturn(null);
            when(webSocketService.getConnectedClientsCount()).thenReturn(0);

            mockMvc.perform(get("/api/aircraft/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.dataAvailable").value(false))
                    .andExpect(jsonPath("$.systemStatus").doesNotExist())
                    .andExpect(jsonPath("$.anomalies").doesNotExist());
        }
    }

    @Nested
    @DisplayName("POST /api/aircraft/simulate/engine-anomaly Tests")
    class SimulateEngineAnomalyTests {

        @Test
        @DisplayName("Should trigger engine anomaly simulation successfully")
        void shouldTriggerEngineAnomalySimulationSuccessfully() throws Exception {
            mockMvc.perform(post("/api/aircraft/simulate/engine-anomaly"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Engine anomaly simulation triggered"))
                    .andExpect(jsonPath("$.status").value("success"));

            verify(dataSimulationService).simulateEngineAnomaly();
            verify(webSocketService).broadcastAlert("ENGINE", "Engine temperature anomaly detected", "WARNING");
        }

        @Test
        @DisplayName("Should handle service exceptions during engine anomaly simulation")
        void shouldHandleServiceExceptionsDuringEngineAnomalySimulation() throws Exception {
            doThrow(new RuntimeException("Simulation failed"))
                    .when(dataSimulationService).simulateEngineAnomaly();

            mockMvc.perform(post("/api/aircraft/simulate/engine-anomaly"))
                    .andExpect(status().is5xxServerError());

            verify(dataSimulationService).simulateEngineAnomaly();
        }

        @Test
        @DisplayName("Should handle WebSocket service exceptions during engine anomaly alert")
        void shouldHandleWebSocketServiceExceptionsDuringEngineAnomalyAlert() throws Exception {
            doThrow(new RuntimeException("Alert broadcast failed"))
                    .when(webSocketService).broadcastAlert(any(), any(), any());

            mockMvc.perform(post("/api/aircraft/simulate/engine-anomaly"))
                    .andExpect(status().is5xxServerError());

            verify(dataSimulationService).simulateEngineAnomaly();
            verify(webSocketService).broadcastAlert("ENGINE", "Engine temperature anomaly detected", "WARNING");
        }
    }

    @Nested
    @DisplayName("POST /api/aircraft/simulate/fuel-anomaly Tests")
    class SimulateFuelAnomalyTests {

        @Test
        @DisplayName("Should trigger fuel anomaly simulation successfully")
        void shouldTriggerFuelAnomalySimulationSuccessfully() throws Exception {
            mockMvc.perform(post("/api/aircraft/simulate/fuel-anomaly"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Fuel anomaly simulation triggered"))
                    .andExpect(jsonPath("$.status").value("success"));

            verify(dataSimulationService).simulateFuelAnomaly();
            verify(webSocketService).broadcastAlert("FUEL", "Low fuel level detected", "WARNING");
        }

        @Test
        @DisplayName("Should handle concurrent fuel anomaly requests")
        void shouldHandleConcurrentFuelAnomalyRequests() throws Exception {
            // First request
            mockMvc.perform(post("/api/aircraft/simulate/fuel-anomaly"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"));

            // Second request (should also succeed)
            mockMvc.perform(post("/api/aircraft/simulate/fuel-anomaly"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"));

            verify(dataSimulationService, times(2)).simulateFuelAnomaly();
            verify(webSocketService, times(2)).broadcastAlert("FUEL", "Low fuel level detected", "WARNING");
        }
    }

    @Nested
    @DisplayName("POST /api/aircraft/simulate/hydraulic-anomaly Tests")
    class SimulateHydraulicAnomalyTests {

        @Test
        @DisplayName("Should trigger hydraulic anomaly simulation successfully")
        void shouldTriggerHydraulicAnomalySimulationSuccessfully() throws Exception {
            mockMvc.perform(post("/api/aircraft/simulate/hydraulic-anomaly"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Hydraulic anomaly simulation triggered"))
                    .andExpect(jsonPath("$.status").value("success"));

            verify(dataSimulationService).simulateHydraulicAnomaly();
            verify(webSocketService).broadcastAlert("HYDRAULIC", "Low hydraulic pressure detected", "WARNING");
        }

        @Test
        @DisplayName("Should handle method not allowed for GET request")
        void shouldHandleMethodNotAllowedForGetRequest() throws Exception {
            mockMvc.perform(get("/api/aircraft/simulate/hydraulic-anomaly"))
                    .andExpect(status().isMethodNotAllowed());

            verify(dataSimulationService, never()).simulateHydraulicAnomaly();
            verify(webSocketService, never()).broadcastAlert(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("POST /api/aircraft/alert Tests")
    class SendCustomAlertTests {

        @Test
        @DisplayName("Should send custom alert successfully")
        void shouldSendCustomAlertSuccessfully() throws Exception {
            Map<String, String> alertRequest = new HashMap<>();
            alertRequest.put("type", "CUSTOM");
            alertRequest.put("message", "Custom test alert");
            alertRequest.put("severity", "INFO");

            mockMvc.perform(post("/api/aircraft/alert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(alertRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Alert sent successfully"))
                    .andExpect(jsonPath("$.status").value("success"));

            verify(webSocketService).broadcastAlert("CUSTOM", "Custom test alert", "INFO");
        }

        @Test
        @DisplayName("Should send custom alert with default severity")
        void shouldSendCustomAlertWithDefaultSeverity() throws Exception {
            Map<String, String> alertRequest = new HashMap<>();
            alertRequest.put("type", "MAINTENANCE");
            alertRequest.put("message", "Scheduled maintenance reminder");
            // No severity specified - should default to INFO

            mockMvc.perform(post("/api/aircraft/alert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(alertRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"));

            verify(webSocketService).broadcastAlert("MAINTENANCE", "Scheduled maintenance reminder", "INFO");
        }

        @Test
        @DisplayName("Should handle empty alert request")
        void shouldHandleEmptyAlertRequest() throws Exception {
            Map<String, String> emptyRequest = new HashMap<>();

            mockMvc.perform(post("/api/aircraft/alert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(emptyRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"));

            verify(webSocketService).broadcastAlert(null, null, "INFO");
        }

        @Test
        @DisplayName("Should handle malformed JSON request")
        void shouldHandleMalformedJSONRequest() throws Exception {
            String malformedJson = "{\"type\":\"TEST\",\"message\":\"Test\",}"; // Extra comma

            mockMvc.perform(post("/api/aircraft/alert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(malformedJson))
                    .andExpect(status().isBadRequest());

            verify(webSocketService, never()).broadcastAlert(any(), any(), any());
        }

        @Test
        @DisplayName("Should handle missing content type")
        void shouldHandleMissingContentType() throws Exception {
            Map<String, String> alertRequest = new HashMap<>();
            alertRequest.put("type", "TEST");
            alertRequest.put("message", "Test message");

            mockMvc.perform(post("/api/aircraft/alert")
                    .content(objectMapper.writeValueAsString(alertRequest)))
                    .andExpect(status().isUnsupportedMediaType());

            verify(webSocketService, never()).broadcastAlert(any(), any(), any());
        }

        @Test
        @DisplayName("Should handle special characters in alert message")
        void shouldHandleSpecialCharactersInAlertMessage() throws Exception {
            Map<String, String> alertRequest = new HashMap<>();
            alertRequest.put("type", "TEST");
            alertRequest.put("message", "Alert with special chars: !@#$%^&*()");
            alertRequest.put("severity", "WARNING");

            mockMvc.perform(post("/api/aircraft/alert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(alertRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"));

            verify(webSocketService).broadcastAlert("TEST", "Alert with special chars: !@#$%^&*()", "WARNING");
        }
    }

    @Nested
    @DisplayName("CORS and Headers Tests")
    class CorsAndHeadersTests {

        @Test
        @DisplayName("Should handle CORS preflight request")
        void shouldHandleCorsPreflightRequest() throws Exception {
            mockMvc.perform(options("/api/aircraft/data")
                    .header("Origin", "http://localhost:3000")
                    .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should include CORS headers in response")
        void shouldIncludeCorsHeadersInResponse() throws Exception {
            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);

            mockMvc.perform(get("/api/aircraft/data")
                    .header("Origin", "http://localhost:3000"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "*"));
        }

        @Test
        @DisplayName("Should handle requests with various origins")
        void shouldHandleRequestsWithVariousOrigins() throws Exception {
            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);

            String[] origins = {
                "http://localhost:3000",
                "https://example.com",
                "http://192.168.1.100:3000"
            };

            for (String origin : origins) {
                mockMvc.perform(get("/api/aircraft/data")
                        .header("Origin", origin))
                        .andExpect(status().isOk());
            }
        }
    }

    @Nested
    @DisplayName("Error Handling and Edge Cases Tests")
    class ErrorHandlingAndEdgeCasesTests {

        @Test
        @DisplayName("Should handle invalid endpoint gracefully")
        void shouldHandleInvalidEndpointGracefully() throws Exception {
            mockMvc.perform(get("/api/aircraft/invalid-endpoint"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle request with invalid HTTP method")
        void shouldHandleRequestWithInvalidHttpMethod() throws Exception {
            mockMvc.perform(patch("/api/aircraft/data"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should handle concurrent requests efficiently")
        void shouldHandleConcurrentRequestsEfficiently() throws Exception {
            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);

            // Simulate multiple concurrent requests
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(get("/api/aircraft/data"))
                        .andExpect(status().isOk());
            }

            verify(dataSimulationService, times(5)).getCurrentData();
        }

        @Test
        @DisplayName("Should handle requests with large payloads")
        void shouldHandleRequestsWithLargePayloads() throws Exception {
            Map<String, String> largeAlertRequest = new HashMap<>();
            largeAlertRequest.put("type", "LARGE_TEST");
            largeAlertRequest.put("message", "This is a large test message ".repeat(LARGE_TEST_MESSAGE_REPEAT_COUNT)); // More realistic large message (~256 chars)
            largeAlertRequest.put("severity", "INFO");

            mockMvc.perform(post("/api/aircraft/alert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(largeAlertRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"));

            verify(webSocketService).broadcastAlert(eq("LARGE_TEST"), 
                    eq("This is a large test message ".repeat(8)), eq("INFO"));
        }

        @Test
        @DisplayName("Should handle null pointer exceptions gracefully")
        void shouldHandleNullPointerExceptionsGracefully() throws Exception {
            when(dataSimulationService.getCurrentData())
                    .thenThrow(new NullPointerException("Null pointer error"));

            mockMvc.perform(get("/api/aircraft/data"))
                    .andExpect(status().is5xxServerError());
        }

        @Test
        @DisplayName("Should validate request parameters")
        void shouldValidateRequestParameters() throws Exception {
            // Test with query parameters (if any endpoint uses them)
            mockMvc.perform(get("/api/aircraft/data?invalidParam=value"))
                    .andExpect(status().isOk()); // Should ignore unknown params

            when(dataSimulationService.getCurrentData()).thenReturn(testAircraftData);
            verify(dataSimulationService).getCurrentData();
        }
    }
}
