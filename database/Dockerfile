FROM openjdk:17-alpine
WORKDIR /db-app
COPY target/database-0.0.1-SNAPSHOT.jar /db/database.jar
ENTRYPOINT ["java", "-jar", "database.jar"]
EXPOSE 8081