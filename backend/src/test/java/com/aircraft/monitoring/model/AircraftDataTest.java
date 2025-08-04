package com.aircraft.monitoring.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AircraftData model class.
 * 
 * Tests data validation, getters/setters, equals/hashCode, 
 * and business logic methods.
 * 
 * @author Aircraft Monitoring Team
 * @version 1.0.0
 */
@DisplayName("AircraftData Model Tests")
class AircraftDataTest {

    private AircraftData aircraftData;
    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = LocalDateTime.now();
        aircraftData = new AircraftData(testTimestamp);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create AircraftData with timestamp")
        void shouldCreateAircraftDataWithTimestamp() {
            AircraftData data = new AircraftData(testTimestamp);
            
            assertNotNull(data);
            assertEquals(testTimestamp, data.getTimestamp());
        }

        @Test
        @DisplayName("Should create AircraftData with no-args constructor")
        void shouldCreateAircraftDataWithNoArgsConstructor() {
            AircraftData data = new AircraftData();
            
            assertNotNull(data);
            assertNull(data.getTimestamp());
        }

        @Test
        @DisplayName("Should create AircraftData with all args constructor")
        void shouldCreateAircraftDataWithAllArgsConstructor() {
            AircraftData data = new AircraftData(
                testTimestamp, 2200.0, 150.0, 45.0, 90.0,
                75.0, 250.0, 30.0, 20.0,
                2800.0, 55.0, 95.0,
                35000.0, 450.0, 440.0, 0.7, -200.0,
                11.5, 23.0, 28.5, 115.0,
                false, false, false, false, false
            );
            
            assertNotNull(data);
            assertEquals(testTimestamp, data.getTimestamp());
            assertEquals(2200.0, data.getEngineRPM());
            assertEquals(75.0, data.getFuelLevel());
            assertEquals(2800.0, data.getHydraulicPressure());
        }
    }

    @Nested
    @DisplayName("Engine System Data Tests")
    class EngineSystemTests {

        @Test
        @DisplayName("Should set and get engine RPM")
        void shouldSetAndGetEngineRPM() {
            double expectedRPM = 2500.0;
            aircraftData.setEngineRPM(expectedRPM);
            
            assertEquals(expectedRPM, aircraftData.getEngineRPM());
        }

        @Test
        @DisplayName("Should set and get engine temperature")
        void shouldSetAndGetEngineTemperature() {
            double expectedTemp = 180.0;
            aircraftData.setEngineTemperature(expectedTemp);
            
            assertEquals(expectedTemp, aircraftData.getEngineTemperature());
        }

        @Test
        @DisplayName("Should set and get oil pressure")
        void shouldSetAndGetOilPressure() {
            double expectedPressure = 60.0;
            aircraftData.setOilPressure(expectedPressure);
            
            assertEquals(expectedPressure, aircraftData.getOilPressure());
        }

        @Test
        @DisplayName("Should set and get oil temperature")
        void shouldSetAndGetOilTemperature() {
            double expectedTemp = 85.0;
            aircraftData.setOilTemperature(expectedTemp);
            
            assertEquals(expectedTemp, aircraftData.getOilTemperature());
        }
    }

    @Nested
    @DisplayName("Fuel System Data Tests")
    class FuelSystemTests {

        @Test
        @DisplayName("Should set and get fuel level")
        void shouldSetAndGetFuelLevel() {
            double expectedLevel = 80.5;
            aircraftData.setFuelLevel(expectedLevel);
            
            assertEquals(expectedLevel, aircraftData.getFuelLevel());
        }

        @Test
        @DisplayName("Should set and get fuel consumption")
        void shouldSetAndGetFuelConsumption() {
            double expectedConsumption = 275.5;
            aircraftData.setFuelConsumption(expectedConsumption);
            
            assertEquals(expectedConsumption, aircraftData.getFuelConsumption());
        }

        @Test
        @DisplayName("Should set and get fuel pressure")
        void shouldSetAndGetFuelPressure() {
            double expectedPressure = 35.0;
            aircraftData.setFuelPressure(expectedPressure);
            
            assertEquals(expectedPressure, aircraftData.getFuelPressure());
        }

        @Test
        @DisplayName("Should set and get fuel temperature")
        void shouldSetAndGetFuelTemperature() {
            double expectedTemp = 22.5;
            aircraftData.setFuelTemperature(expectedTemp);
            
            assertEquals(expectedTemp, aircraftData.getFuelTemperature());
        }
    }

    @Nested
    @DisplayName("Hydraulic System Data Tests")
    class HydraulicSystemTests {

        @Test
        @DisplayName("Should set and get hydraulic pressure")
        void shouldSetAndGetHydraulicPressure() {
            double expectedPressure = 3000.0;
            aircraftData.setHydraulicPressure(expectedPressure);
            
            assertEquals(expectedPressure, aircraftData.getHydraulicPressure());
        }

        @Test
        @DisplayName("Should set and get hydraulic temperature")
        void shouldSetAndGetHydraulicTemperature() {
            double expectedTemp = 65.0;
            aircraftData.setHydraulicTemperature(expectedTemp);
            
            assertEquals(expectedTemp, aircraftData.getHydraulicTemperature());
        }

        @Test
        @DisplayName("Should set and get hydraulic fluid level")
        void shouldSetAndGetHydraulicFluidLevel() {
            double expectedLevel = 92.5;
            aircraftData.setHydraulicFluidLevel(expectedLevel);
            
            assertEquals(expectedLevel, aircraftData.getHydraulicFluidLevel());
        }
    }

    @Nested
    @DisplayName("Flight Data Tests")
    class FlightDataTests {

        @Test
        @DisplayName("Should set and get altitude")
        void shouldSetAndGetAltitude() {
            double expectedAltitude = 37000.0;
            aircraftData.setAltitude(expectedAltitude);
            
            assertEquals(expectedAltitude, aircraftData.getAltitude());
        }

        @Test
        @DisplayName("Should set and get airspeed")
        void shouldSetAndGetAirspeed() {
            double expectedAirspeed = 480.0;
            aircraftData.setAirspeed(expectedAirspeed);
            
            assertEquals(expectedAirspeed, aircraftData.getAirspeed());
        }

        @Test
        @DisplayName("Should set and get ground speed")
        void shouldSetAndGetGroundSpeed() {
            double expectedGroundSpeed = 475.0;
            aircraftData.setGroundSpeed(expectedGroundSpeed);
            
            assertEquals(expectedGroundSpeed, aircraftData.getGroundSpeed());
        }

        @Test
        @DisplayName("Should set and get mach number")
        void shouldSetAndGetMachNumber() {
            double expectedMach = 0.75;
            aircraftData.setMachNumber(expectedMach);
            
            assertEquals(expectedMach, aircraftData.getMachNumber());
        }

        @Test
        @DisplayName("Should set and get vertical speed")
        void shouldSetAndGetVerticalSpeed() {
            double expectedVerticalSpeed = -150.0;
            aircraftData.setVerticalSpeed(expectedVerticalSpeed);
            
            assertEquals(expectedVerticalSpeed, aircraftData.getVerticalSpeed());
        }
    }

    @Nested
    @DisplayName("Additional Systems Data Tests")
    class AdditionalSystemsTests {

        @Test
        @DisplayName("Should set and get cabin pressure")
        void shouldSetAndGetCabinPressure() {
            double expectedPressure = 11.8;
            aircraftData.setCabinPressure(expectedPressure);
            
            assertEquals(expectedPressure, aircraftData.getCabinPressure());
        }

        @Test
        @DisplayName("Should set and get cabin temperature")
        void shouldSetAndGetCabinTemperature() {
            double expectedTemp = 24.5;
            aircraftData.setCabinTemperature(expectedTemp);
            
            assertEquals(expectedTemp, aircraftData.getCabinTemperature());
        }

        @Test
        @DisplayName("Should set and get battery voltage")
        void shouldSetAndGetBatteryVoltage() {
            double expectedVoltage = 28.5;
            aircraftData.setBatteryVoltage(expectedVoltage);
            
            assertEquals(expectedVoltage, aircraftData.getBatteryVoltage());
        }

        @Test
        @DisplayName("Should set and get generator output")
        void shouldSetAndGetGeneratorOutput() {
            double expectedOutput = 118.0;
            aircraftData.setGeneratorOutput(expectedOutput);
            
            assertEquals(expectedOutput, aircraftData.getGeneratorOutput());
        }
    }

    @Nested
    @DisplayName("Anomaly Detection Tests")
    class AnomalyDetectionTests {

        @Test
        @DisplayName("Should set and get engine anomaly flag")
        void shouldSetAndGetEngineAnomaly() {
            aircraftData.setEngineAnomaly(true);
            assertTrue(aircraftData.isEngineAnomaly());
            
            aircraftData.setEngineAnomaly(false);
            assertFalse(aircraftData.isEngineAnomaly());
        }

        @Test
        @DisplayName("Should set and get fuel anomaly flag")
        void shouldSetAndGetFuelAnomaly() {
            aircraftData.setFuelAnomaly(true);
            assertTrue(aircraftData.isFuelAnomaly());
            
            aircraftData.setFuelAnomaly(false);
            assertFalse(aircraftData.isFuelAnomaly());
        }

        @Test
        @DisplayName("Should set and get hydraulic anomaly flag")
        void shouldSetAndGetHydraulicAnomaly() {
            aircraftData.setHydraulicAnomaly(true);
            assertTrue(aircraftData.isHydraulicAnomaly());
            
            aircraftData.setHydraulicAnomaly(false);
            assertFalse(aircraftData.isHydraulicAnomaly());
        }

        @Test
        @DisplayName("Should set and get altitude anomaly flag")
        void shouldSetAndGetAltitudeAnomaly() {
            aircraftData.setAltitudeAnomaly(true);
            assertTrue(aircraftData.isAltitudeAnomaly());
            
            aircraftData.setAltitudeAnomaly(false);
            assertFalse(aircraftData.isAltitudeAnomaly());
        }

        @Test
        @DisplayName("Should set and get airspeed anomaly flag")
        void shouldSetAndGetAirspeedAnomaly() {
            aircraftData.setAirspeedAnomaly(true);
            assertTrue(aircraftData.isAirspeedAnomaly());
            
            aircraftData.setAirspeedAnomaly(false);
            assertFalse(aircraftData.isAirspeedAnomaly());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should return false when no anomalies detected")
        void shouldReturnFalseWhenNoAnomaliesDetected() {
            // All anomaly flags are false by default
            assertFalse(aircraftData.hasAnyAnomaly());
        }

        @Test
        @DisplayName("Should return true when engine anomaly detected")
        void shouldReturnTrueWhenEngineAnomalyDetected() {
            aircraftData.setEngineAnomaly(true);
            assertTrue(aircraftData.hasAnyAnomaly());
        }

        @Test
        @DisplayName("Should return true when fuel anomaly detected")
        void shouldReturnTrueWhenFuelAnomalyDetected() {
            aircraftData.setFuelAnomaly(true);
            assertTrue(aircraftData.hasAnyAnomaly());
        }

        @Test
        @DisplayName("Should return true when hydraulic anomaly detected")
        void shouldReturnTrueWhenHydraulicAnomalyDetected() {
            aircraftData.setHydraulicAnomaly(true);
            assertTrue(aircraftData.hasAnyAnomaly());
        }

        @Test
        @DisplayName("Should return true when altitude anomaly detected")
        void shouldReturnTrueWhenAltitudeAnomalyDetected() {
            aircraftData.setAltitudeAnomaly(true);
            assertTrue(aircraftData.hasAnyAnomaly());
        }

        @Test
        @DisplayName("Should return true when airspeed anomaly detected")
        void shouldReturnTrueWhenAirspeedAnomalyDetected() {
            aircraftData.setAirspeedAnomaly(true);
            assertTrue(aircraftData.hasAnyAnomaly());
        }

        @Test
        @DisplayName("Should return true when multiple anomalies detected")
        void shouldReturnTrueWhenMultipleAnomaliesDetected() {
            aircraftData.setEngineAnomaly(true);
            aircraftData.setFuelAnomaly(true);
            assertTrue(aircraftData.hasAnyAnomaly());
        }

        @Test
        @DisplayName("Should return NORMAL status when no anomalies")
        void shouldReturnNormalStatusWhenNoAnomalies() {
            assertEquals("NORMAL", aircraftData.getSystemStatus());
        }

        @Test
        @DisplayName("Should return WARNING status when anomalies detected")
        void shouldReturnWarningStatusWhenAnomaliesDetected() {
            aircraftData.setEngineAnomaly(true);
            assertEquals("WARNING", aircraftData.getSystemStatus());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(aircraftData, aircraftData);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, aircraftData);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            assertNotEquals(aircraftData, "string");
        }

        @Test
        @DisplayName("Should be equal to another AircraftData with same values")
        void shouldBeEqualToAnotherAircraftDataWithSameValues() {
            AircraftData other = new AircraftData(testTimestamp);
            other.setEngineRPM(2200.0);
            other.setAltitude(35000.0);
            
            aircraftData.setEngineRPM(2200.0);
            aircraftData.setAltitude(35000.0);
            
            assertEquals(aircraftData, other);
            assertEquals(aircraftData.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to AircraftData with different values")
        void shouldNotBeEqualToAircraftDataWithDifferentValues() {
            AircraftData other = new AircraftData(testTimestamp);
            other.setEngineRPM(2500.0);
            
            aircraftData.setEngineRPM(2200.0);
            
            assertNotEquals(aircraftData, other);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Validation Tests")
    class EdgeCasesAndValidationTests {

        @Test
        @DisplayName("Should handle zero values")
        void shouldHandleZeroValues() {
            aircraftData.setEngineRPM(0.0);
            aircraftData.setFuelLevel(0.0);
            aircraftData.setAltitude(0.0);
            
            assertEquals(0.0, aircraftData.getEngineRPM());
            assertEquals(0.0, aircraftData.getFuelLevel());
            assertEquals(0.0, aircraftData.getAltitude());
        }

        @Test
        @DisplayName("Should handle negative values")
        void shouldHandleNegativeValues() {
            aircraftData.setVerticalSpeed(-1000.0);
            aircraftData.setEngineTemperature(-50.0);
            
            assertEquals(-1000.0, aircraftData.getVerticalSpeed());
            assertEquals(-50.0, aircraftData.getEngineTemperature());
        }

        @Test
        @DisplayName("Should handle large values")
        void shouldHandleLargeValues() {
            aircraftData.setAltitude(50000.0);
            aircraftData.setEngineRPM(5000.0);
            
            assertEquals(50000.0, aircraftData.getAltitude());
            assertEquals(5000.0, aircraftData.getEngineRPM());
        }

        @Test
        @DisplayName("Should handle decimal precision")
        void shouldHandleDecimalPrecision() {
            double preciseValue = 123.456789;
            aircraftData.setMachNumber(preciseValue);
            
            assertEquals(preciseValue, aircraftData.getMachNumber(), 0.000001);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate non-null toString")
        void shouldGenerateNonNullToString() {
            String toString = aircraftData.toString();
            assertNotNull(toString);
            assertFalse(toString.isEmpty());
        }

        @Test
        @DisplayName("Should include class name in toString")
        void shouldIncludeClassNameInToString() {
            String toString = aircraftData.toString();
            assertTrue(toString.contains("AircraftData"));
        }
    }
}
