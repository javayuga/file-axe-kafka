package br.dev.marcosilva.springkafka.config;

import br.dev.marcosilva.springkafka.properties.KafkaProperties;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Autowired
    KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic fileUpload() {
         return new NewTopic(kafkaProperties.getFileUploads(), 1, (short) 1);
    }

    @Bean
    public NewTopic stringTopic() {
        return new NewTopic(kafkaProperties.getStringTopic(), 1, (short) 1);
    }

    @Bean
    public NewTopic objectTopic() {
        return new NewTopic(kafkaProperties.getObjectTopic(), 1, (short) 1);
    }
}
