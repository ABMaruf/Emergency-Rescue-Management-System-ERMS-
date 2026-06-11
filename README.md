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
|-- pom.xml
|-- README.md
|-- apache-maven-3.9.16-bin.zip
|-- mysql-installer-web-community-8.0.46.0.msi
|-- src/
|   `-- main/
|       |-- java/
|       |   `-- com/erms/
|       |       |-- ErmsApplication.java
|       |       |-- config/
|       |       |-- controller/
|       |       |-- dto/
|       |       |-- entity/
|       |       |-- enums/
|       |       |-- repository/
|       |       |-- security/
|       |       |-- service/
|       |       `-- util/
|       `-- resources/
|           |-- application.properties
|           |-- static/
|           |   `-- css/
|           |       `-- style.css
|           `-- templates/
|               |-- admin/
|               |-- auth/
|               |-- chat/
|               |-- map/
|               |-- notifications/
|               |-- user/
|               `-- volunteer/
`-- uploads/
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

## Maven Setup Guide

Maven is the Java build tool used by this project. It reads `pom.xml`, downloads the Spring Boot dependencies, compiles Java files, and runs the application.

This repository includes the Maven ZIP file:

```text
apache-maven-3.9.16-bin.zip
```

### Option A: Use The Maven ZIP From This Repository

1. Open the project folder:

   ```text
   C:\Users....\ERMS
   ```

2. Extract:

   ```text
   apache-maven-3.9.16-bin.zip
   ```

3. After extraction, the Maven executable should be here:

   ```text
   C:\Users\Abdullah\Desktop\ERMS\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd
   ```

4. Add Maven to Windows PATH:

   - Press `Windows + S`
   - Search `Environment Variables`
   - Open `Edit the system environment variables`
   - Click `Environment Variables`
   - Under `User variables`, select `Path`
   - Click `Edit`
   - Click `New`
   - Paste this path:

     ```text
     C:\Users\Abdullah\Desktop\ERMS\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin
     ```

   - Click `OK` on all windows

5. Close PowerShell and open a new PowerShell window.

6. Verify Maven:

   ```powershell
   mvn -v
   ```

   Expected output should include:

   ```text
   Apache Maven 3.9.16
   Java version: 21.x
   ```

### Option B: Use Maven Without Adding PATH

If you do not want to edit PATH, run Maven using the full path.

Compile:

```powershell
& "C:\Users\Abdullah\Desktop\ERMS\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd" clean compile
```

Run:

```powershell
& "C:\Users\Abdullah\Desktop\ERMS\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run
```

### Common Maven Problems

If PowerShell says:

```text
mvn : The term 'mvn' is not recognized
```

Maven is not in PATH. Fix it by reopening PowerShell after updating PATH, or use the full `mvn.cmd` path shown above.

If Maven cannot download dependencies, check your internet connection and force dependency updates:

```powershell
mvn clean compile -U
```

## MySQL Setup Guide

MySQL stores ERMS users, volunteers, help requests, tasks, notifications, and chat messages.

This repository includes the MySQL Windows installer:

```text
mysql-installer-web-community-8.0.46.0.msi
```

This is the MySQL web installer. It may download selected MySQL components during installation, so an internet connection may still be required.

### Install MySQL On Windows

1. Double-click:

   ```text
   mysql-installer-web-community-8.0.46.0.msi
   ```

2. If Windows asks for permission, click `Yes`.

3. Choose setup type:

   ```text
   Developer Default
   ```

   If you want a smaller installation, choose `Custom` and select:

   - MySQL Server 8.0
   - MySQL Workbench
   - MySQL Shell

4. Continue through the installer and allow it to download/install required components.

5. At authentication method, choose:

   ```text
   Use Strong Password Encryption for Authentication
   ```

6. Set the MySQL root password.

   This project currently uses:

   ```text
   root
   ```

   If you choose a different password, update `src/main/resources/application.properties`.

7. Keep the default MySQL port:

   ```text
   3306
   ```

8. Finish installation and make sure MySQL Server is running as a Windows service.

### Verify MySQL Is Running

Open a new PowerShell window:

```powershell
mysql -u root -p
```

Enter the root password. If login works, you will see:

```text
mysql>
```

Exit MySQL:

```sql
exit;
```

If `mysql` is not recognized, MySQL is installed but not in PATH. Add this folder to PATH:

```text
C:\Program Files\MySQL\MySQL Server 8.0\bin
```

Then close and reopen PowerShell.

### Create The ERMS Database

The application URL includes `createDatabaseIfNotExist=true`, so Spring Boot can create the database automatically if the MySQL user has permission.

Manual creation is still recommended. In MySQL Workbench or MySQL command line:

```sql
CREATE DATABASE erms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Check that it exists:

```sql
SHOW DATABASES;
```

### Configure MySQL In The Project

Open:

```text
src/main/resources/application.properties
```

Current configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/erms_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Dhaka
spring.datasource.username=root
spring.datasource.password=root
```

If your MySQL password is different, change only:

```properties
spring.datasource.password=your_mysql_password
```

### First Startup Database Behavior

This setting:

```properties
spring.jpa.hibernate.ddl-auto=update
```

means Hibernate creates or updates the required tables automatically from the Java entity classes. You do not need to manually create tables.

On first startup, `DataSeeder.java` inserts the default admin, volunteer, and user accounts if they do not already exist.

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
