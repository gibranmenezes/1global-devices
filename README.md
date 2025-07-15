# Devices API

## Overview

[cite_start]This project implements a RESTful API for device management, as per the 1GLOBAL challenge specifications[cite: 1]. The API allows for the persistence and manipulation of device resources, including functionalities for creation, retrieval, updating (full and partial), and deletion.

## Architectural Pattern

The application adheres to a **Port and Adapters Architecture** pattern, with a clear separation of concerns. It also incorporates principles from **Clean Architecture / Hexagonal Architecture** through the adoption of Ports and Adapters. This promotes dependency inversion and facilitates testability and maintainability, ensuring the core business logic remains independent of infrastructure technologies (like databases and web frameworks).

* **`adapter.in` (Controllers):** The entry layer, responsible for receiving HTTP requests and translating them into calls to the application layer.
* **`application.port.in` (Use Cases):** Defines the interfaces for the use cases, representing the core business logic.
* **`application.service` (Use Case Implementations):** Implements the use cases defined by the inbound ports.
* **`application.port.out` (Repositories):** Defines the interfaces for the outbound ports for communication with infrastructure.
* **`adapter.out.persistence` (Adapters/Repositories):** Implements the outbound ports, adapting the business logic to the persistence technology (JPA/Hibernate).
* **`domain` (Core Business Logic):** Contains the core domain entities, enums, and essential business rules.

## Supported Functionalities

The API offers the following capabilities for device management:

* **Create** a new device.
* **Fully and/or partially update** an existing device. 
* **Fetch** a single device by ID.
* **Fetch** all devices with pagination support.
* **Filter** devices by brand.
* **Filter** devices by state.
* **Delete** a single device.

## Domain Validations

The following business rules are enforced:

* **Creation time** cannot be updated.
* **Name and brand** properties cannot be updated if the device is `IN_USE`. 
* **Devices in the **`IN_USE` state cannot be deleted**. 
* A newly created device is always initialized with the **`AVAILABLE` state**. 
* State changes must be made explicitly through the update functionality.

## Technologies Used

* **Java 21+** 
* **Spring Boot 3.x:** 
* **Maven 3.9+:**
* **Spring Data JPA / Hibernate:**
* **PostgreSQL:**
* **Flyway:**
* **Docker / Docker Compose:** 
* **JUnit 5, Mockito, Spring Boot Test, Testcontainers:** 
* **Swagger (SpringDoc OpenAPI):** 

## How to Run the Project

To run the application locally using Docker Compose, follow these steps:

### Prerequisites

* Docker Desktop (or Docker Engine and Docker Compose) installed.
* Java 21+ installed.

### Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/gibranmenezes/1global-devices.git
    cd one-global-api 
    ```

2.  **Build the Maven project:**
    Navigate to the project's root directory and execute the docker compose command to run the containers.
    ```bash
     docker-compose up -d
    ```

3.  **Verify if all containers are running:**
    ```bash
    docker ps
    ```
   

4.  **Access the application:**

    | Service | URL | Description |
    | --- | --- | --- |
    | API | http://localhost:8080/global-api | API REST Java/Spring Boot |
    | Swagger  UI | http://localhost:8080/global-api/swagger-ui/index.html | API Documentation |
    | Admine |http://localhost:8081 |Database GUI |

5.  **Stop applications:**
    To stop the application, run:
    ```bash
    docker-compose down
    ```