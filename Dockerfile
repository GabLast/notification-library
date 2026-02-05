#Alpine version is smaller, so when minimizing
#the final Docker image size is a critical concern, it's great
FROM maven:3.9.12-eclipse-temurin-21-alpine AS build

#Setting Enviroments / Arguments
#ARG PROFILE
ENV HOME=/home/maven/src
#build maintainer
LABEL maintainer="Gabriel <gabmarte999@gmail.com>"

COPY . $HOME
WORKDIR $HOME

#RUN mvn clean package -P$PROFILE
RUN mvn clean package

#Using alpine version
FROM eclipse-temurin:21-jre-alpine

#Setting Enviroments / Arguments
ENV HOME=/home/maven/src
#App port
EXPOSE 8080

#Execute commands.
RUN mkdir /app # create project directory

#Getting the files from the "build" instance
#then package
COPY --from=build $HOME/target/*.jar /app/app-runner.jar
# ENTRYPOINT ["java", "-jar", "/app/app-runner.jar"]
ENTRYPOINT ["java", "-cp", "/app/app-runner.jar", "com.example.notifications.Main"]
