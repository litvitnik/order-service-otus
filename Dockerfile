FROM --platform=linux/amd64 openjdk:17
MAINTAINER vtl.ltnvsk
COPY target/order-service-1.0.0.jar order-service-1.0.0.jar
ENTRYPOINT ["java","-jar","/order-service-1.0.0.jar"]