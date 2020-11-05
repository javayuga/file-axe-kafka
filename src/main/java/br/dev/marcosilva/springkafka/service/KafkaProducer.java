package br.dev.marcosilva.springkafka.service;

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

@Service
@Slf4j
@Transactional(transactionManager = "chainedTransactionManager")
public class KafkaProducer {
    @Autowired
    KafkaProperties kafkaProperties;

    private final KafkaTemplate<String, String> kafkaStringTemplate;
    private final KafkaTemplate<String, SampleObject> kafkaObjectTemplate;
    private final KafkaTemplate<String, MetaFile> kafkaFileTemplate;

    @Autowired
    public KafkaProducer(
            final KafkaTemplate<String, String> kafkaStringTemplate,
            final KafkaTemplate<String, SampleObject> kafkaObjectTemplate,
            final KafkaTemplate<String, MetaFile> kafkaFileTemplate) {
        this.kafkaStringTemplate = kafkaStringTemplate;
        this.kafkaObjectTemplate = kafkaObjectTemplate;
        this.kafkaFileTemplate = kafkaFileTemplate;
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

    public void sendStringKeyAndFileContainer(String key, MetaFile fileContainer) {
        ListenableFuture<SendResult<String, MetaFile>> future = kafkaFileTemplate.send(
                kafkaProperties.getFileUploads(), key, fileContainer);
        future.addCallback(new ListenableFutureCallback<SendResult<String, MetaFile>>() {

            @Override
            public void onSuccess(SendResult<String, MetaFile> result) {
                log.info("Sent message=[" + fileContainer + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=[" + fileContainer + "] due to : " + ex.getMessage());
            }
        });
    }


}
