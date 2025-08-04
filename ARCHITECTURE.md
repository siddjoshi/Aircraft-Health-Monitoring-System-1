# Aircraft Health Monitoring System - Architecture Documentation

## Overview

The Aircraft Health Monitoring System is a real-time monitoring solution designed to track critical aircraft systems and detect anomalies. The system follows a modern microservices architecture with a clear separation between frontend and backend components.

## High-Level Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        WB[Web Browser]
        MD[Mobile Device]
    end
    
    subgraph "Frontend Layer"
        RA[React Application]
        TW[Tailwind CSS]
        WS[WebSocket Client]
        HTTP[HTTP Client]
    end
    
    subgraph "Backend Layer"
        SB[Spring Boot Application]
        WC[WebSocket Controller]
        RC[REST Controller]
        WS_SERVICE[WebSocket Service]
    end
    
    subgraph "Business Logic Layer"
        DS[Data Simulation Service]
        AD[Anomaly Detection Service]
        AS[Alert Service]
    end
    
    subgraph "Data Layer"
        MEM[In-Memory Data Store]
        LOG[Logging System]
    end
    
    subgraph "External Systems"
        CSV[Sample Data Files]
        MON[System Monitoring]
    end
    
    %% Client connections
    WB --> RA
    MD --> RA
    
    %% Frontend to Backend
    RA --> HTTP
    RA --> WS
    HTTP --> RC
    WS --> WC
    
    %% Backend internal
    RC --> DS
    RC --> WS_SERVICE
    WC --> WS_SERVICE
    WS_SERVICE --> AD
    DS --> AD
    AD --> AS
    
    %% Data flow
    DS --> MEM
    AD --> LOG
    AS --> LOG
    CSV --> DS
    
    %% Monitoring
    SB --> MON
    
    %% Styling
    classDef frontend fill:#e1f5fe
    classDef backend fill:#f3e5f5
    classDef data fill:#e8f5e8
    classDef external fill:#fff3e0
    
    class RA,TW,WS,HTTP frontend
    class SB,WC,RC,WS_SERVICE,DS,AD,AS backend
    class MEM,LOG data
    class CSV,MON external
```

## System Components

### Frontend Layer (React/TypeScript)

```mermaid
graph LR
    subgraph "React Application"
        APP[App.js]
        DASH[Dashboard.js]
        
        subgraph "System Components"
            ENG[EngineSystem.js]
            FUEL[FuelSystem.js]
            HYD[HydraulicSystem.js]
            FLIGHT[FlightData.js]
        end
        
        subgraph "UI Components"
            STATUS[SystemStatus.js]
            ALERT[AlertPanel.js]
            CONTROLS[AnomalyControls.js]
        end
        
        subgraph "Services"
            WS_SRV[WebSocketService.js]
        end
        
        subgraph "Styling"
            CSS[App.css]
            TAILWIND[Tailwind Config]
        end
    end
    
    APP --> DASH
    DASH --> ENG
    DASH --> FUEL
    DASH --> HYD
    DASH --> FLIGHT
    DASH --> STATUS
    APP --> ALERT
    DASH --> CONTROLS
    APP --> WS_SRV
    APP --> CSS
    CSS --> TAILWIND
    
    classDef component fill:#bbdefb
    classDef service fill:#c8e6c9
    classDef style fill:#fff9c4
    
    class ENG,FUEL,HYD,FLIGHT,STATUS,ALERT,CONTROLS component
    class WS_SRV service
    class CSS,TAILWIND style
```

### Backend Layer (Spring Boot/Java)

```mermaid
graph TB
    subgraph "Spring Boot Application"
        MAIN[AircraftMonitoringApplication.java]
        
        subgraph "Configuration"
            WS_CONFIG[WebSocketConfig.java]
            APP_PROPS[application.properties]
        end
        
        subgraph "Controllers"
            REST_CTRL[AircraftController.java]
        end
        
        subgraph "Services"
            DATA_SIM[DataSimulationService.java]
            ANOMALY[AnomalyDetectionService.java]
            WS_SRV[WebSocketService.java]
        end
        
        subgraph "Models"
            AIRCRAFT_DATA[AircraftData.java]
        end
        
        subgraph "Dependencies"
            SPRING_WEB[Spring Web]
            SPRING_WS[Spring WebSocket]
            JACKSON[Jackson JSON]
            LOMBOK[Lombok]
        end
    end
    
    MAIN --> WS_CONFIG
    MAIN --> REST_CTRL
    REST_CTRL --> DATA_SIM
    REST_CTRL --> WS_SRV
    WS_CONFIG --> WS_SRV
    DATA_SIM --> ANOMALY
    DATA_SIM --> AIRCRAFT_DATA
    WS_SRV --> AIRCRAFT_DATA
    
    classDef config fill:#ffecb3
    classDef controller fill:#e1bee7
    classDef service fill:#c8e6c9
    classDef model fill:#ffcdd2
    classDef dependency fill:#f0f4c3
    
    class WS_CONFIG,APP_PROPS config
    class REST_CTRL controller
    class DATA_SIM,ANOMALY,WS_SRV service
    class AIRCRAFT_DATA model
    class SPRING_WEB,SPRING_WS,JACKSON,LOMBOK dependency
```

## Data Flow Architecture

```mermaid
sequenceDiagram
    participant Browser
    participant React
    participant WebSocket
    participant REST_API
    participant DataService
    participant AnomalyDetection
    participant AlertSystem
    
    Note over Browser,AlertSystem: System Initialization
    Browser->>React: Load Application
    React->>WebSocket: Establish Connection
    React->>REST_API: Fallback Connection
    
    Note over Browser,AlertSystem: Real-time Data Flow
    loop Every 2 seconds
        DataService->>AnomalyDetection: Generate Aircraft Data
        AnomalyDetection->>DataService: Processed Data + Anomalies
        DataService->>WebSocket: Broadcast Data
        WebSocket->>React: Real-time Updates
        React->>Browser: Update UI
    end
    
    Note over Browser,AlertSystem: Anomaly Detection & Alerts
    AnomalyDetection->>AlertSystem: Anomaly Detected
    AlertSystem->>WebSocket: Broadcast Alert
    WebSocket->>React: Alert Notification
    React->>Browser: Display Alert
    
    Note over Browser,AlertSystem: User Interactions
    Browser->>React: Trigger Anomaly Simulation
    React->>REST_API: POST /simulate/*-anomaly
    REST_API->>DataService: Simulate Anomaly
    REST_API->>AlertSystem: Send Alert
    AlertSystem->>WebSocket: Broadcast Alert
```

## Communication Protocols

### WebSocket Communication

```mermaid
graph LR
    subgraph "Frontend"
        WSC[WebSocket Client<br/>SockJS]
    end
    
    subgraph "Backend"
        WSH[WebSocket Handler<br/>Spring WebSocket]
    end
    
    subgraph "Message Types"
        DATA[aircraft_data]
        ALERT[alert]
        CONN[connection]
        ECHO[echo]
    end
    
    WSC <-->|Real-time| WSH
    WSH --> DATA
    WSH --> ALERT
    WSH --> CONN
    WSH --> ECHO
    
    classDef frontend fill:#e3f2fd
    classDef backend fill:#f3e5f5
    classDef message fill:#e8f5e8
    
    class WSC frontend
    class WSH backend
    class DATA,ALERT,CONN,ECHO message
```

### REST API Endpoints

```mermaid
graph TB
    subgraph "API Endpoints"
        GET_DATA[GET /api/aircraft/data<br/>Get current aircraft data]
        GET_STATUS[GET /api/aircraft/status<br/>Get system status]
        GET_HEALTH[GET /api/aircraft/health<br/>Health check]
        
        POST_ENGINE[POST /api/aircraft/simulate/engine-anomaly<br/>Trigger engine anomaly]
        POST_FUEL[POST /api/aircraft/simulate/fuel-anomaly<br/>Trigger fuel anomaly]
        POST_HYDRAULIC[POST /api/aircraft/simulate/hydraulic-anomaly<br/>Trigger hydraulic anomaly]
        POST_ALERT[POST /api/aircraft/alert<br/>Send custom alert]
    end
    
    subgraph "Response Types"
        JSON_DATA[AircraftData JSON]
        JSON_STATUS[Status Object]
        JSON_SUCCESS[Success Response]
    end
    
    GET_DATA --> JSON_DATA
    GET_STATUS --> JSON_STATUS
    GET_HEALTH --> JSON_STATUS
    POST_ENGINE --> JSON_SUCCESS
    POST_FUEL --> JSON_SUCCESS
    POST_HYDRAULIC --> JSON_SUCCESS
    POST_ALERT --> JSON_SUCCESS
    
    classDef get fill:#e8f5e8
    classDef post fill:#fff3e0
    classDef response fill:#e1f5fe
    
    class GET_DATA,GET_STATUS,GET_HEALTH get
    class POST_ENGINE,POST_FUEL,POST_HYDRAULIC,POST_ALERT post
    class JSON_DATA,JSON_STATUS,JSON_SUCCESS response
```

## Aircraft Systems Monitoring

```mermaid
graph TB
    subgraph "Aircraft Systems"
        ENGINE[Engine System<br/>• RPM<br/>• Temperature<br/>• Oil Pressure<br/>• Oil Temperature]
        
        FUEL[Fuel System<br/>• Fuel Level<br/>• Consumption Rate<br/>• Fuel Pressure<br/>• Fuel Temperature]
        
        HYDRAULIC[Hydraulic System<br/>• Hydraulic Pressure<br/>• Hydraulic Temperature<br/>• Fluid Level]
        
        FLIGHT[Flight Data<br/>• Altitude<br/>• Airspeed<br/>• Ground Speed<br/>• Mach Number<br/>• Vertical Speed]
        
        CABIN[Cabin Systems<br/>• Cabin Pressure<br/>• Cabin Temperature]
        
        ELECTRICAL[Electrical Systems<br/>• Battery Voltage<br/>• Generator Output]
    end
    
    subgraph "Monitoring Components"
        SENSORS[Sensor Data Simulation]
        ANOMALY_DET[Anomaly Detection]
        ALERTS[Alert System]
        DISPLAY[Real-time Display]
    end
    
    ENGINE --> SENSORS
    FUEL --> SENSORS
    HYDRAULIC --> SENSORS
    FLIGHT --> SENSORS
    CABIN --> SENSORS
    ELECTRICAL --> SENSORS
    
    SENSORS --> ANOMALY_DET
    ANOMALY_DET --> ALERTS
    ALERTS --> DISPLAY
    
    classDef system fill:#bbdefb
    classDef monitoring fill:#c8e6c9
    
    class ENGINE,FUEL,HYDRAULIC,FLIGHT,CABIN,ELECTRICAL system
    class SENSORS,ANOMALY_DET,ALERTS,DISPLAY monitoring
```

## Technology Stack

### Frontend Technologies
- **React 18.2.0**: Component-based UI library
- **Tailwind CSS 3.3.0**: Utility-first CSS framework
- **SockJS Client 1.6.1**: WebSocket communication with fallback
- **Lucide React**: Modern icon library
- **Recharts 2.7.2**: Data visualization (if needed)

### Backend Technologies
- **Spring Boot 3.2.0**: Application framework
- **Spring WebSocket**: Real-time communication
- **Jackson**: JSON processing
- **Lombok**: Code generation for reducing boilerplate
- **OpenCSV 5.8**: CSV file processing
- **Java 17**: Programming language

### Development & Build Tools
- **Maven**: Backend dependency management and build
- **npm**: Frontend package management
- **PostCSS**: CSS processing
- **Autoprefixer**: CSS vendor prefixing

## Deployment Architecture

```mermaid
graph TB
    subgraph "Development Environment"
        DEV_FE[Frontend Dev Server<br/>localhost:3000]
        DEV_BE[Backend Dev Server<br/>localhost:8080]
    end
    
    subgraph "Build Artifacts"
        FE_BUILD[Frontend Build<br/>Static Files]
        BE_JAR[Backend JAR<br/>monitoring-1.0.0.jar]
    end
    
    subgraph "Production Deployment"
        WEB_SERVER[Web Server<br/>Nginx/Apache]
        APP_SERVER[Application Server<br/>Spring Boot Embedded]
    end
    
    DEV_FE --> FE_BUILD
    DEV_BE --> BE_JAR
    FE_BUILD --> WEB_SERVER
    BE_JAR --> APP_SERVER
    
    WEB_SERVER <--> APP_SERVER
    
    classDef dev fill:#e8f5e8
    classDef build fill:#fff3e0
    classDef prod fill:#ffebee
    
    class DEV_FE,DEV_BE dev
    class FE_BUILD,BE_JAR build
    class WEB_SERVER,APP_SERVER prod
```

## Security Considerations

- **CORS Configuration**: Configured for development (allow all origins)
- **WebSocket Security**: SockJS with origin validation
- **Input Validation**: Server-side validation for API endpoints
- **Error Handling**: Comprehensive error handling and logging

## Scalability Features

- **Stateless Backend**: RESTful API design for horizontal scaling
- **WebSocket Broadcasting**: Efficient message distribution to multiple clients
- **In-Memory Data**: Fast data access for real-time requirements
- **Modular Architecture**: Easy to extend with new aircraft systems

## Monitoring & Observability

- **Application Logging**: Comprehensive logging with different levels
- **Health Endpoints**: Built-in health check endpoints
- **Connection Monitoring**: Real-time connection status tracking
- **Alert System**: Configurable alert notifications

## Future Enhancements

1. **Database Integration**: Persistent data storage for historical analysis
2. **Authentication & Authorization**: User management and role-based access
3. **Advanced Analytics**: Machine learning for predictive maintenance
4. **Mobile Application**: Native mobile app for field technicians
5. **External System Integration**: Integration with actual aircraft systems
6. **Data Export**: Export capabilities for compliance and reporting

This architecture provides a solid foundation for real-time aircraft health monitoring with room for future enhancements and scaling.
