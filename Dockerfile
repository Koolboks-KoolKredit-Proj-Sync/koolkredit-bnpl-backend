FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apk add --no-cache maven

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the executable JAR (not the .original)
COPY --from=build /app/target/CreditProject-*.jar app.jar

# Create uploads directory
RUN mkdir -p /tmp/uploads/receipts

EXPOSE 5000

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
