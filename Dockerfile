# ===============================================================
# SK BANK OF BAREILLY & SK MINI BANK - UNIFIED DOCKERFILE
# Multi-Stage Build: Maven 3.9 + Java 17 -> Tomcat 9 + MariaDB Runtime
# ===============================================================

# Stage 1: Build Stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# 1. Build SK Bank of Bareilly
COPY pom.xml .
COPY src ./src
COPY database ./database
RUN mvn clean package -DskipTests

# 2. Build SK Mini Bank
WORKDIR /app/skminibank
COPY skminibank/pom.xml .
COPY skminibank/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Stage (Tomcat 9 + Java 17 + MariaDB Server)
FROM tomcat:9.0.86-jdk17-corretto

WORKDIR /usr/local/tomcat

# Install MariaDB server and client for self-contained database execution
RUN yum update -y && \
    yum install -y mariadb-server mariadb procps-ng && \
    yum clean all

# Remove default ROOT and other applications
RUN rm -rf webapps/ROOT webapps/ROOT.war webapps/skminibank webapps/skminibank.war

# Copy compiled WARs into Tomcat webapps
COPY --from=builder /app/target/sk-bank-of-bareilly.war webapps/ROOT.war
COPY --from=builder /app/skminibank/target/skminibank.war webapps/skminibank.war
COPY --from=builder /app/database/sk_bank_of_bareilly.sql /docker-entrypoint-initdb.d/schema.sql

# Copy Entrypoint Script
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Expose default port
EXPOSE 8080

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["catalina.sh", "run"]
