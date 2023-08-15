FROM openjdk:17-alpine
WORKDIR /org-app
COPY target/organization-0.0.1-SNAPSHOT.jar /org-app/organization.jar
ENTRYPOINT ["java", "-jar", "organization.jar"]
EXPOSE 8080