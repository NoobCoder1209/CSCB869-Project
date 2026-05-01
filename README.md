# Electronic Medical Records System (CSCB869)

A Spring Boot web application for managing electronic medical records, developed as a course project for CSCB869 at New Bulgarian University.

## Tech Stack

| Component        | Technology                          |
|------------------|-------------------------------------|
| Backend          | Spring Boot 3.2.5                   |
| Language         | Java 17                             |
| Template Engine  | Thymeleaf + Layout Dialect 3.3.0    |
| CSS              | Tailwind CSS (CDN)                  |
| Database         | H2 (file-based)                     |
| Security         | Spring Security 6 (form login, BCrypt) |
| Build Tool       | Maven (with Maven Wrapper)          |
| ORM              | Spring Data JPA / Hibernate         |

## Quick Start

```bash
cd medical-record
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in your browser.

## Demo Credentials

| Username     | Password    | Role    |
|--------------|-------------|---------|
| admin        | admin123    | Admin   |
| dr.petrov    | doctor123   | Doctor  |
| dr.dimitrov  | doctor123   | Doctor  |
| dr.todorov   | doctor123   | Doctor  |
| patient1     | patient123  | Patient |
| patient2     | patient123  | Patient |

## Features

- **Patient Management** -- CRUD operations, personal doctor assignment, insurance status
- **Doctor Management** -- CRUD with specialty and GP designation
- **Diagnosis Catalog** -- ICD-10 coded diagnoses
- **Examinations** -- Full visit records linking doctor, patient, diagnosis, and treatment
- **Sick Leaves** -- Issue and track medical leave certificates
- **Insurance Tracking** -- Monthly payment records with 6-month coverage validation
- **11 Reports** -- Statistical queries (patients by diagnosis, most common diagnosis, payments per doctor, visit history, sick leave statistics, etc.)
- **Role-Based Access** -- Admin, Doctor, and Patient dashboards with method-level security
- **Bulgarian UI** -- All interface labels and data in Bulgarian

## Project Structure

```
medical-record/
├── src/main/java/bg/medicalrecord/
│   ├── config/          # SecurityConfig, DataInitializer
│   ├── controller/      # MVC controllers (10 controllers)
│   ├── dto/             # Data transfer objects + report DTOs
│   ├── exception/       # Global exception handling
│   ├── model/           # JPA entities + enums
│   ├── repository/      # Spring Data JPA repositories
│   └── service/         # Business logic (8 services)
├── src/main/resources/
│   ├── templates/       # Thymeleaf HTML templates
│   └── application.properties
└── pom.xml
```

## Database Console

H2 console is available at [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (admin only).

- JDBC URL: `jdbc:h2:file:./data/medicaldb`
- Username: `sa`
- Password: *(empty)*

## License

University course project. Not intended for production use.
