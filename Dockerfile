FROM openjdk:11
EXPOSE 8087
ADD target/eventsProject-1.0.0-SNAPSHOT.jar events-1.0.jar
ENTRYPOINT ["java","-jar","/events-1.0.jar"]