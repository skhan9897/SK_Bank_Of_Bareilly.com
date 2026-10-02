# ===============================================================
# SK BANK OF BAREILLY - DOCKERFILE
# Multi-Stage Build: Maven 3.9 + Java 17 -> Tomcat 9 + MariaDB Runtime
# ===============================================================

# Stage 1: Build Stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy Maven POM and pre-fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy project source and database scripts
COPY src ./src
COPY database ./database

# Package application into WAR
RUN mvn clean package -DskipTests

# Stage 2: Runtime Stage (Tomcat 9 + Java 17 + MariaDB Server)
FROM tomcat:9.0.86-jdk17-corretto

WORKDIR /usr/local/tomcat

# Install MariaDB server and client for self-contained database execution
RUN yum update -y && \
    yum install -y mariadb-server mariadb procps-ng && \
    yum clean all

# Remove default ROOT application
RUN rm -rf webapps/ROOT webapps/ROOT.war

# Copy compiled WAR as ROOT.war
COPY --from=builder /app/target/sk-bank-of-bareilly.war webapps/ROOT.war
COPY --from=builder /app/database/sk_bank_of_bareilly.sql /docker-entrypoint-initdb.d/schema.sql

# Copy Entrypoint Script
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Expose default port
EXPOSE 8080

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["catalina.sh", "run"]
