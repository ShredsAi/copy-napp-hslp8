# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /build
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy

# Add maintainer info
LABEL maintainer="Shreds Team <team@shreds.ai>"
LABEL version="1.0.0"
LABEL description="Menu Record Management Service"

# Create non-root user
RUN useradd -r -u 1001 -g root shreds

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /build/target/shreds-menu-1.0.0.jar app.jar

# Set ownership to non-root user
RUN chown shreds:root /app/app.jar

# Use non-root user
USER 1001

# Set environment variables
ENV JAVA_OPTS="-XX:+UseG1GC \
    -XX:MaxGCPauseMillis=100 \
    -XX:+UseStringDeduplication \
    -Xmx512m \
    -Xms256m \
    -Djava.security.egd=file:/dev/./urandom \
    -Duser.timezone=UTC \
    -Dfile.encoding=UTF-8"

ENV PORT=8080
EXPOSE ${PORT}

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
