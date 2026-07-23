FROM eclipse-temurin:25-jdk-alpine AS build
LABEL authors="bipin-koirala"
WORKDIR /app

COPY --link mvnw .
COPY --link .mvn .mvn

COPY --link pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B

COPY --link src ./src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -Dmaven.test.skip=true -B

RUN mv /app/target/*.jar /app/app.jar && \
    java -Djarmode=tools -jar /app/app.jar extract --layers --destination /app/extracted 

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/extracted/dependencies/ ./
COPY --from=build /app/extracted/snapshot-dependencies/ ./
COPY --from=build /app/extracted/spring-boot-loader/ ./
COPY --from=build /app/extracted/application/ ./

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]