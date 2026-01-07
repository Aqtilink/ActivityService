# Activity Service

A microservice that manages activities and sports events for the Aqtilink social sports platform. Built with Spring Boot 3.4, it provides RESTful APIs for creating, managing, and sharing activities with friends.

## Overview

The Activity Service is a core component of the Aqtilink ecosystem, handling all activity-related operations including:
- Activity creation and management
- User participation tracking
- Friends' activity feeds
- Activity discovery and filtering
- Event notifications

## Architecture

### Technology Stack

- **Framework**: Spring Boot 3.4.12
- **Java Version**: 17
- **Database**: PostgreSQL
- **Message Queue**: RabbitMQ (AMQP)
- **Security**: OAuth2 with JWT (Clerk authentication)
- **ORM**: Hibernate/JPA

### Project Structure

```
activity_service/
├── src/main/java/com/aqtilink/activity_service/
│   ├── ActivityServiceApplication.java    # Main application class
│   ├── client/                            # External service clients
│   ├── config/                            # Configuration classes
│   ├── controller/                        # REST API endpoints
│   ├── dto/                               # Data Transfer Objects
│   ├── exception/                         # Custom exceptions
│   ├── messaging/                         # Message publishing
│   ├── model/                             # JPA entities
│   ├── repository/                        # Data access layer
│   ├── security/                          # Security configuration
│   └── service/                           # Business logic
└── src/main/resources/
    ├── application.yml                    # Application configuration
    └── db/                                # Database scripts
```

## API Endpoints

### Activity Management

#### Create Activity
```
POST /api/v1/activities/json
Content-Type: application/json

Request Body:
{
  "ownerId": "user-id",
  "title": "Morning Run",
  "sportType": "RUNNING",
  "startTime": "2025-01-10T08:00:00",
  "location": "Central Park",
  "participants": ["user-id"]
}

Response: ActivityResponseDTO
Status Code: 201 Created
```

#### Join Activity
```
POST /api/v1/activities/{activityId}/join/{userId}

Status Code: 200 OK
Exceptions: ActivityAlreadyStartedException (409 Conflict) if activity has started
```

#### Delete Activity
```
DELETE /api/v1/activities/{activityId}

Status Code: 204 No Content
```

### Activity Retrieval

#### Get User's Activities
```
GET /api/v1/activities/user/{userId}

Response: List<ActivityResponseDTO>
Status Code: 200 OK
```

#### Get Friends' Feed
```
GET /api/v1/activities/friends-feed/{userId}

Response: List<ActivityResponseDTO>
Status Code: 200 OK
```

#### Get Joined Activities
```
GET /api/v1/activities/joined/{userId}

Response: List<ActivityResponseDTO>
Status Code: 200 OK
```

#### Get All Activities
```
GET /api/v1/activities/all

Response: List<ActivityResponseDTO>
Status Code: 200 OK
```

## Data Models

### Activity Entity

```java
{
  "id": "UUID",
  "ownerId": "string",
  "title": "string",
  "sportType": "RUNNING|CYCLING|SWIMMING|HIKING|CLIMBING|YOGA|...",
  "startTime": "LocalDateTime",
  "location": "string",
  "gpxPath": "string (optional)",
  "createdAt": "LocalDateTime",
  "participants": ["user-id-1", "user-id-2"],
  "ownerName": "string (transient)"
}
```

### ActivityResponseDTO

Extends Activity data with:
- `owner`: UserSummaryDTO - Owner profile information
- `participants`: List<UserSummaryDTO> - Participant profiles

### UserSummaryDTO

```java
{
  "id": "string",
  "name": "string",
  "email": "string"
}
```

## Key Features

### Activity Notifications
- When a user creates an activity, all their friends receive email notifications
- Powered by RabbitMQ messaging through `NotificationPublisherActivity`
- Integrates with the Notification Service

### User Service Integration
- Fetches friend lists via `UserServiceClient`
- Retrieves user profile information
- Validates user relationships

### Sport Type Support
The service supports various sport types via the `SportType` enum:
- RUNNING, CYCLING, SWIMMING, HIKING, CLIMBING, YOGA, and others

### Activity Validation
- Activities cannot be joined after they have started
- Prevents duplicate participant entries
- Automatic creation timestamp on activity save

## Configuration

### Environment Variables

```yaml
# Database Configuration
spring.datasource.url: jdbc:postgresql://host:5432/activity_service_db
spring.datasource.username: postgres
spring.datasource.password: postgres

# JWT/OAuth2
JWK_SET_URI: https://clerk.aqtilink.live/.well-known/jwks.json

# CORS
CORS_ALLOWED_ORIGINS: http://localhost:5173,http://localhost:3000,https://aqtilink.live

# Service Communication
SERVICE_API_KEY: <api-key>
USER_SERVICE_URL: http://localhost:8080

# Server
server.port: 8081
spring.application.name: activity-service
```

### Application Properties

- **Port**: 8081
- **Database Migration**: Automatic (Hibernate DDL-auto: update)
- **Actuator Endpoints**: /health, /info
- **JWT Validation**: Validates tokens from Clerk OAuth2 provider

## Dependencies

### Core Framework
- `spring-boot-starter-web` - REST API support
- `spring-boot-starter-data-jpa` - Database ORM
- `spring-boot-starter-security` - Security framework
- `spring-boot-starter-oauth2-resource-server` - JWT validation

### Data & Validation
- `spring-boot-starter-validation` - Bean validation
- `postgresql` - Database driver

### Communication
- `spring-boot-starter-amqp` - RabbitMQ support
- `nimbus-jose-jwt` - JWT handling

### Development
- `spring-boot-devtools` - Live reload support
- `spring-boot-starter-test` - Testing framework

## Security

### Authentication
- OAuth2 resource server configuration
- JWT token validation against Clerk's JWKS endpoint
- Bearer token required for protected endpoints

### CORS
Configurable allowed origins supporting:
- Local development (localhost:5173, localhost:3000)
- Production (aqtilink.live)

## Running the Service

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Docker and Docker Compose (for containerized deployment)

### Local Development

1. **Install dependencies** (Maven):
   ```bash
   ./mvnw clean install
   ```

2. **Configure environment**:
   ```bash
   export JWK_SET_URI=https://clerk.aqtilink.live/.well-known/jwks.json
   export CORS_ALLOWED_ORIGINS=http://localhost:5173
   ```

3. **Run the service**:
   ```bash
   ./mvnw spring-boot:run
   ```

   Service will start at `http://localhost:8081`

### Docker Deployment

```bash
docker build -t activity-service:latest .
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/activity_service_db \
  -e JWK_SET_URI=https://clerk.aqtilink.live/.well-known/jwks.json \
  activity-service:latest
```

## Database Schema

### Tables

**activities**
- id (UUID, Primary Key)
- ownerId (String, Not Null)
- title (String, Not Null)
- sportType (String, Not Null)
- startTime (Timestamp, Not Null)
- location (String)
- gpxPath (String)
- createdAt (Timestamp)

**activity_participants**
- activity_id (UUID, Foreign Key)
- user_id (String)

## Integration Points

### User Service
- **Endpoint**: USER_SERVICE_URL
- **Purpose**: Fetch user profiles and friend lists
- **Client**: `UserServiceClient`

### Notification Service
- **Protocol**: RabbitMQ/AMQP
- **Events**: Activity creation notifications
- **Publisher**: `NotificationPublisherActivity`

## Additional Endpoints

### User Data Management

#### Delete Activities by Owner
```
DELETE /api/v1/activities/user/{userId}

Status Code: 204 No Content
Description: Deletes all activities owned by the specified user
```

#### Remove User from All Activities
```
DELETE /api/v1/activities/participants/{userId}

Status Code: 204 No Content
Description: Removes the user from all activities they are participating in
```

## Error Handling

### Custom Exceptions

- **ActivityAlreadyStartedException**: Thrown when attempting to join an activity that has already started (409 Conflict)

### HTTP Status Codes

#### Success Codes
- `200 OK` - Successful GET or JOIN operations
- `201 Created` - Activity successfully created
- `204 No Content` - Successful DELETE operations

#### Error Codes
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Missing/invalid JWT token
- `404 Not Found` - Activity or resource not found
- `409 Conflict` - Activity already started (cannot join)
- `500 Internal Server Error` - Server-side error

## Development Guidelines

### Adding New Endpoints

1. Create controller method in `ActivityController`
2. Add business logic to `ActivityService`
3. Create/update DTOs in `dto/` package
4. Update repository if new queries needed

### Database Changes

1. Modify JPA entities in `model/`
2. Let Hibernate auto-generate schema changes (DDL-auto: update)
3. For complex migrations, add scripts to `src/main/resources/db/`

### Testing

Run tests with:
```bash
./mvnw test
```

## References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/3.4.x/reference/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)

## Support

For issues or questions about the Activity Service, refer to the project's issue tracker or contact the development team.
