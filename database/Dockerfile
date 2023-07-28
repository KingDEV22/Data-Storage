FROM amazoncorretto:17-alpine3.16-jdk
WORKDIR /app
COPY target/database-0.0.1-SNAPSHOT.jar /app/database.jar
ENTRYPOINT ["java", "-jar", "database.jar"]