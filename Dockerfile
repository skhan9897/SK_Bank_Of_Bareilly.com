# =========================================================
# SK BANK OF BAREILLY - DOCKERFILE FOR TOMCAT 9 & JAVA 17
# Multi-stage Docker build for deployment on Render / Cloud
# =========================================================

# STAGE 1: Build Web Application WAR using Maven & JDK 17
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package WAR file
COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2: Deploy WAR to Apache Tomcat 9 (Using official Docker Hub image)
FROM tomcat:9.0-jdk17-temurin
WORKDIR /usr/local/tomcat

# Remove default Tomcat webapps
RUN rm -rf webapps/*

# Copy generated WAR from builder stage as ROOT.war
COPY --from=builder /app/target/sk-bank-of-bareilly.war webapps/ROOT.war

# Expose Tomcat Port 8080
EXPOSE 8080

# Environment Variable Defaults for Database Connection
ENV DB_URL="jdbc:mysql://bjkcueu7xmg0w4f52r7x-mysql.services.clever-cloud.com:3306/bjkcueu7xmg0w4f52r7x?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8"
ENV DB_USERNAME="uqbtxyvc7q2exlt8"
ENV DB_PASSWORD="Osqa4c9dgHcCIZot4JdZ"
ENV JAVA_OPTS="-Xmx512m"

# Start Catalina Server
CMD ["catalina.sh", "run"]
