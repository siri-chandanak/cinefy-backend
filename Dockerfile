# Use lightweight Java image
FROM eclipse-temurin:21-jdk-alpine

# Create app directory
WORKDIR /app

# copy jar
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

#Run App
ENTRYPOINT ["java","-jar","app.jar"]