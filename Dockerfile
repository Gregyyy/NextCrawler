#
# Build stage
#
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build
COPY ./ /opt/build/
COPY pom.xml /opt/build
RUN mvn -f /opt/build/pom.xml clean package -Dmaven.test.skip

#
# Package stage
#
FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app
COPY --from=build /opt/build/target/*-jar-with-dependencies.jar /opt/app/app.jar
CMD ["java", "-jar", "app.jar", "stable"]