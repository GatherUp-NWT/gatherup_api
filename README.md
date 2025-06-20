# GatherUp - Event Management Platform

## Project Overview
GatherUp is a microservices-based event management platform that allows users to create, manage, and participate in events. The application provides features for user authentication, event creation, invitations, payments, reviews, and more.

## Architecture
GatherUp follows a microservices architecture pattern, with the following components:

### Infrastructure Services
- **Config Server**: Centralized configuration management for all services
- **Eureka Server**: Service discovery and registration
- **API Gateway**: Entry point for client requests, handles routing to appropriate services

### Message Broker
- **RabbitMQ**: Facilitates asynchronous communication between services

### Business Services
- **Auth Service**: Handles user authentication, authorization, and user management
- **Event Service**: Manages event creation, updates, and queries
- **Invitation Service**: Handles sending and managing invitations to events
- **Payment Service**: Processes payments for paid events
- **Registration Service**: Manages user registrations for events
- **Review Service**: Allows users to review events they've attended
- **System Events Service**: Logs system events for auditing and monitoring

## Technology Stack
- **Java 21**: Programming language
- **Spring Boot 3.4.3**: Application framework
- **Spring Cloud**: Microservices infrastructure
- **Spring Data JPA**: Database access
- **PostgreSQL**: Relational database
- **Docker**: Containerization
- **RabbitMQ**: Message broker
- **JWT**: Authentication mechanism

## Setup and Installation

### Prerequisites
- Java 21
- Docker and Docker Compose
- Maven

### Steps to Run
1. Clone the repository:
   ```
   git clone <repository-url>
   cd gatherup_api
   ```

2. Configure GitHub credentials for Config Server:
   Edit the `docker-compose.yml` file and update the following environment variables:
   ```yaml
   config-server:
     environment:
       - GITHUB_USERNAME=your_github_username
       - GITHUB_TOKEN=your_github_token
   ```

3. Build the project:
   ```
   mvn clean package
   ```

4. Start the application using Docker Compose:
   ```
   docker-compose up -d
   ```

5. The application will be accessible at:
   - API Gateway: http://localhost:8080
   - Eureka Server: http://localhost:8761
   - RabbitMQ Management UI: http://localhost:15672 (username: guest, password: guest)

## Service Endpoints

### Auth Service
- **Login**: POST /auth/login
- **Register**: POST /auth/register
- **Validate Token**: POST /auth/validate
- **Refresh Token**: POST /auth/refresh
- **Logout**: POST /auth/logout

### User Management
- **Get User**: GET /users/{id}
- **Update User**: PUT /users/{id}
- **Delete User**: DELETE /users/{id}

### Event Service
- **Create Event**: POST /events
- **Get Event**: GET /events/{id}
- **Update Event**: PUT /events/{id}
- **Delete Event**: DELETE /events/{id}
- **List Events**: GET /events

### Other services have similar RESTful endpoints following standard patterns.

## Security
The application uses JWT (JSON Web Tokens) for authentication. When a user logs in, they receive an access token and a refresh token. The access token is used for API requests, while the refresh token (stored as an HTTP-only cookie) is used to obtain a new access token when the current one expires.

Role-based access control is implemented with different permissions for regular users and administrators.

## Development
Each service can be developed and run independently. Use the following steps for local development:

1. Start infrastructure services (Config Server, Eureka Server, RabbitMQ) using Docker Compose:
   ```
   docker-compose up -d config-server eureka-server rabbitmq
   ```

2. Run the specific service you're working on locally:
   ```
   cd <service-directory>
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## Troubleshooting
- If services fail to register with Eureka, ensure the Config Server is running and accessible.
- Check logs for each service using `docker-compose logs <service-name>`.
- Ensure all required environment variables are properly set in the docker-compose.yml file.

## Contributing
Please follow standard Git workflow:
1. Create a feature branch
2. Make your changes
3. Submit a pull request

Ensure all tests pass before submitting your changes.

## Link on Video

[https://drive.google.com/file/d/1PUOjYcPmjoxxzx6Vsr5fveo1vjaFRSlL/view?usp=sharing](https://drive.google.com/file/d/14gheywHmmz5prdo8sHEIenmUp0qxFHSq/view?usp=sharing)
