package com.aircraft.monitoring.service;

import com.aircraft.monitoring.model.AircraftData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnomalyDetectionService.
 * 
 * Tests anomaly detection logic for all aircraft systems including
 * engine, fuel, hydraulic, altitude, and airspeed systems.
 * 
 * @author Aircraft Monitoring Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AnomalyDetectionService Tests")
class AnomalyDetectionServiceTest {

    @InjectMocks
    private AnomalyDetectionService anomalyDetectionService;

    private AircraftData normalData;
    private AircraftData anomalousData;

    @BeforeEach
    void setUp() {
        normalData = createNormalAircraftData();
        anomalousData = createAnomalousAircraftData();
    }

    private AircraftData createNormalAircraftData() {
        AircraftData data = new AircraftData(LocalDateTime.now());
        
        // Normal engine values
        data.setEngineRPM(2200.0);
        data.setEngineTemperature(150.0);
        data.setOilPressure(45.0);
        data.setOilTemperature(90.0);
        
        // Normal fuel values
        data.setFuelLevel(75.0);
        data.setFuelConsumption(250.0);
        data.setFuelPressure(25.0);
        data.setFuelTemperature(20.0);
        
        // Normal hydraulic values
        data.setHydraulicPressure(2800.0);
        data.setHydraulicTemperature(55.0);
        data.setHydraulicFluidLevel(95.0);
        
        // Normal flight data
        data.setAltitude(35000.0);
        data.setAirspeed(450.0);
        data.setGroundSpeed(440.0);
        data.setMachNumber(0.7);
        data.setVerticalSpeed(-200.0);
        
        return data;
    }

    private AircraftData createAnomalousAircraftData() {
        AircraftData data = new AircraftData(LocalDateTime.now());
        
        // Anomalous engine values
        data.setEngineRPM(4000.0); // Too high
        data.setEngineTemperature(250.0); // Too high
        data.setOilPressure(10.0); // Too low
        data.setOilTemperature(150.0); // Too high
        
        // Anomalous fuel values
        data.setFuelLevel(15.0); // Too low
        data.setFuelConsumption(1200.0); // Too high
        data.setFuelPressure(5.0); // Too low
        data.setFuelTemperature(25.0);
        
        // Anomalous hydraulic values
        data.setHydraulicPressure(1800.0); // Too low
        data.setHydraulicTemperature(90.0); // Too high
        data.setHydraulicFluidLevel(70.0); // Too low
        
        // Anomalous flight data
        data.setAltitude(50000.0); // Too high
        data.setAirspeed(700.0); // Too high
        data.setGroundSpeed(690.0);
        data.setMachNumber(1.2); // Too high
        data.setVerticalSpeed(6000.0); // Too high
        
        return data;
    }

    @Nested
    @DisplayName("Main Detection Method Tests")
    class MainDetectionMethodTests {

        @Test
        @DisplayName("Should detect no anomalies in normal data")
        void shouldDetectNoAnomaliesInNormalData() {
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertNotNull(result);
            assertFalse(result.hasAnyAnomaly());
            assertFalse(result.isEngineAnomaly());
            assertFalse(result.isFuelAnomaly());
            assertFalse(result.isHydraulicAnomaly());
            assertFalse(result.isAltitudeAnomaly());
            assertFalse(result.isAirspeedAnomaly());
            assertEquals("NORMAL", result.getSystemStatus());
        }

        @Test
        @DisplayName("Should detect all anomalies in anomalous data")
        void shouldDetectAllAnomaliesInAnomalousData() {
            AircraftData result = anomalyDetectionService.detectAnomalies(anomalousData);
            
            assertNotNull(result);
            assertTrue(result.hasAnyAnomaly());
            assertTrue(result.isEngineAnomaly());
            assertTrue(result.isFuelAnomaly());
            assertTrue(result.isHydraulicAnomaly());
            assertTrue(result.isAltitudeAnomaly());
            assertTrue(result.isAirspeedAnomaly());
            assertEquals("WARNING", result.getSystemStatus());
        }

        @Test
        @DisplayName("Should handle null input gracefully")
        void shouldHandleNullInputGracefully() {
            assertThrows(NullPointerException.class, () -> {
                anomalyDetectionService.detectAnomalies(null);
            });
        }

        @Test
        @DisplayName("Should preserve original timestamp")
        void shouldPreserveOriginalTimestamp() {
            LocalDateTime originalTimestamp = normalData.getTimestamp();
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertEquals(originalTimestamp, result.getTimestamp());
        }
    }

    @Nested
    @DisplayName("Engine Anomaly Detection Tests")
    class EngineAnomalyDetectionTests {

        @Test
        @DisplayName("Should detect engine RPM too low")
        void shouldDetectEngineRPMTooLow() {
            normalData.setEngineRPM(400.0); // Below minimum of 500
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isEngineAnomaly());
        }

        @Test
        @DisplayName("Should detect engine RPM too high")
        void shouldDetectEngineRPMTooHigh() {
            normalData.setEngineRPM(3500.0); // Above maximum of 3000
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isEngineAnomaly());
        }

        @Test
        @DisplayName("Should detect engine temperature too high")
        void shouldDetectEngineTemperatureTooHigh() {
            normalData.setEngineTemperature(250.0); // Above maximum of 200
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isEngineAnomaly());
        }

        @Test
        @DisplayName("Should detect oil pressure too low")
        void shouldDetectOilPressureTooLow() {
            normalData.setOilPressure(15.0); // Below minimum of 20
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isEngineAnomaly());
        }

        @Test
        @DisplayName("Should detect oil pressure too high")
        void shouldDetectOilPressureTooHigh() {
            normalData.setOilPressure(110.0); // Above maximum of 100
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isEngineAnomaly());
        }

        @Test
        @DisplayName("Should detect oil temperature too high")
        void shouldDetectOilTemperatureTooHigh() {
            normalData.setOilTemperature(130.0); // Above maximum of 120
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isEngineAnomaly());
        }

        @Test
        @DisplayName("Should not detect anomaly for normal engine values")
        void shouldNotDetectAnomalyForNormalEngineValues() {
            normalData.setEngineRPM(2200.0);
            normalData.setEngineTemperature(150.0);
            normalData.setOilPressure(45.0);
            normalData.setOilTemperature(90.0);
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertFalse(result.isEngineAnomaly());
        }

        @Test
        @DisplayName("Should detect anomaly for boundary values")
        void shouldDetectAnomalyForBoundaryValues() {
            // Test exact boundary values
            normalData.setEngineRPM(500.0); // Exactly at minimum
            AircraftData result1 = anomalyDetectionService.detectAnomalies(normalData);
            assertFalse(result1.isEngineAnomaly());
            
            normalData.setEngineRPM(499.9); // Just below minimum
            AircraftData result2 = anomalyDetectionService.detectAnomalies(normalData);
            assertTrue(result2.isEngineAnomaly());
        }
    }

    @Nested
    @DisplayName("Fuel Anomaly Detection Tests")
    class FuelAnomalyDetectionTests {

        @Test
        @DisplayName("Should detect low fuel level")
        void shouldDetectLowFuelLevel() {
            normalData.setFuelLevel(15.0); // Below minimum of 20%
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isFuelAnomaly());
        }

        @Test
        @DisplayName("Should detect high fuel consumption")
        void shouldDetectHighFuelConsumption() {
            normalData.setFuelConsumption(1200.0); // Above maximum of 1000
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isFuelAnomaly());
        }

        @Test
        @DisplayName("Should detect fuel pressure too low")
        void shouldDetectFuelPressureTooLow() {
            normalData.setFuelPressure(5.0); // Below minimum of 10
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isFuelAnomaly());
        }

        @Test
        @DisplayName("Should detect fuel pressure too high")
        void shouldDetectFuelPressureTooHigh() {
            normalData.setFuelPressure(60.0); // Above maximum of 50
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isFuelAnomaly());
        }

        @Test
        @DisplayName("Should not detect anomaly for normal fuel values")
        void shouldNotDetectAnomalyForNormalFuelValues() {
            normalData.setFuelLevel(75.0);
            normalData.setFuelConsumption(250.0);
            normalData.setFuelPressure(25.0);
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertFalse(result.isFuelAnomaly());
        }

        @Test
        @DisplayName("Should handle zero fuel level as anomaly")
        void shouldHandleZeroFuelLevelAsAnomaly() {
            normalData.setFuelLevel(0.0);
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isFuelAnomaly());
        }
    }

    @Nested
    @DisplayName("Hydraulic Anomaly Detection Tests")
    class HydraulicAnomalyDetectionTests {

        @Test
        @DisplayName("Should detect hydraulic pressure too low")
        void shouldDetectHydraulicPressureTooLow() {
            normalData.setHydraulicPressure(1800.0); // Below minimum of 2000
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isHydraulicAnomaly());
        }

        @Test
        @DisplayName("Should detect hydraulic pressure too high")
        void shouldDetectHydraulicPressureTooHigh() {
            normalData.setHydraulicPressure(3600.0); // Above maximum of 3500
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isHydraulicAnomaly());
        }

        @Test
        @DisplayName("Should detect hydraulic temperature too high")
        void shouldDetectHydraulicTemperatureTooHigh() {
            normalData.setHydraulicTemperature(90.0); // Above maximum of 80
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isHydraulicAnomaly());
        }

        @Test
        @DisplayName("Should detect low hydraulic fluid level")
        void shouldDetectLowHydraulicFluidLevel() {
            normalData.setHydraulicFluidLevel(70.0); // Below minimum of 80%
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isHydraulicAnomaly());
        }

        @Test
        @DisplayName("Should not detect anomaly for normal hydraulic values")
        void shouldNotDetectAnomalyForNormalHydraulicValues() {
            normalData.setHydraulicPressure(2800.0);
            normalData.setHydraulicTemperature(55.0);
            normalData.setHydraulicFluidLevel(95.0);
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertFalse(result.isHydraulicAnomaly());
        }
    }

    @Nested
    @DisplayName("Altitude Anomaly Detection Tests")
    class AltitudeAnomalyDetectionTests {

        @Test
        @DisplayName("Should detect altitude too high")
        void shouldDetectAltitudeTooHigh() {
            normalData.setAltitude(50000.0); // Above maximum of 45000
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isAltitudeAnomaly());
        }

        @Test
        @DisplayName("Should detect vertical speed too high positive")
        void shouldDetectVerticalSpeedTooHighPositive() {
            normalData.setVerticalSpeed(6000.0); // Above maximum of 5000
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isAltitudeAnomaly());
        }

        @Test
        @DisplayName("Should detect vertical speed too high negative")
        void shouldDetectVerticalSpeedTooHighNegative() {
            normalData.setVerticalSpeed(-6000.0); // Below minimum of -5000
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isAltitudeAnomaly());
        }

        @Test
        @DisplayName("Should not detect anomaly for normal altitude values")
        void shouldNotDetectAnomalyForNormalAltitudeValues() {
            normalData.setAltitude(35000.0);
            normalData.setVerticalSpeed(-200.0);
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertFalse(result.isAltitudeAnomaly());
        }

        @Test
        @DisplayName("Should handle negative altitude")
        void shouldHandleNegativeAltitude() {
            normalData.setAltitude(-1000.0); // Below sea level
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            // Negative altitude is allowed (below sea level)
            assertFalse(result.isAltitudeAnomaly());
        }
    }

    @Nested
    @DisplayName("Airspeed Anomaly Detection Tests")
    class AirspeedAnomalyDetectionTests {

        @Test
        @DisplayName("Should detect airspeed too high")
        void shouldDetectAirspeedTooHigh() {
            normalData.setAirspeed(700.0); // Above maximum of 600
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isAirspeedAnomaly());
        }

        @Test
        @DisplayName("Should detect Mach number too high")
        void shouldDetectMachNumberTooHigh() {
            normalData.setMachNumber(1.2); // Above maximum of 0.9
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.isAirspeedAnomaly());
        }

        @Test
        @DisplayName("Should not detect anomaly for normal airspeed values")
        void shouldNotDetectAnomalyForNormalAirspeedValues() {
            normalData.setAirspeed(450.0);
            normalData.setMachNumber(0.7);
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertFalse(result.isAirspeedAnomaly());
        }

        @Test
        @DisplayName("Should handle zero airspeed")
        void shouldHandleZeroAirspeed() {
            normalData.setAirspeed(0.0);
            normalData.setMachNumber(0.0);
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            // Zero airspeed is allowed (aircraft on ground)
            assertFalse(result.isAirspeedAnomaly());
        }

        @Test
        @DisplayName("Should detect anomaly for boundary Mach values")
        void shouldDetectAnomalyForBoundaryMachValues() {
            normalData.setMachNumber(0.9); // Exactly at maximum
            AircraftData result1 = anomalyDetectionService.detectAnomalies(normalData);
            assertFalse(result1.isAirspeedAnomaly());
            
            normalData.setMachNumber(0.91); // Just above maximum
            AircraftData result2 = anomalyDetectionService.detectAnomalies(normalData);
            assertTrue(result2.isAirspeedAnomaly());
        }
    }

    @Nested
    @DisplayName("Multiple Anomaly Tests")
    class MultipleAnomalyTests {

        @Test
        @DisplayName("Should detect multiple system anomalies")
        void shouldDetectMultipleSystemAnomalies() {
            normalData.setEngineRPM(4000.0); // Engine anomaly
            normalData.setFuelLevel(10.0); // Fuel anomaly
            normalData.setHydraulicPressure(1500.0); // Hydraulic anomaly
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.hasAnyAnomaly());
            assertTrue(result.isEngineAnomaly());
            assertTrue(result.isFuelAnomaly());
            assertTrue(result.isHydraulicAnomaly());
            assertFalse(result.isAltitudeAnomaly());
            assertFalse(result.isAirspeedAnomaly());
        }

        @Test
        @DisplayName("Should handle mix of normal and anomalous values")
        void shouldHandleMixOfNormalAndAnomalousValues() {
            normalData.setEngineRPM(2200.0); // Normal
            normalData.setFuelLevel(10.0); // Anomaly
            normalData.setAltitude(35000.0); // Normal
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertTrue(result.hasAnyAnomaly());
            assertFalse(result.isEngineAnomaly());
            assertTrue(result.isFuelAnomaly());
            assertFalse(result.isAltitudeAnomaly());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle extreme values")
        void shouldHandleExtremeValues() {
            normalData.setEngineRPM(Double.MAX_VALUE);
            normalData.setAltitude(Double.MIN_VALUE);
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertNotNull(result);
            assertTrue(result.isEngineAnomaly()); // Extreme RPM should be anomaly
        }

        @Test
        @DisplayName("Should handle NaN values")
        void shouldHandleNaNValues() {
            normalData.setEngineRPM(Double.NaN);
            normalData.setFuelLevel(Double.NaN);
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertNotNull(result);
            // NaN comparisons always return false, so these should be considered anomalies
            assertTrue(result.isEngineAnomaly());
            assertTrue(result.isFuelAnomaly());
        }

        @Test
        @DisplayName("Should handle infinite values")
        void shouldHandleInfiniteValues() {
            normalData.setAirspeed(Double.POSITIVE_INFINITY);
            normalData.setVerticalSpeed(Double.NEGATIVE_INFINITY);
            
            AircraftData result = anomalyDetectionService.detectAnomalies(normalData);
            
            assertNotNull(result);
            assertTrue(result.isAirspeedAnomaly());
            assertTrue(result.isAltitudeAnomaly());
        }
    }
}
