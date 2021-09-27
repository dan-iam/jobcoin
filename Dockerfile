FROM adoptopenjdk/maven-openjdk8 AS build
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn clean package -DskipTests

FROM openjdk:8-jre-alpine
RUN apk add dumb-init
RUN mkdir /app
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
COPY --from=build /project/target/jobcoin-1.0-SNAPSHOT-jar-with-dependencies.jar /app/java-application.jar
WORKDIR /app
RUN chown -R javauser:javauser /app
USER javauser