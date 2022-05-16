FROM maven:3.8.3-openjdk-17 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

RUN ls

FROM openjdk:17-alpine
COPY --from=MAVEN_BUILD /build/target/ApplicationForManagingRequests-1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]