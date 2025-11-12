# content-sharing-test

Spring Boot **3.3** app for sharing media (video/artwork/music/game) with authentication, ratings and **multipart file uploads**.  
Includes **Swagger UI**, cookie-based auth, and MySQL.

> Repo: https://github.com/StivenVA/ngp-content-sharing-test/tree/main

---

## Tech stack

- Java 17, Spring Boot 3.3
- Spring Web, Security, Data JPA
- MySQL 8 (driver `mysql-connector-j`)
- Multipart uploads (`MultipartFile`)
- OpenAPI/Swagger (springdoc 2.x)
- Maven Wrapper (`mvnw`) – **no system Maven needed**

---

## Project layout (high level)

```
src/main/java/.../auth        # login/register/refresh; cookies
src/main/java/.../media       # CRUD + multipart upload endpoints
src/main/java/.../rating      # add/edit/delete ratings
src/main/resources/
  application.properties      # MySQL + multipart size
```

---

## Configuration

Default DB config:

```
spring.datasource.url=jdbc:mysql://localhost:3306/content_sharing_test?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password123
spring.jpa.hibernate.ddl-auto=update

# upload sizes
spring.servlet.multipart.max-file-size=1GB
spring.servlet.multipart.max-request-size=1GB
```

> Change via environment variables at runtime if needed:  
> `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.

---

## Run locally (no Docker)

1) Start **MySQL** (container or local)
```bash
docker run -d --name mysql 
  -e MYSQL_ROOT_PASSWORD=password123 
  -e MYSQL_DATABASE=content_sharing_test 
  -p 3306:3306 
  mysql:8
```

2) Run the app (using **Maven Wrapper**)
```bash
# from repo root
./mvnw spring-boot:run
# or build a jar
./mvnw clean package
java -jar target/content-sharing-test-0.0.1-SNAPSHOT.jar
```

3) Open **Swagger UI**
- http://localhost:8080/swagger-ui/index.html  
- OpenAPI JSON: http://localhost:8080/v3/api-docs

---

## Docker

### Build image
```bash
docker build -t content-sharing-test:latest .
```

### Run with MySQL on the host
```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/content_sharing_test?useSSL=false&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password123 \
  --name content-sharing-test content-sharing-test:latest
```

> On Linux, replace `host.docker.internal` with your host IP or run both services with **docker-compose** (recommended).

---

## docker-compose (recommended)

```yaml
services:
  db:
    image: mysql:8
    container_name: mysql
    environment:
      MYSQL_ROOT_PASSWORD: password123
      MYSQL_DATABASE: content_sharing_test
    ports:
      - "3306:3306"
    volumes:
      - dbdata:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "127.0.0.1", "-uroot", "-ppassword123", "--silent"]
      interval: 5s
      timeout: 3s
      retries: 20

  app:
    build: .
    container_name: content-sharing-test
    depends_on:
      db:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: "jdbc:mysql://db:3306/content_sharing_test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: password123

      SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT: "0"
    ports:
      - "8080:8080"
    command: >
      sh -c "until nc -z db 3306; do echo 'waiting for db...'; sleep 2; done;
             java -jar /app/app.jar"

volumes:
  dbdata:

```

Run everything:
```bash
docker compose up --build
```
App: http://localhost:8080 · Swagger: http://localhost:8080/swagger-ui/index.html

---

## Endpoints (short tour)

### Auth (cookies)
- `POST /api/auth/register`
- `POST /api/auth/login` → sets `access_token` & `refresh_token` cookies
- `GET /api/auth/refresh`

> In **Swagger**, after logging in, cookies are kept; subsequent calls to protected endpoints will be authenticated.

### Media (multipart)
- `GET /api/media/{id}`
- `GET /api/media/all?page&size`
- `GET /api/media/user?page&size` (auth)
- `POST /api/media/filter` (JSON)
- `POST /api/media/create` (**multipart/form-data** parts):
  - `mediaContent` (file, required), `thumbnail` (file, optional)
  - `title` (string), `description` (string), `category` (`GAME|VIDEO|ARTWORK|MUSIC`)
- `PUT /api/media/update/{id}` (**multipart/form-data**)
- `DELETE /api/media/delete/{id}`

### Ratings
- `POST /api/rating/add`
- `PUT /api/rating/edit`
- `DELETE /api/rating/delete/{id}`

---

## cURL quick test

```bash
# 1) register (if needed)
curl -i -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo"}' \
  http://localhost:8080/api/auth/register

# 2) login (save cookies)
curl -i -c cookies.txt -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo"}' \
  http://localhost:8080/api/auth/login

# 3) upload media
curl -b cookies.txt -i -X POST http://localhost:8080/api/media/create \
  -H "Content-Type: multipart/form-data" \
  -F "mediaContent=@/path/to/video.mp4" \
  -F "thumbnail=@/path/to/thumbnail.jpg" \
  -F "title=Slowtown by twenty one pilots" \
  -F "description=A music video HEHE" \
  -F "category=VIDEO"
```

---

## Swagger shows JSON body instead of file pickers?

Make sure the controller method declares **multipart** and uses `MultipartFile` with `@RequestPart` or a `@ModelAttribute` form:

```java
@PostMapping(value="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public MediaResponse create(
    @RequestPart("mediaContent") MultipartFile mediaContent,
    @RequestPart(value="thumbnail", required=false) MultipartFile thumbnail,
    @RequestPart("title") String title,
    @RequestPart("description") String description,
    @RequestPart("category") MediaCategory category
) { ... }
```

If you wrap files inside a JSON DTO or forget `consumes`, Swagger will render a JSON body instead.

---

## Troubleshooting

### IntelliJ run shows `java.lang.ExceptionInInitializerError com.sun.tools.javac.code.TypeTag :: UNKNOWN`
This is a Lombok + JDK mismatch in the **IDE**. Fix by aligning IntelliJ to **JDK 17** everywhere:

- *Project SDK* = 17, *Language level* = 17  
- *Settings → Build Tools → Maven → Runner JRE* = 17 (or use bundled Maven)  
- *Compiler* target bytecode = 17, *Use --release* = ON  
- *Annotation Processors* = Enabled, Obtain from classpath  
- Lombok plugin updated; then *Invalidate Caches* and *Rebuild*.

Your POM already pins Lombok `1.18.36` and sets `maven-compiler-plugin` to release 17.

---
