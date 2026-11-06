# Spring Boot + SQLite example: a tiny social network API

A small, readable reference for **Maven**, **Spring Boot**, and **building REST APIs in Java**.
Users register, log in (JWT), and create/list/delete posts.

**Stack:** Java 21 · Spring Boot 4 · Maven (wrapper included) · Spring Data JPA / Hibernate · SQLite · Spring Security (JWT)

## Run it

Requires JDK 21+. No database server needed: SQLite lives in a file (`social.db`, created on first run).

```bash
./mvnw spring-boot:run     # start on http://localhost:8080
./mvnw test                # run the tests
./mvnw clean package       # build target/socialapi-0.0.1-SNAPSHOT.jar
java -jar target/socialapi-0.0.1-SNAPSHOT.jar
```

Set `JWT_SECRET` (32+ chars) in any real environment; the default in `application.properties` is for demos only.

## Try it

```bash
# 1. Register (public)
curl -X POST localhost:8080/api/users -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"password123","bio":"hello"}'

# 2. Log in -> returns {"token": "...", "type": "Bearer"}
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"password123"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')

# 3. Create, list, fetch, delete posts
curl -X POST localhost:8080/api/posts -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"content":"my first post"}'
curl 'localhost:8080/api/posts?limit=10' -H "Authorization: Bearer $TOKEN"
curl localhost:8080/api/posts/1 -H "Authorization: Bearer $TOKEN"
curl -X DELETE localhost:8080/api/posts/1 -H "Authorization: Bearer $TOKEN"
```

## Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/users` | no | Register |
| POST | `/api/auth/login` | no | Get a JWT |
| GET | `/api/users/me` | yes | Current user |
| GET | `/api/users/{id}` | yes | A user's profile |
| POST | `/api/posts` | yes | Create a post (max 280 chars) |
| GET | `/api/posts?authorId=&limit=` | yes | Newest first (limit 1-100, default 20) |
| GET | `/api/posts/{id}` | yes | One post |
| DELETE | `/api/posts/{id}` | yes | Delete (author only, else 403) |

Errors are JSON ([RFC 7807](https://www.rfc-editor.org/rfc/rfc7807)): 400 validation (with per-field `errors`), 401, 403, 404, 409.

## Project tour

```
pom.xml                      Maven build: dependencies + Boot plugin (versions come from the Boot parent)
mvnw, .mvn/                  Maven wrapper: runs the right Maven version without installing it
src/main/resources/application.properties   config: datasource, JPA, JWT
src/main/java/com/example/socialapi/
  SocialapiApplication.java  entry point (@SpringBootApplication)
  user/   User (entity) · UserRepository · UserService · UserController · UserDtos
  post/   Post (entity) · PostRepository · PostService · PostController · PostDtos
  auth/   SecurityConfig (filter chain, JWT beans) · TokenService · AuthController
  error/  exceptions + ApiExceptionHandler (exception -> HTTP response)
src/test/                    end-to-end tests with MockMvc against a throwaway SQLite file
```

Request flow: **Controller** (HTTP in/out, validation) → **Service** (rules, transactions) → **Repository** (database) → **Entity**.
DTOs (Java records) define the JSON shapes so entities, such as the password hash, never leak.

## Things worth reading in the code

- **SQLite setup:** `application.properties` (driver, `SQLiteDialect`, pool size 1) and the two SQLite deps in `pom.xml`.
- **JWT without extra libraries:** `SecurityConfig` uses Spring Security's built-in resource server; `TokenService` signs tokens.
- **Authorization rule:** `PostService.delete` checks the requester is the author.
- **N+1 avoidance:** `@EntityGraph` in `PostRepository`.

## Production caveats (intentionally out of scope)

`ddl-auto=update` should be replaced by migrations (Flyway/Liquibase); no refresh tokens, rate limiting, or pagination cursors;
SQLite suits demos and small apps, not heavily concurrent writes.
