package br.dev.marcosilva.springkafka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.kafka")
@Getter
@Setter
public class KafkaProperties {
    private String bootstrapAddress;

    private String stringTopic;
    private String objectTopic;
    private String fileChunks;

}
