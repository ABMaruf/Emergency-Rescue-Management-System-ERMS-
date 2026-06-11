# ERMS - Emergency Rescue Management System

ERMS is a Spring Boot MVC web application for emergency help request management. It supports three roles: admin, volunteer, and user. Users submit emergency requests with GPS/map location, admins monitor and assign requests, and volunteers manage assigned tasks.

## Features

- Role-based login for `ADMIN`, `VOLUNTEER`, and `USER`
- User emergency request submission with location picker and GPS support
- Admin dashboard for requests, volunteers, users, and assignment workflow
- Volunteer dashboard for assigned tasks and availability/location updates
- Notifications shown from dashboard topbar popup
- Request chat using WebSocket/STOMP for active requests
- Live map page for request and volunteer coordination
- Responsive UI with mobile sidebar behavior
- MySQL persistence through Spring Data JPA/Hibernate

## Tech Stack

- Java 21
- Spring Boot 3.2.5
- Spring MVC
- Spring Security
- Spring Data JPA / Hibernate
- Thymeleaf
- Spring WebSocket with STOMP and SockJS
- MySQL
- Maven
- Leaflet.js for maps
- Lucide icons

## Project Structure

```text
ERMS/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/
│       │   └── com/erms/
│       │       ├── ErmsApplication.java
│       │       ├── config/
│       │       ├── controller/
│       │       ├── dto/
│       │       ├── entity/
│       │       ├── enums/
│       │       ├── repository/
│       │       ├── security/
│       │       ├── service/
│       │       └── util/
│       └── resources/
│           ├── application.properties
│           ├── static/
│           │   └── css/
│           │       └── style.css
│           └── templates/
│               ├── admin/
│               ├── auth/
│               ├── chat/
│               ├── map/
│               ├── notifications/
│               ├── user/
│               └── volunteer/
└── uploads/
```

## Requirements

Install these before running the project:

- Java JDK 21
- Maven 3.9 or newer
- MySQL 8.x
- VS Code, IntelliJ IDEA, or another Java IDE

Check versions:

```powershell
java -version
mvn -v
mysql --version
```

## Database Setup

The application is configured to use MySQL database `erms_db`.

Current database config in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/erms_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Dhaka
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

Because `createDatabaseIfNotExist=true` is enabled, Spring can create the database if the MySQL user has permission. You can also create it manually:

```sql
CREATE DATABASE erms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

If your MySQL password is different, update:

```properties
spring.datasource.password=your_password
```

## Run The Application

From the project root folder:

```powershell
mvn clean compile
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## Default Login Accounts

`DataSeeder.java` creates these accounts automatically on first startup:

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `Admin@123` |
| Volunteer | `volunteer1` | `Volunteer@123` |
| User | `user1` | `User@123` |

Use these only for local development.

## Main Routes

| Route | Purpose |
| --- | --- |
| `/login` | Login page |
| `/register` | User/volunteer registration |
| `/admin/dashboard` | Admin dashboard |
| `/admin/requests` | Admin request management |
| `/admin/volunteers` | Admin volunteer list |
| `/admin/users` | Admin user list |
| `/user/dashboard` | User request panel |
| `/volunteer/dashboard` | Volunteer task panel |
| `/chat/{requestId}` | Request chat room |
| `/map/{requestId}` | Live request map |
| `/notifications` | Notification page |
| `/logout` | POST logout endpoint |

## WebSocket Details

WebSocket is configured in `WebSocketConfig.java`.

- SockJS endpoint: `/ws`
- Application prefix: `/app`
- Broadcast topic prefix: `/topic`

Chat messages use:

```text
Client sends: /app/chat/{room}
Client listens: /topic/chat/{room}
```

Location updates use:

```text
Volunteer sends: /app/location/volunteer/{requestId}
User sends: /app/location/user/{requestId}
Clients listen: /topic/location/{requestId}
```

## Chat Availability Rule

Chat is active only when a request status is:

- `PENDING`
- `ASSIGNED`
- `IN_PROGRESS`

When the request becomes `COMPLETED` or `CANCELLED`, the chat page becomes read-only.

## Build A JAR

```powershell
mvn clean package
java -jar target/erms-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### `mvn` is not recognized

Maven is not in your PATH. Use the Maven folder already downloaded in this project or install Maven globally, then reopen PowerShell and run:

```powershell
mvn -v
```

### MySQL access denied

Your password in `application.properties` does not match your MySQL root password.

```properties
spring.datasource.username=root
spring.datasource.password=root
```

### Port 8080 already in use

Change the port:

```properties
server.port=8081
```

Then open:

```text
http://localhost:8081
```

### Tables are missing

Make sure this is enabled:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Then restart the app.

### Logout gives 404

Spring Security logout should be submitted with POST. Templates should use:

```html
<form th:action="@{/logout}" method="post">
    <button type="submit">Logout</button>
</form>
```

## Development Notes

- Keep controller code in `src/main/java/com/erms/controller/`.
- Keep business logic in `src/main/java/com/erms/service/`.
- Keep database access in `src/main/java/com/erms/repository/`.
- Keep JPA tables as entity classes in `src/main/java/com/erms/entity/`.
- Keep HTML pages in `src/main/resources/templates/`.
- Keep CSS in `src/main/resources/static/css/style.css`.

## Compile Check

Use this command before sharing or submitting the project:

```powershell
mvn clean compile
```

If it ends with `BUILD SUCCESS`, the Java code compiles correctly.
