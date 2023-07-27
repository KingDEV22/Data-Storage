FROM amazoncorretto:17-alpine3.16-jdk
WORKDIR /app
COPY target/organization-0.0.1-SNAPSHOT.jar /app/organization.jar
ENTRYPOINT ["java", "-jar", "organization.jar"]