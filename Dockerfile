# ══════════════════════════════════════════════════════════
#  ParkMate Backend — Dockerfile
#  Multi-stage build:
#    Stage 1 (builder) → compiles Java code using Maven
#    Stage 2 (runner)  → runs the compiled JAR (smaller image)
#
#  Result image size: ~200MB (vs ~600MB if single stage)
# ══════════════════════════════════════════════════════════

# ── STAGE 1: BUILD ────────────────────────────────────────
# Uses official Maven + Java 17 image to compile the project
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml first — Docker caches this layer
# If only your code changes (not pom.xml), Maven won't re-download dependencies
COPY pom.xml .

# Download all dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the JAR, skip tests (tests need DB connection, we skip in build)
RUN mvn clean package -DskipTests -B

# ── STAGE 2: RUN ──────────────────────────────────────────
# Uses a slim Java 17 JRE — no Maven, no source code, just the JAR
FROM eclipse-temurin:17-jre-jammy

# Set working directory
WORKDIR /app

# Create a non-root user (security best practice)
RUN addgroup --system parkmate && adduser --system --group parkmate

# Copy ONLY the built JAR from the builder stage
COPY --from=builder /app/target/parkmate-backend-*.jar app.jar

# Give ownership to the non-root user
RUN chown parkmate:parkmate app.jar

# Switch to non-root user
USER parkmate

# Expose port 8080
EXPOSE 8080

# Health check — Render (and Docker) uses this to know if app is alive
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# ── START COMMAND ─────────────────────────────────────────
# SPRING_PROFILES_ACTIVE=prod → activates PostgreSQL config in application.properties
# -Xmx256m                   → limits memory to 256MB (fits Render free tier: 512MB)
# -Xms64m                    → starts with 64MB heap
# -XX:+UseContainerSupport   → tells JVM it's inside Docker (better memory management)
ENTRYPOINT ["java", \
  "-Xms64m", \
  "-Xmx256m", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]
