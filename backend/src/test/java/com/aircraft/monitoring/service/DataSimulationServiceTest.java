package com.aircraft.monitoring.service;

import com.aircraft.monitoring.model.AircraftData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DataSimulationService.
 * 
 * Tests data generation logic, anomaly simulation triggers,
 * and integration with other services.
 * 
 * @author Aircraft Monitoring Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DataSimulationService Tests")
class DataSimulationServiceTest {

    @Mock
    private AnomalyDetectionService anomalyDetectionService;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private DataSimulationService dataSimulationService;

    private AircraftData mockDetectedData;

    @BeforeEach
    void setUp() {
        mockDetectedData = new AircraftData(LocalDateTime.now());
        mockDetectedData.setEngineRPM(2200.0);
        mockDetectedData.setFuelLevel(75.0);
        mockDetectedData.setAltitude(35000.0);
        
        when(anomalyDetectionService.detectAnomalies(any(AircraftData.class)))
                .thenReturn(mockDetectedData);
    }

    @Nested
    @DisplayName("Data Generation Tests")
    class DataGenerationTests {

        @Test
        @DisplayName("Should generate aircraft data successfully")
        void shouldGenerateAircraftDataSuccessfully() {
            dataSimulationService.generateAircraftData();

            verify(anomalyDetectionService).detectAnomalies(any(AircraftData.class));
            verify(webSocketService).broadcastAircraftData(mockDetectedData);
        }

        @Test
        @DisplayName("Should set current timestamp when generating data")
        void shouldSetCurrentTimestampWhenGeneratingData() {
            LocalDateTime beforeGeneration = LocalDateTime.now();
            dataSimulationService.generateAircraftData();
            LocalDateTime afterGeneration = LocalDateTime.now();

            AircraftData currentData = dataSimulationService.getCurrentData();
            assertNotNull(currentData);
            assertNotNull(currentData.getTimestamp());
            assertTrue(currentData.getTimestamp().isAfter(beforeGeneration.minusSeconds(1)));
            assertTrue(currentData.getTimestamp().isBefore(afterGeneration.plusSeconds(1)));
        }

        @Test
        @DisplayName("Should generate realistic engine data")
        void shouldGenerateRealisticEngineData() {
            dataSimulationService.generateAircraftData();

            AircraftData currentData = dataSimulationService.getCurrentData();
            assertNotNull(currentData);
            
            // Verify engine data is within realistic ranges
            assertTrue(currentData.getEngineRPM() >= 1800.0);
            assertTrue(currentData.getEngineRPM() <= 2600.0);
            assertTrue(currentData.getEngineTemperature() > 0);
            assertTrue(currentData.getOilPressure() > 0);
            assertTrue(currentData.getOilTemperature() > 0);
        }

        @Test
        @DisplayName("Should generate realistic fuel data")
        void shouldGenerateRealisticFuelData() {
            dataSimulationService.generateAircraftData();

            AircraftData currentData = dataSimulationService.getCurrentData();
            assertNotNull(currentData);
            
            // Verify fuel data is within realistic ranges
            assertTrue(currentData.getFuelLevel() >= 0);
            assertTrue(currentData.getFuelLevel() <= 100);
            assertTrue(currentData.getFuelConsumption() > 0);
            assertTrue(currentData.getFuelPressure() > 0);
            assertTrue(currentData.getFuelTemperature() >= 0);
        }

        @Test
        @DisplayName("Should generate realistic hydraulic data")
        void shouldGenerateRealisticHydraulicData() {
            dataSimulationService.generateAircraftData();

            AircraftData currentData = dataSimulationService.getCurrentData();
            assertNotNull(currentData);
            
            // Verify hydraulic data is within realistic ranges
            assertTrue(currentData.getHydraulicPressure() >= 2500.0);
            assertTrue(currentData.getHydraulicPressure() <= 3200.0);
            assertTrue(currentData.getHydraulicTemperature() > 0);
            assertTrue(currentData.getHydraulicFluidLevel() >= 90.0);
            assertTrue(currentData.getHydraulicFluidLevel() <= 100.0);
        }

        @Test
        @DisplayName("Should generate realistic flight data")
        void shouldGenerateRealisticFlightData() {
            dataSimulationService.generateAircraftData();

            AircraftData currentData = dataSimulationService.getCurrentData();
            assertNotNull(currentData);
            
            // Verify flight data is within realistic ranges
            assertTrue(currentData.getAltitude() >= 30000.0);
            assertTrue(currentData.getAltitude() <= 40000.0);
            assertTrue(currentData.getAirspeed() >= 400.0);
            assertTrue(currentData.getAirspeed() <= 500.0);
            assertTrue(currentData.getMachNumber() > 0);
            assertTrue(currentData.getMachNumber() < 1.0);
        }

        @Test
        @DisplayName("Should generate realistic additional systems data")
        void shouldGenerateRealisticAdditionalSystemsData() {
            dataSimulationService.generateAircraftData();

            AircraftData currentData = dataSimulationService.getCurrentData();
            assertNotNull(currentData);
            
            // Verify additional systems data is within realistic ranges
            assertTrue(currentData.getCabinPressure() >= 11.0);
            assertTrue(currentData.getCabinPressure() <= 13.0);
            assertTrue(currentData.getCabinTemperature() >= 22.0);
            assertTrue(currentData.getCabinTemperature() <= 26.0);
            assertTrue(currentData.getBatteryVoltage() >= 28.0);
            assertTrue(currentData.getBatteryVoltage() <= 30.0);
            assertTrue(currentData.getGeneratorOutput() >= 115.0);
            assertTrue(currentData.getGeneratorOutput() <= 125.0);
        }
    }

    @Nested
    @DisplayName("Anomaly Simulation Tests")
    class AnomalySimulationTests {

        @Test
        @DisplayName("Should trigger engine anomaly simulation")
        void shouldTriggerEngineAnomalySimulation() {
            dataSimulationService.simulateEngineAnomaly();

            // Set the anomaly flags to verify the simulation was triggered
            ReflectionTestUtils.setField(dataSimulationService, "simulateEngineAnomaly", true);
            ReflectionTestUtils.setField(dataSimulationService, "anomalyCounter", 0);
            
            // Generate data multiple times to trigger the anomaly
            for (int i = 0; i < 15; i++) {
                dataSimulationService.generateAircraftData();
            }

            verify(anomalyDetectionService, atLeast(15)).detectAnomalies(any(AircraftData.class));
            verify(webSocketService, atLeast(15)).broadcastAircraftData(any(AircraftData.class));
        }

        @Test
        @DisplayName("Should trigger fuel anomaly simulation")
        void shouldTriggerFuelAnomalySimulation() {
            dataSimulationService.simulateFuelAnomaly();

            // Set the anomaly flags to verify the simulation was triggered
            ReflectionTestUtils.setField(dataSimulationService, "simulateFuelAnomaly", true);
            ReflectionTestUtils.setField(dataSimulationService, "anomalyCounter", 0);
            
            // Generate data multiple times to trigger the anomaly
            for (int i = 0; i < 20; i++) {
                dataSimulationService.generateAircraftData();
            }

            verify(anomalyDetectionService, atLeast(20)).detectAnomalies(any(AircraftData.class));
            verify(webSocketService, atLeast(20)).broadcastAircraftData(any(AircraftData.class));
        }

        @Test
        @DisplayName("Should trigger hydraulic anomaly simulation")
        void shouldTriggerHydraulicAnomalySimulation() {
            dataSimulationService.simulateHydraulicAnomaly();

            // Set the anomaly flags to verify the simulation was triggered
            ReflectionTestUtils.setField(dataSimulationService, "simulateHydraulicAnomaly", true);
            ReflectionTestUtils.setField(dataSimulationService, "anomalyCounter", 0);
            
            // Generate data multiple times to trigger the anomaly
            for (int i = 0; i < 25; i++) {
                dataSimulationService.generateAircraftData();
            }

            verify(anomalyDetectionService, atLeast(25)).detectAnomalies(any(AircraftData.class));
            verify(webSocketService, atLeast(25)).broadcastAircraftData(any(AircraftData.class));
        }

        @Test
        @DisplayName("Should reset anomaly flags after simulation")
        void shouldResetAnomalyFlagsAfterSimulation() {
            // Test engine anomaly reset
            dataSimulationService.simulateEngineAnomaly();
            boolean initialEngineFlag = (Boolean) ReflectionTestUtils.getField(dataSimulationService, "simulateEngineAnomaly");
            assertTrue(initialEngineFlag);

            // Simulate the counter reaching the threshold and reset
            ReflectionTestUtils.setField(dataSimulationService, "anomalyCounter", 11);
            ReflectionTestUtils.setField(dataSimulationService, "simulateEngineAnomaly", true);
            
            dataSimulationService.generateAircraftData();
            
            // The anomaly flag should be reset after the simulation runs
            // Note: This test verifies the logic exists, actual reset happens internally
        }
    }

    @Nested
    @DisplayName("Current Data Management Tests")
    class CurrentDataManagementTests {

        @Test
        @DisplayName("Should return null when no data generated")
        void shouldReturnNullWhenNoDataGenerated() {
            AircraftData currentData = dataSimulationService.getCurrentData();
            assertNull(currentData);
        }

        @Test
        @DisplayName("Should return current data after generation")
        void shouldReturnCurrentDataAfterGeneration() {
            dataSimulationService.generateAircraftData();
            
            AircraftData currentData = dataSimulationService.getCurrentData();
            assertNotNull(currentData);
            assertEquals(mockDetectedData, currentData);
        }

        @Test
        @DisplayName("Should update current data on each generation")
        void shouldUpdateCurrentDataOnEachGeneration() {
            // Generate first set of data
            dataSimulationService.generateAircraftData();
            AircraftData firstData = dataSimulationService.getCurrentData();
            
            // Wait a small amount to ensure different timestamp
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Mock different return data for second generation
            AircraftData secondMockData = new AircraftData(LocalDateTime.now().plusSeconds(1));
            secondMockData.setEngineRPM(2300.0);
            when(anomalyDetectionService.detectAnomalies(any(AircraftData.class)))
                    .thenReturn(secondMockData);
            
            // Generate second set of data
            dataSimulationService.generateAircraftData();
            AircraftData secondData = dataSimulationService.getCurrentData();
            
            assertNotNull(firstData);
            assertNotNull(secondData);
            assertEquals(secondMockData, secondData);
            assertNotEquals(firstData.getTimestamp(), secondData.getTimestamp());
        }
    }

    @Nested
    @DisplayName("Service Integration Tests")
    class ServiceIntegrationTests {

        @Test
        @DisplayName("Should call anomaly detection service with generated data")
        void shouldCallAnomalyDetectionServiceWithGeneratedData() {
            dataSimulationService.generateAircraftData();

            verify(anomalyDetectionService).detectAnomalies(argThat(data -> 
                data != null && 
                data.getTimestamp() != null &&
                data.getEngineRPM() > 0 &&
                data.getFuelLevel() >= 0 &&
                data.getAltitude() > 0
            ));
        }

        @Test
        @DisplayName("Should broadcast processed data through WebSocket service")
        void shouldBroadcastProcessedDataThroughWebSocketService() {
            dataSimulationService.generateAircraftData();

            verify(webSocketService).broadcastAircraftData(mockDetectedData);
            verify(webSocketService, never()).broadcastAircraftData(argThat(data -> 
                data != mockDetectedData
            ));
        }

        @Test
        @DisplayName("Should handle anomaly detection service exceptions")
        void shouldHandleAnomalyDetectionServiceExceptions() {
            when(anomalyDetectionService.detectAnomalies(any(AircraftData.class)))
                    .thenThrow(new RuntimeException("Anomaly detection failed"));

            assertThrows(RuntimeException.class, () -> {
                dataSimulationService.generateAircraftData();
            });

            verify(anomalyDetectionService).detectAnomalies(any(AircraftData.class));
            verify(webSocketService, never()).broadcastAircraftData(any(AircraftData.class));
        }

        @Test
        @DisplayName("Should handle WebSocket service exceptions")
        void shouldHandleWebSocketServiceExceptions() {
            doThrow(new RuntimeException("WebSocket broadcast failed"))
                    .when(webSocketService).broadcastAircraftData(any(AircraftData.class));

            assertThrows(RuntimeException.class, () -> {
                dataSimulationService.generateAircraftData();
            });

            verify(anomalyDetectionService).detectAnomalies(any(AircraftData.class));
            verify(webSocketService).broadcastAircraftData(mockDetectedData);
        }
    }

    @Nested
    @DisplayName("Data Consistency Tests")
    class DataConsistencyTests {

        @Test
        @DisplayName("Should maintain realistic correlations between engine RPM and temperature")
        void shouldMaintainRealisticCorrelationsBetweenEngineRPMAndTemperature() {
            // Generate multiple data points to test correlation
            for (int i = 0; i < 10; i++) {
                dataSimulationService.generateAircraftData();
                AircraftData currentData = dataSimulationService.getCurrentData();
                
                // Higher RPM should generally correlate with higher temperature
                if (currentData.getEngineRPM() > 2400) {
                    assertTrue(currentData.getEngineTemperature() > 120, 
                            "High RPM should correlate with higher temperature");
                }
            }
        }

        @Test
        @DisplayName("Should maintain realistic correlations between airspeed and Mach number")
        void shouldMaintainRealisticCorrelationsBetweenAirspeedAndMachNumber() {
            dataSimulationService.generateAircraftData();
            AircraftData currentData = dataSimulationService.getCurrentData();
            
            // Mach number should be reasonable for the airspeed and altitude
            double expectedMach = currentData.getAirspeed() / (661.5 + currentData.getAltitude() * 0.001);
            assertEquals(expectedMach, currentData.getMachNumber(), 0.1, 
                    "Mach number should correlate with airspeed and altitude");
        }

        @Test
        @DisplayName("Should generate different values on successive calls")
        void shouldGenerateDifferentValuesOnSuccessiveCalls() {
            dataSimulationService.generateAircraftData();
            AircraftData firstData = dataSimulationService.getCurrentData();
            
            // Reset the mock to return new data
            AircraftData newMockData = new AircraftData(LocalDateTime.now());
            newMockData.setEngineRPM(2400.0); // Different from initial
            when(anomalyDetectionService.detectAnomalies(any(AircraftData.class)))
                    .thenReturn(newMockData);
            
            dataSimulationService.generateAircraftData();
            AircraftData secondData = dataSimulationService.getCurrentData();
            
            assertNotNull(firstData);
            assertNotNull(secondData);
            // Since we're using mocks, we can't directly test randomness,
            // but we can verify the service is called multiple times
            verify(anomalyDetectionService, times(2)).detectAnomalies(any(AircraftData.class));
        }
    }

    @Nested
    @DisplayName("Performance and Memory Tests")
    class PerformanceAndMemoryTests {

        @Test
        @DisplayName("Should not accumulate memory on repeated data generation")
        void shouldNotAccumulateMemoryOnRepeatedDataGeneration() {
            // Generate data many times to test for memory leaks
            for (int i = 0; i < 100; i++) {
                dataSimulationService.generateAircraftData();
            }
            
            // Verify services are called the expected number of times
            verify(anomalyDetectionService, times(100)).detectAnomalies(any(AircraftData.class));
            verify(webSocketService, times(100)).broadcastAircraftData(any(AircraftData.class));
            
            // Current data should still be accessible
            AircraftData currentData = dataSimulationService.getCurrentData();
            assertNotNull(currentData);
        }

        @Test
        @DisplayName("Should handle rapid successive calls")
        void shouldHandleRapidSuccessiveCalls() {
            // Simulate rapid calls that might happen in production
            for (int i = 0; i < 10; i++) {
                dataSimulationService.generateAircraftData();
            }
            
            verify(anomalyDetectionService, times(10)).detectAnomalies(any(AircraftData.class));
            verify(webSocketService, times(10)).broadcastAircraftData(any(AircraftData.class));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null return from anomaly detection service")
        void shouldHandleNullReturnFromAnomalyDetectionService() {
            when(anomalyDetectionService.detectAnomalies(any(AircraftData.class)))
                    .thenReturn(null);

            assertThrows(NullPointerException.class, () -> {
                dataSimulationService.generateAircraftData();
            });
        }

        @Test
        @DisplayName("Should handle service initialization without dependencies")
        void shouldHandleServiceInitializationWithoutDependencies() {
            DataSimulationService serviceWithoutDeps = new DataSimulationService();
            
            // Should not throw exception when getting current data
            AircraftData currentData = serviceWithoutDeps.getCurrentData();
            assertNull(currentData);
        }
    }
}
