# Use OpenJDK 11 as base image
FROM openjdk:11-jdk

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Create test-output directory
RUN mkdir -p test-output/screenshots test-output/reports

# Expose port for Selenium Grid (if needed)
EXPOSE 4444

# Default command to run tests
CMD ["./mvnw", "clean", "test", "-Duse.grid=true", "-Dgrid.url=http://selenium-hub:4444/wd/hub"] 