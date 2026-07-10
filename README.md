# ResteFlex Backend API

**Microservice SpringBoot indépendant** pour gestion des logements et réservations Airbnb.

## 🏗️ Structure

```
ResteFlex-Backend/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── README.md
├── src/
│   ├── main/java/com/resteflex/
│   │   ├── config/           (Swagger, Security)
│   │   ├── controller/       (REST endpoints)
│   │   ├── dto/              (Data Transfer Objects)
│   │   ├── entity/           (JPA Entities)
│   │   ├── repository/       (Data Access)
│   │   ├── service/          (Business Logic)
│   │   └── ListingsApiApplication.java
│   ├── main/resources/
│   │   └── application.yml
│   └── test/
│       ├── java/com/resteflex/
│       │   ├── service/
│       │   └── controller/
│       └── resources/
└── docs/
    ├── API.md
    └── DEPLOYMENT.md
```

## 🚀 Quick Start

### Développement local

```bash
# Cloner
git clone https://github.com/hwalidh/ResteFlex-Backend.git
cd ResteFlex-Backend

# Variables
cp .env.example .env
# Éditer .env avec vos credentials Supabase & Stripe

# Build & run
mvn clean install
mvn spring-boot:run

# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

### Docker

```bash
docker-compose up -d
# API: http://localhost:8080
```

## 📚 API Endpoints

- **Listings**: `/api/listings` - CRUD + recherche
- **Bookings**: `/api/bookings` - Réservations + Stripe
- **Swagger UI**: `/swagger-ui.html`

## 🧪 Tests

```bash
mvn test
mvn test jacoco:report
```

## 📋 Dépendances

- Spring Boot 3.2
- PostgreSQL (Supabase)
- Spring Security + JWT
- Stripe SDK
- iCal4j (calendrier)
- Swagger/OpenAPI
- JUnit 5 + Mockito

## 🔐 Variables d'environnement

```env
DB_PASSWORD=your_supabase_password
JWT_SECRET=your-jwt-secret-32-chars
STRIPE_SECRET_KEY=sk_test_...
STRIPE_PUBLISHABLE_KEY=pk_test_...
```

## ✅ Features

- CRUD Listings (15 photos max)
- Booking management
- Stripe Checkout
- iCal sync
- JWT auth
- Swagger docs
- Tests complets

**Repo séparé du frontend - Déploiement indépendant** ✨
