package br.dev.marcosilva.springkafka.service;

import br.dev.marcosilva.fileaxe.axe.CSVAxerImpl;
import br.dev.marcosilva.fileaxe.axe.configuration.FileAxeConfigurations;
import br.dev.marcosilva.fileaxe.axe.dummy.NoPreAxingAction;
import br.dev.marcosilva.fileaxe.axe.interfaces.FileAxingStrategy;
import br.dev.marcosilva.fileaxe.axe.interfaces.FilePreAxingStrategy;
import br.dev.marcosilva.fileaxe.axe.kafka.SimpleKafkaTopicPost;
import br.dev.marcosilva.springkafka.dto.MetaFile;
import br.dev.marcosilva.springkafka.dto.SampleObject;
import br.dev.marcosilva.springkafka.properties.KafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@Slf4j
@Transactional(transactionManager = "chainedTransactionManager")
public class KafkaProducer {
    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private FileAxeConfigurations fileAxeConfigurations;

    @Autowired
    private NoPreAxingAction noPreAxingAction;

    @Autowired
    private SimpleKafkaTopicPost simpleKafkaTopicPost;

    private final KafkaTemplate<String, String> kafkaStringTemplate;
    private final KafkaTemplate<String, SampleObject> kafkaObjectTemplate;

    @Autowired
    public KafkaProducer(
            final KafkaTemplate<String, String> kafkaStringTemplate,
            final KafkaTemplate<String, SampleObject> kafkaObjectTemplate) {
        this.kafkaStringTemplate = kafkaStringTemplate;
        this.kafkaObjectTemplate = kafkaObjectTemplate;
    }

    public void sendStringKeyAndValue(String key, String message) {

        ListenableFuture<SendResult<String, String>> future = kafkaStringTemplate.send(
                kafkaProperties.getStringTopic(), key, message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=[" + message + "] due to : " + ex.getMessage());
            }
        });
    }


    public void sendStringKeyAndObjectValue(String key, SampleObject object) {
        ListenableFuture<SendResult<String,SampleObject>> future = kafkaObjectTemplate.send(
                kafkaProperties.getObjectTopic(), key, object);
        future.addCallback(new ListenableFutureCallback<SendResult<String,SampleObject>>() {

            @Override
            public void onSuccess(SendResult<String, SampleObject> result) {
                log.info("Sent message=[" + object + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=[" + object + "] due to : " + ex.getMessage());
            }
        });
    }

    public void sendStringKeyAndFileContainer(String key, MetaFile fileContainer) throws IOException {
        CSVAxerImpl.builder()
                .chunkSize(fileAxeConfigurations.getChunkSize())
                .filePreAxingStrategy(noPreAxingAction)
                .fileAxingStrategy(simpleKafkaTopicPost)
                .build()
                .processStream(key, new ByteArrayInputStream(fileContainer.getContent()));

    }


}
