# BEMSIChat
***
BEMSI project

### The aim of the project:
The aim of the project is to create end-to-end (OTR) chat.

### Technology stack:
- Java 11
- Kotlin
- SpringBoot
- Gradle
- Git

### Build instructions

Docker and Gradle is required to the run this project. It's available [here](https://www.docker.com/get-started) and [here](https://gradle.org/install/).

```
docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management
gradle bootRun
```
