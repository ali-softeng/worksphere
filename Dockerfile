FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

RUN useradd --system worksphere

COPY --from=build /app/target/worksphere-0.0.1-SNAPSHOT.jar /app/worksphere.jar

USER worksphere

CMD ["java", "-jar", "/app/worksphere.jar"]