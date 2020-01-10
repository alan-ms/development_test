FROM openjdk:8-jdk-alpine

RUN mkdir /development_test

WORKDIR /development_test

COPY . /development_test

EXPOSE 8080

CMD ["./mvnw"]
