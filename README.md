# ILO Community Energy Allocation

A sophisticated energy management system that optimizes renewable energy distribution within a community.

## Features

- Dynamic energy allocation based on availability and user preferences
- Real-time energy consumption and production tracking
- Smart cost calculation based on supply and demand
- Multiple renewable energy sources support (Solar, Wind, Hydro, Biomass)
- Grid energy fallback when renewable sources are insufficient
- CSV data import for energy consumption and production
- Comprehensive API documentation with Swagger
- Rate limiting and security measures
- MongoDB for persistent storage
- Redis for caching and rate limiting

## Tech Stack

- Java 21
- Spring Boot 3.4.2
- MongoDB
- Redis
- Gradle
- Docker
- Swagger/OpenAPI

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 21 or higher
- Gradle 8.5 or higher

### Running the Application

1. Clone the repository

```bash
git clone https://github.com/migueloli/ilo-community-energy-allocation.git
```

2. Configure environment variables

Rename .env.example to .env and update the values as needed.

3. Start the infrastructure services

```bash
docker-compose --env-file .env up -d
```

The first build might take a while,
as it includes building the Docker images
and generating the fake data.

The application will be available at http://localhost:8081

### API Documentation

Access the Swagger UI at http://localhost:8080/swagger-ui.html