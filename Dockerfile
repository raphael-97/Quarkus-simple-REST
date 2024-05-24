FROM maven:3.9.6-eclipse-temurin-21-alpine as build

WORKDIR /app


COPY pom.xml .
COPY src src
RUN mvn package

FROM eclipse-temurin:21.0.2_13-jre-alpine

WORKDIR /app

COPY --from=build /app/target/quarkus-app .

ENTRYPOINT ["java","-jar","quarkus-run.jar"]
