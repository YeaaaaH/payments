FROM openjdk:17
COPY build/libs/duo-0.0.1-SNAPSHOT.jar duo-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar", "/duo-0.0.1-SNAPSHOT.jar"]