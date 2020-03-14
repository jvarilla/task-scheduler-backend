FROM gradle:6.2.2-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:8-jre-slim

EXPOSE 7777

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/growintandem-1.0.0-SNAPSHOT-fat.jar /app/growintandem-1.0.0-SNAPSHOT-fat.jar

ENTRYPOINT ["java", "-jar","/app/growintandem-1.0.0-SNAPSHOT-fat.jar"]
