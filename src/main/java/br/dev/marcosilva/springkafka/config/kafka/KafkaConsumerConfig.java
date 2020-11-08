package br.dev.marcosilva.springkafka.config.kafka;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import br.dev.marcosilva.fileaxe.axe.kafka.dto.FileChunkDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import lombok.extern.slf4j.Slf4j;

@EnableKafka
@Configuration
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
@Slf4j
public class KafkaConsumerConfig {

    private KafkaProperties properties;

    @Value("#{new Boolean('${spring.kafka.consumer.acknowledgeOnRecoveryCallback:false}')}")
    private Boolean acknowledgeOnRecoveryCallback;

    @Value("${spring.kafka.consumer.maxIdleInterval: 60000}")
    private Long maxIdleInterval;

    @Value("${spring.kafka.consumer.maxAttempts: 3}")
    private Integer maxAttempts;

    @Autowired
    public KafkaConsumerConfig(KafkaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ConsumerFactory<String, FileChunkDTO> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                properties.buildConsumerProperties(), new StringDeserializer(), new JsonDeserializer<>(FileChunkDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FileChunkDTO> retryKafkaListenerContainerFactory(KafkaErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, FileChunkDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        RetryTemplate retryTemplate = new RetryTemplate();
        ExceptionClassifierRetryPolicy exceptionClassifierRetryPolicy = new ExceptionClassifierRetryPolicy();
        HashMap<Class<? extends Throwable>, RetryPolicy> policies = new HashMap<Class<? extends Throwable>, RetryPolicy>();
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(maxAttempts);
        policies.put(Exception.class, simpleRetryPolicy);
        exceptionClassifierRetryPolicy.setPolicyMap(policies);
        retryTemplate.setRetryPolicy(exceptionClassifierRetryPolicy);
        factory.setRetryTemplate(retryTemplate);
        factory.setErrorHandler(kafkaErrorHandler);
        factory.setRecoveryCallback(new ConsumerRecoveryCallback());
        factory.getContainerProperties().setAckMode(AckMode.MANUAL);
        return factory;
    }

    private class ConsumerRecoveryCallback implements RecoveryCallback<Object> {

        @Override
        public Object recover(RetryContext context) {
            ConsumerRecord<?, ?> record = (ConsumerRecord<?, ?>) context.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD);
            logRecord(record);
            Acknowledgment acknowledgment = (Acknowledgment) context.getAttribute(RetryingMessageListenerAdapter.CONTEXT_ACKNOWLEDGMENT);
//            log.info("acknowledgement value: {}", acknowledgment);
//            log.info("acknowledgeOnRecoveryCallback value: {}", acknowledgeOnRecoveryCallback);
            if ((acknowledgeOnRecoveryCallback != null && acknowledgeOnRecoveryCallback) && acknowledgment != null) {
                acknowledgment.acknowledge();
                log.info("executing acknowledge on recoverycallback");
            }
            return null;
        }

        private void logRecord(ConsumerRecord<?, ?> record) {
//            log.info("Recovery callback: {}", record);
            try {
                record.headers().forEach(h -> log.info(h.key(), new String(h.value(), StandardCharsets.UTF_8)));
            } catch (Exception e) {
                log.error("Error while logging headers", e);
            }
        }
    }

}
