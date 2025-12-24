# Stage 1: Build

# Dùng image chứa Maven để build code
FROM amazoncorretto:21.0.4-alpine3.18 AS build

# Tạo thư mục làm việc trong container
WORKDIR /last-dance-backend

# Copy file pom.xml và mã nguồn vào container
COPY pom.xml .

COPY src ./src

# Build project và tạo file JAR
RUN mvn clean package -DskipTests

# Stage 2: Run

# Dùng image chứa JRE để chạy ứng dụng
FROM eclipse-temurin:21-jre

WORKDIR /last-dance-backend

# Copy file JAR từ stage build sang stage run
COPY --from=build /last-dance-backend/target/*.jar last-dance-backend.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","last-dance-backend.jar"]
