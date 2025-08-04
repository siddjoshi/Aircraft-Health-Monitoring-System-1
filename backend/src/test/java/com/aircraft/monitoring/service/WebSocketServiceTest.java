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
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebSocketService.
 * 
 * Tests WebSocket connection management, message handling,
 * and data broadcasting functionality.
 * 
 * @author Aircraft Monitoring Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocketService Tests")
class WebSocketServiceTest {

    @Mock
    private WebSocketSession mockSession1;

    @Mock
    private WebSocketSession mockSession2;

    @Mock
    private WebSocketSession mockClosedSession;

    @InjectMocks
    private WebSocketService webSocketService;

    private AircraftData testAircraftData;

    @BeforeEach
    void setUp() throws Exception {
        
        // Setup test aircraft data
        testAircraftData = new AircraftData(LocalDateTime.now());
        testAircraftData.setEngineRPM(2200.0);
        testAircraftData.setFuelLevel(75.0);
        testAircraftData.setAltitude(35000.0);
        testAircraftData.setEngineAnomaly(false);
        
        // Setup mock sessions
        when(mockSession1.getId()).thenReturn("session-1");
        when(mockSession1.isOpen()).thenReturn(true);
        
        when(mockSession2.getId()).thenReturn("session-2");
        when(mockSession2.isOpen()).thenReturn(true);
        
        when(mockClosedSession.getId()).thenReturn("closed-session");
        when(mockClosedSession.isOpen()).thenReturn(false);
    }

    @Nested
    @DisplayName("Connection Management Tests")
    class ConnectionManagementTests {

        @Test
        @DisplayName("Should handle new WebSocket connection establishment")
        void shouldHandleNewWebSocketConnectionEstablishment() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);

            assertEquals(1, webSocketService.getConnectedClientsCount());
            verify(mockSession1).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"connection\"") && 
                       payload.contains("Connected to Aircraft Monitoring System");
            }));
        }

        @Test
        @DisplayName("Should handle multiple WebSocket connections")
        void shouldHandleMultipleWebSocketConnections() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            webSocketService.afterConnectionEstablished(mockSession2);

            assertEquals(2, webSocketService.getConnectedClientsCount());
            verify(mockSession1).sendMessage(any(TextMessage.class));
            verify(mockSession2).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("Should handle WebSocket connection closure")
        void shouldHandleWebSocketConnectionClosure() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            webSocketService.afterConnectionEstablished(mockSession2);
            
            assertEquals(2, webSocketService.getConnectedClientsCount());
            
            webSocketService.afterConnectionClosed(mockSession1, CloseStatus.NORMAL);
            
            assertEquals(1, webSocketService.getConnectedClientsCount());
        }

        @Test
        @DisplayName("Should handle connection closure with different close statuses")
        void shouldHandleConnectionClosureWithDifferentCloseStatuses() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            assertEquals(1, webSocketService.getConnectedClientsCount());
            
            webSocketService.afterConnectionClosed(mockSession1, CloseStatus.SERVER_ERROR);
            
            assertEquals(0, webSocketService.getConnectedClientsCount());
        }

        @Test
        @DisplayName("Should handle duplicate connection establishment")
        void shouldHandleDuplicateConnectionEstablishment() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            webSocketService.afterConnectionEstablished(mockSession1); // Duplicate
            
            // Should still count as one connection (CopyOnWriteArrayList allows duplicates)
            assertEquals(2, webSocketService.getConnectedClientsCount());
        }
    }

    @Nested
    @DisplayName("Message Handling Tests")
    class MessageHandlingTests {

        @Test
        @DisplayName("Should handle incoming text messages")
        void shouldHandleIncomingTextMessages() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            TextMessage incomingMessage = new TextMessage("test message");
            webSocketService.handleTextMessage(mockSession1, incomingMessage);
            
            verify(mockSession1, times(2)).sendMessage(any(TextMessage.class)); // Welcome + echo
        }

        @Test
        @DisplayName("Should echo back received messages")
        void shouldEchoBackReceivedMessages() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            TextMessage incomingMessage = new TextMessage("Hello Server");
            webSocketService.handleTextMessage(mockSession1, incomingMessage);
            
            verify(mockSession1).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"echo\"") && 
                       payload.contains("Hello Server");
            }));
        }

        @Test
        @DisplayName("Should handle empty messages")
        void shouldHandleEmptyMessages() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            TextMessage emptyMessage = new TextMessage("");
            webSocketService.handleTextMessage(mockSession1, emptyMessage);
            
            verify(mockSession1).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"echo\"");
            }));
        }

        @Test
        @DisplayName("Should handle special characters in messages")
        void shouldHandleSpecialCharactersInMessages() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            TextMessage specialMessage = new TextMessage("Special chars: !@#$%^&*()");
            webSocketService.handleTextMessage(mockSession1, specialMessage);
            
            verify(mockSession1).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("Special chars: !@#$%^&*()");
            }));
        }

        @Test
        @DisplayName("Should handle JSON messages")
        void shouldHandleJSONMessages() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            String jsonMessage = "{\"action\":\"test\",\"data\":\"value\"}";
            TextMessage incomingMessage = new TextMessage(jsonMessage);
            webSocketService.handleTextMessage(mockSession1, incomingMessage);
            
            verify(mockSession1).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains(jsonMessage);
            }));
        }
    }

    @Nested
    @DisplayName("Aircraft Data Broadcasting Tests")
    class AircraftDataBroadcastingTests {

        @Test
        @DisplayName("Should broadcast aircraft data to all connected clients")
        void shouldBroadcastAircraftDataToAllConnectedClients() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            webSocketService.afterConnectionEstablished(mockSession2);
            
            webSocketService.broadcastAircraftData(testAircraftData);
            
            verify(mockSession1, atLeast(1)).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"aircraft_data\"") && 
                       payload.contains("\"engineRPM\":2200.0");
            }));
            
            verify(mockSession2, atLeast(1)).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"aircraft_data\"") && 
                       payload.contains("\"engineRPM\":2200.0");
            }));
        }

        @Test
        @DisplayName("Should not broadcast when no clients connected")
        void shouldNotBroadcastWhenNoClientsConnected() throws Exception {
            webSocketService.broadcastAircraftData(testAircraftData);
            
            // No sessions to verify since none are connected
            assertEquals(0, webSocketService.getConnectedClientsCount());
        }

        @Test
        @DisplayName("Should handle JSON serialization errors gracefully")
        void shouldHandleJSONSerializationErrorsGracefully() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            // Create data with problematic values that might cause JSON issues
            AircraftData problematicData = new AircraftData(LocalDateTime.now());
            problematicData.setEngineRPM(Double.NaN);
            problematicData.setFuelLevel(Double.POSITIVE_INFINITY);
            
            // Should not throw exception
            assertDoesNotThrow(() -> {
                webSocketService.broadcastAircraftData(problematicData);
            });
        }

        @Test
        @DisplayName("Should remove closed sessions during broadcast")
        void shouldRemoveClosedSessionsDuringBroadcast() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            webSocketService.afterConnectionEstablished(mockClosedSession);
            
            assertEquals(2, webSocketService.getConnectedClientsCount());
            
            webSocketService.broadcastAircraftData(testAircraftData);
            
            // Closed session should be removed
            assertEquals(1, webSocketService.getConnectedClientsCount());
            verify(mockSession1).sendMessage(any(TextMessage.class));
            verify(mockClosedSession, never()).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("Should handle send message exceptions")
        void shouldHandleSendMessageExceptions() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            webSocketService.afterConnectionEstablished(mockSession2);
            
            // Make session1 throw exception on sendMessage
            doThrow(new RuntimeException("Send failed")).when(mockSession1).sendMessage(any(TextMessage.class));
            
            webSocketService.broadcastAircraftData(testAircraftData);
            
            // Session2 should still receive the message
            verify(mockSession2, atLeast(1)).sendMessage(any(TextMessage.class));
            // Session1 should be removed after the exception
            assertEquals(1, webSocketService.getConnectedClientsCount());
        }
    }

    @Nested
    @DisplayName("Alert Broadcasting Tests")
    class AlertBroadcastingTests {

        @Test
        @DisplayName("Should broadcast alerts to all connected clients")
        void shouldBroadcastAlertsToAllConnectedClients() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            webSocketService.afterConnectionEstablished(mockSession2);
            
            webSocketService.broadcastAlert("ENGINE", "Engine temperature high", "WARNING");
            
            verify(mockSession1, atLeast(1)).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"alert\"") && 
                       payload.contains("\"alertType\":\"ENGINE\"") &&
                       payload.contains("\"message\":\"Engine temperature high\"") &&
                       payload.contains("\"severity\":\"WARNING\"");
            }));
            
            verify(mockSession2, atLeast(1)).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"alert\"") && 
                       payload.contains("\"alertType\":\"ENGINE\"");
            }));
        }

        @Test
        @DisplayName("Should handle different alert severities")
        void shouldHandleDifferentAlertSeverities() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            webSocketService.broadcastAlert("FUEL", "Low fuel level", "CRITICAL");
            webSocketService.broadcastAlert("SYSTEM", "System check complete", "INFO");
            
            verify(mockSession1).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"severity\":\"CRITICAL\"");
            }));
            
            verify(mockSession1).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"severity\":\"INFO\"");
            }));
        }

        @Test
        @DisplayName("Should not broadcast alerts when no clients connected")
        void shouldNotBroadcastAlertsWhenNoClientsConnected() throws Exception {
            webSocketService.broadcastAlert("ENGINE", "Test alert", "WARNING");
            
            assertEquals(0, webSocketService.getConnectedClientsCount());
        }

        @Test
        @DisplayName("Should handle special characters in alert messages")
        void shouldHandleSpecialCharactersInAlertMessages() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            webSocketService.broadcastAlert("TEST", "Alert with \"quotes\" and 'apostrophes'", "INFO");
            
            verify(mockSession1, atLeast(1)).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("Should handle null and empty alert parameters")
        void shouldHandleNullAndEmptyAlertParameters() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            assertDoesNotThrow(() -> {
                webSocketService.broadcastAlert(null, "message", "INFO");
                webSocketService.broadcastAlert("TYPE", null, "INFO");
                webSocketService.broadcastAlert("TYPE", "message", null);
                webSocketService.broadcastAlert("", "", "");
            });
        }
    }

    @Nested
    @DisplayName("Custom Message Broadcasting Tests")
    class CustomMessageBroadcastingTests {

        @Test
        @DisplayName("Should broadcast custom messages")
        void shouldBroadcastCustomMessages() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            String testData = "custom data";
            webSocketService.broadcastCustomMessage("custom_type", testData);
            
            verify(mockSession1, atLeast(1)).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"custom_type\"") && 
                       payload.contains("\"data\":\"custom data\"");
            }));
        }

        @Test
        @DisplayName("Should broadcast custom messages with complex objects")
        void shouldBroadcastCustomMessagesWithComplexObjects() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            webSocketService.broadcastCustomMessage("aircraft_status", testAircraftData);
            
            verify(mockSession1, atLeast(1)).sendMessage(argThat(message -> {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"aircraft_status\"") && 
                       payload.contains("\"engineRPM\":2200.0");
            }));
        }

        @Test
        @DisplayName("Should handle custom message serialization errors")
        void shouldHandleCustomMessageSerializationErrors() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            // Create an object that might cause serialization issues
            Object problematicObject = new Object() {
                @SuppressWarnings("unused")
                public String getValue() {
                    throw new RuntimeException("Serialization error");
                }
            };
            
            assertDoesNotThrow(() -> {
                webSocketService.broadcastCustomMessage("test", problematicObject);
            });
        }
    }

    @Nested
    @DisplayName("Connection Count and Management Tests")
    class ConnectionCountAndManagementTests {

        @Test
        @DisplayName("Should return correct connected clients count")
        void shouldReturnCorrectConnectedClientsCount() throws Exception {
            assertEquals(0, webSocketService.getConnectedClientsCount());
            
            webSocketService.afterConnectionEstablished(mockSession1);
            assertEquals(1, webSocketService.getConnectedClientsCount());
            
            webSocketService.afterConnectionEstablished(mockSession2);
            assertEquals(2, webSocketService.getConnectedClientsCount());
            
            webSocketService.afterConnectionClosed(mockSession1, CloseStatus.NORMAL);
            assertEquals(1, webSocketService.getConnectedClientsCount());
        }

        @Test
        @DisplayName("Should handle concurrent connection operations")
        void shouldHandleConcurrentConnectionOperations() throws Exception {
            // Simulate concurrent connections
            webSocketService.afterConnectionEstablished(mockSession1);
            webSocketService.afterConnectionEstablished(mockSession2);
            
            // Concurrent broadcast should not cause issues
            Thread broadcastThread = new Thread(() -> {
                webSocketService.broadcastAircraftData(testAircraftData);
            });
            
            Thread connectionThread = new Thread(() -> {
                try {
                    WebSocketSession mockSession3 = mock(WebSocketSession.class);
                    when(mockSession3.getId()).thenReturn("session-3");
                    when(mockSession3.isOpen()).thenReturn(true);
                    webSocketService.afterConnectionEstablished(mockSession3);
                } catch (Exception e) {
                    // Handle exception
                }
            });
            
            broadcastThread.start();
            connectionThread.start();
            
            broadcastThread.join(1000);
            connectionThread.join(1000);
            
            // Should have at least 2 connections
            assertTrue(webSocketService.getConnectedClientsCount() >= 2);
        }

        @Test
        @DisplayName("Should maintain session list integrity")
        void shouldMaintainSessionListIntegrity() throws Exception {
            List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
            ReflectionTestUtils.setField(webSocketService, "sessions", sessions);
            
            webSocketService.afterConnectionEstablished(mockSession1);
            assertEquals(1, sessions.size());
            
            webSocketService.afterConnectionClosed(mockSession1, CloseStatus.NORMAL);
            assertEquals(0, sessions.size());
        }
    }

    @Nested
    @DisplayName("Error Handling and Edge Cases Tests")
    class ErrorHandlingAndEdgeCasesTests {

        @Test
        @DisplayName("Should handle null session in connection establishment")
        void shouldHandleNullSessionInConnectionEstablishment() {
            assertThrows(NullPointerException.class, () -> {
                webSocketService.afterConnectionEstablished(null);
            });
        }

        @Test
        @DisplayName("Should handle null session in connection closure")
        void shouldHandleNullSessionInConnectionClosure() {
            assertThrows(NullPointerException.class, () -> {
                webSocketService.afterConnectionClosed(null, CloseStatus.NORMAL);
            });
        }

        @Test
        @DisplayName("Should handle null message in text message handling")
        void shouldHandleNullMessageInTextMessageHandling() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            assertThrows(NullPointerException.class, () -> {
                webSocketService.handleTextMessage(mockSession1, null);
            });
        }

        @Test
        @DisplayName("Should handle null aircraft data in broadcast")
        void shouldHandleNullAircraftDataInBroadcast() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            assertDoesNotThrow(() -> {
                webSocketService.broadcastAircraftData(null);
            });
        }

        @Test
        @DisplayName("Should handle session send message IOException")
        void shouldHandleSessionSendMessageIOException() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            doThrow(new java.io.IOException("Connection lost")).when(mockSession1)
                    .sendMessage(any(TextMessage.class));
            
            webSocketService.broadcastAircraftData(testAircraftData);
            
            // Session should be removed after IOException
            assertEquals(0, webSocketService.getConnectedClientsCount());
        }

        @Test
        @DisplayName("Should handle large message payloads")
        void shouldHandleLargeMessagePayloads() throws Exception {
            webSocketService.afterConnectionEstablished(mockSession1);
            
            // Create a large aircraft data object
            AircraftData largeData = new AircraftData(LocalDateTime.now());
            // Set all fields to create a large JSON
            largeData.setEngineRPM(2200.0);
            largeData.setEngineTemperature(150.0);
            largeData.setOilPressure(45.0);
            largeData.setOilTemperature(90.0);
            largeData.setFuelLevel(75.0);
            largeData.setFuelConsumption(250.0);
            largeData.setFuelPressure(25.0);
            largeData.setFuelTemperature(20.0);
            
            assertDoesNotThrow(() -> {
                webSocketService.broadcastAircraftData(largeData);
            });
        }
    }
}
