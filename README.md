# Interview Trainer

An AI-powered, chat-style trainer for practising technical topics. Pick any topic and subtopic
(Java, Spring Boot → Security, Kafka, OpenShift, ...) and a level (junior, mid, senior, architect);
the app generates fresh questions, evaluates your free-text answers against a rubric, and digs deeper
with follow-up questions. Everything is kept, so weak spots come back for repetition.

> Status: early development.

## Tech stack
- Java 25, Spring Boot 4.1, Maven
- Spring Web MVC, Spring Data JPA, Bean Validation, Actuator
- Flyway migrations; H2 in PostgreSQL mode (local), PostgreSQL (planned)
- Claude API via the Anthropic Java SDK (structured outputs)
- Angular frontend (planned)

## Running locally
Requirements: JDK 25, Maven 3.9+, and an Anthropic API key for the AI features
(the app also starts without one, but AI calls will fail):

```bash
setx ANTHROPIC_API_KEY "<your key>"
```

```bash
mvn spring-boot:run
```

- App: http://localhost:8080
- Health check: http://localhost:8080/actuator/health
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/interview-trainer`, user `sa`, empty password)

Run the tests:

```bash
mvn verify
```
