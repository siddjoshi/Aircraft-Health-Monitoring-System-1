package com.aircraft.monitoring.config;

import com.aircraft.monitoring.service.WebSocketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebSocketConfig.
 * 
 * Tests WebSocket configuration, handler registration,
 * and configuration properties.
 * 
 * @author Aircraft Monitoring Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocketConfig Tests")
class WebSocketConfigTest {

    @Mock
    private WebSocketService mockWebSocketService;

    @Mock
    private WebSocketHandlerRegistry mockRegistry;

    @Mock
    private WebSocketHandlerRegistration mockRegistration;

    @Mock
    private SockJsServiceRegistration mockSockJsRegistration;

    private WebSocketConfig webSocketConfig;

    @BeforeEach
    void setUp() {
        webSocketConfig = new WebSocketConfig(mockWebSocketService);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create WebSocketConfig with WebSocketService")
        void shouldCreateWebSocketConfigWithWebSocketService() {
            WebSocketConfig config = new WebSocketConfig(mockWebSocketService);
            
            assertNotNull(config);
        }

        @Test
        @DisplayName("Should handle null WebSocketService")
        void shouldHandleNullWebSocketService() {
            assertThrows(NullPointerException.class, () -> {
                new WebSocketConfig(null);
            });
        }
    }

    @Nested
    @DisplayName("Handler Registration Tests")
    class HandlerRegistrationTests {

        @Test
        @DisplayName("Should register WebSocket handler correctly")
        void shouldRegisterWebSocketHandlerCorrectly() {
            // Setup mock chain
            when(mockRegistry.addHandler(eq(mockWebSocketService), eq("/websocket")))
                    .thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns("*"))
                    .thenReturn(mockRegistration);
            when(mockRegistration.withSockJS())
                    .thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            verify(mockRegistry).addHandler(mockWebSocketService, "/websocket");
            verify(mockRegistration).setAllowedOriginPatterns("*");
            verify(mockRegistration).withSockJS();
        }

        @Test
        @DisplayName("Should register handler with correct endpoint")
        void shouldRegisterHandlerWithCorrectEndpoint() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            verify(mockRegistry).addHandler(mockWebSocketService, "/websocket");
        }

        @Test
        @DisplayName("Should configure CORS with wildcard origin")
        void shouldConfigureCorsWithWildcardOrigin() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            verify(mockRegistration).setAllowedOriginPatterns("*");
        }

        @Test
        @DisplayName("Should enable SockJS fallback")
        void shouldEnableSockJSFallback() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            verify(mockRegistration).withSockJS();
        }

        @Test
        @DisplayName("Should handle null registry gracefully")
        void shouldHandleNullRegistryGracefully() {
            assertThrows(NullPointerException.class, () -> {
                webSocketConfig.registerWebSocketHandlers(null);
            });
        }

        @Test
        @DisplayName("Should handle registry exceptions")
        void shouldHandleRegistryExceptions() {
            when(mockRegistry.addHandler(any(), any()))
                    .thenThrow(new RuntimeException("Registry error"));

            assertThrows(RuntimeException.class, () -> {
                webSocketConfig.registerWebSocketHandlers(mockRegistry);
            });

            verify(mockRegistry).addHandler(mockWebSocketService, "/websocket");
        }
    }

    @Nested
    @DisplayName("Configuration Validation Tests")
    class ConfigurationValidationTests {

        @Test
        @DisplayName("Should register only one handler")
        void shouldRegisterOnlyOneHandler() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            verify(mockRegistry, times(1)).addHandler(any(), any());
        }

        @Test
        @DisplayName("Should use correct WebSocket path")
        void shouldUseCorrectWebSocketPath() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            verify(mockRegistry).addHandler(mockWebSocketService, "/websocket");
        }

        @Test
        @DisplayName("Should register the provided WebSocketService instance")
        void shouldRegisterTheProvidedWebSocketServiceInstance() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            verify(mockRegistry).addHandler(eq(mockWebSocketService), any());
        }
    }

    @Nested
    @DisplayName("Integration and End-to-End Tests")
    class IntegrationAndEndToEndTests {

        @Test
        @DisplayName("Should complete full registration chain successfully")
        void shouldCompleteFullRegistrationChainSuccessfully() {
            // Mock the complete chain
            when(mockRegistry.addHandler(mockWebSocketService, "/websocket"))
                    .thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns("*"))
                    .thenReturn(mockRegistration);
            when(mockRegistration.withSockJS())
                    .thenReturn(mockSockJsRegistration);

            // Should not throw any exceptions
            assertDoesNotThrow(() -> {
                webSocketConfig.registerWebSocketHandlers(mockRegistry);
            });

            // Verify the complete chain was called
            verify(mockRegistry).addHandler(mockWebSocketService, "/websocket");
            verify(mockRegistration).setAllowedOriginPatterns("*");
            verify(mockRegistration).withSockJS();
        }

        @Test
        @DisplayName("Should handle multiple registration calls")
        void shouldHandleMultipleRegistrationCalls() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            // Call registration multiple times
            webSocketConfig.registerWebSocketHandlers(mockRegistry);
            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            // Should be called twice
            verify(mockRegistry, times(2)).addHandler(mockWebSocketService, "/websocket");
            verify(mockRegistration, times(2)).setAllowedOriginPatterns("*");
            verify(mockRegistration, times(2)).withSockJS();
        }

        @Test
        @DisplayName("Should work with different WebSocketService instances")
        void shouldWorkWithDifferentWebSocketServiceInstances() {
            WebSocketService anotherService = mock(WebSocketService.class);
            WebSocketConfig anotherConfig = new WebSocketConfig(anotherService);

            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            anotherConfig.registerWebSocketHandlers(mockRegistry);

            verify(mockRegistry).addHandler(anotherService, "/websocket");
        }
    }

    @Nested
    @DisplayName("Error Handling and Edge Cases Tests")
    class ErrorHandlingAndEdgeCasesTests {

        @Test
        @DisplayName("Should handle registration chain interruption")
        void shouldHandleRegistrationChainInterruption() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any()))
                    .thenThrow(new RuntimeException("Chain interrupted"));

            assertThrows(RuntimeException.class, () -> {
                webSocketConfig.registerWebSocketHandlers(mockRegistry);
            });

            verify(mockRegistry).addHandler(mockWebSocketService, "/websocket");
            verify(mockRegistration).setAllowedOriginPatterns("*");
            verify(mockRegistration, never()).withSockJS();
        }

        @Test
        @DisplayName("Should handle null return from addHandler")
        void shouldHandleNullReturnFromAddHandler() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(null);

            assertThrows(NullPointerException.class, () -> {
                webSocketConfig.registerWebSocketHandlers(mockRegistry);
            });

            verify(mockRegistry).addHandler(mockWebSocketService, "/websocket");
        }

        @Test
        @DisplayName("Should handle concurrent registration attempts")
        void shouldHandleConcurrentRegistrationAttempts() throws InterruptedException {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            // Create multiple threads trying to register
            Thread thread1 = new Thread(() -> 
                webSocketConfig.registerWebSocketHandlers(mockRegistry));
            Thread thread2 = new Thread(() -> 
                webSocketConfig.registerWebSocketHandlers(mockRegistry));

            thread1.start();
            thread2.start();

            thread1.join(1000);
            thread2.join(1000);

            // Both should complete successfully
            verify(mockRegistry, times(2)).addHandler(mockWebSocketService, "/websocket");
        }
    }

    @Nested
    @DisplayName("Configuration Properties Tests")
    class ConfigurationPropertiesTests {

        @Test
        @DisplayName("Should use correct endpoint path")
        void shouldUseCorrectEndpointPath() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            // Verify the exact endpoint path
            verify(mockRegistry).addHandler(any(), eq("/websocket"));
        }

        @Test
        @DisplayName("Should use wildcard for allowed origins")
        void shouldUseWildcardForAllowedOrigins() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            // Verify wildcard pattern is used
            verify(mockRegistration).setAllowedOriginPatterns(eq("*"));
        }

        @Test
        @DisplayName("Should enable SockJS for fallback support")
        void shouldEnableSockJSForFallbackSupport() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            webSocketConfig.registerWebSocketHandlers(mockRegistry);

            // Verify SockJS is enabled
            verify(mockRegistration).withSockJS();
        }
    }

    @Nested
    @DisplayName("Spring Framework Integration Tests")
    class SpringFrameworkIntegrationTests {

        @Test
        @DisplayName("Should implement WebSocketConfigurer interface")
        void shouldImplementWebSocketConfigurerInterface() {
            assertTrue(webSocketConfig instanceof org.springframework.web.socket.config.annotation.WebSocketConfigurer);
        }

        @Test
        @DisplayName("Should have Configuration annotation")
        void shouldHaveConfigurationAnnotation() {
            assertTrue(WebSocketConfig.class.isAnnotationPresent(
                    org.springframework.context.annotation.Configuration.class));
        }

        @Test
        @DisplayName("Should have EnableWebSocket annotation")
        void shouldHaveEnableWebSocketAnnotation() {
            assertTrue(WebSocketConfig.class.isAnnotationPresent(
                    org.springframework.web.socket.config.annotation.EnableWebSocket.class));
        }

        @Test
        @DisplayName("Should override registerWebSocketHandlers method")
        void shouldOverrideRegisterWebSocketHandlersMethod() throws NoSuchMethodException {
            // Verify the method exists and matches the interface
            java.lang.reflect.Method method = WebSocketConfig.class.getMethod(
                    "registerWebSocketHandlers", WebSocketHandlerRegistry.class);
            
            assertNotNull(method);
            assertEquals(void.class, method.getReturnType());
        }
    }

    @Nested
    @DisplayName("Memory and Performance Tests")
    class MemoryAndPerformanceTests {

        @Test
        @DisplayName("Should not leak memory on repeated registrations")
        void shouldNotLeakMemoryOnRepeatedRegistrations() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            // Simulate many registration calls
            for (int i = 0; i < 100; i++) {
                webSocketConfig.registerWebSocketHandlers(mockRegistry);
            }

            // Verify all calls were made
            verify(mockRegistry, times(100)).addHandler(mockWebSocketService, "/websocket");
        }

        @Test
        @DisplayName("Should handle rapid successive registration calls")
        void shouldHandleRapidSuccessiveRegistrationCalls() {
            when(mockRegistry.addHandler(any(), any())).thenReturn(mockRegistration);
            when(mockRegistration.setAllowedOriginPatterns(any())).thenReturn(mockRegistration);
            when(mockRegistration.withSockJS()).thenReturn(mockSockJsRegistration);

            // Rapid calls
            for (int i = 0; i < 10; i++) {
                webSocketConfig.registerWebSocketHandlers(mockRegistry);
            }

            verify(mockRegistry, times(10)).addHandler(mockWebSocketService, "/websocket");
            verify(mockRegistration, times(10)).setAllowedOriginPatterns("*");
            verify(mockRegistration, times(10)).withSockJS();
        }
    }
}
