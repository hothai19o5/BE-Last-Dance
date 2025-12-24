# Stage 1: Build

# Dùng image chứa Maven để build code
FROM maven:3.9.9-eclipse-temurin-21 AS build
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
# Tạo user không phải root để chạy ứng dụng
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
# Mở cổng 8080 để truy cập ứng dụng
EXPOSE 8080
# Chạy ứng dụng
ENTRYPOINT ["java","-jar","last-dance-backend.jar"]
