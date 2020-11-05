package br.dev.marcosilva.springkafka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.kafka")
@Getter
@Setter
public class KafkaProperties {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    private String fileUploads;
    private String stringTopic;
    private String objectTopic;

}
