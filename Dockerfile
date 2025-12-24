# Stage 1: Build

# Dùng image chứa Maven để build code
FROM amazoncorretto:21.0.4-alpine3.18

# Tạo thư mục làm việc trong container
WORKDIR /last-dance-backend

# Sao chép file JAR đã build từ máy host vào container
COPY target/*.jar last-dance-backend.jar

# Bảo mật
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java","-jar","last-dance-backend.jar"]

# Stage 2: Run

FROM eclipse-temurin:21-jre

WORKDIR /last-dance-backend

COPY --from=build /last-dance-backend/target/*.jar last-dance-backend.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
