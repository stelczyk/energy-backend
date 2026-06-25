# Energy Mix Backend

Spring Boot REST API providing energy generation mix data for Great Britain and calculating the optimal EV charging window based on clean energy availability.

## Live

- **Energy Mix:** https://energy-backend-8wur.onrender.com/api/energy-mix
- **Optimal Window (3h):** https://energy-backend-8wur.onrender.com/api/optimal-window?hours=3
- **Frontend:** https://energy-frontend-zawz.onrender.com

> First request may take up to 60 seconds (free Render instance).

## Tech Stack

- Java 21, Spring Boot 4.1, Gradle, Docker

## Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/energy-mix` | Daily average energy mix for today, tomorrow and the day after |
| GET | `/api/optimal-window?hours={1-6}` | Best EV charging window based on clean energy |

## Run Locally

```bash
git clone https://github.com/stelczyk/energy-backend
cd energy-backend
./gradlew bootRun
```

## Run with Docker

```bash
docker build -t energy-backend .
docker run -p 8080:8080 energy-backend
```

## Tests

```bash
./gradlew test
```