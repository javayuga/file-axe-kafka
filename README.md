# spring-kafka
Spring Boot Kafka playground

## Get Started

This project needs a running Kafka instance, you may use any provider of your choice.

If you prefer to use a local Docker deployment, there is a Docker Compose configuration available at [javayuga/docker-dev](https://github.com/javayuga/docker-dev/local-kafka)

Your broker URL needs to be configured at **application.yml**:

```
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

The app automatically creates these topics:

- string-topic
- object-topic
- file-uploads

Swagger-ui is available at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

If you're using **docker-dev**, messages can be inspected via Kafdrop at:

[http://localhost:19000/](http://localhost:19000/)

# References

[Swagger](https://swagger.io/tools/swagger-ui/)

[Kafdrop](https://github.com/obsidiandynamics/kafdrop)

[Baeldung's Spring-Kafka](https://www.baeldung.com/spring-kafka)

