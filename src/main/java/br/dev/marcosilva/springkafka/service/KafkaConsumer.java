package br.dev.marcosilva.springkafka.service;

import br.dev.marcosilva.fileaxe.axe.kafka.dto.FileChunkDTO;
import br.dev.marcosilva.springkafka.properties.KafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(transactionManager = "chainedTransactionManager")
public class KafkaConsumer {
    @Autowired
    KafkaProperties kafkaProperties;

    @KafkaListener(
            groupId = "KafkaConsumer",
            topics = "${spring.kafka.stringTopic:null}",
            containerFactory = "retryKafkaListenerContainerFactory")
    public void onStringMessage(final ConsumerRecord<String, String> consumerRecord, final Acknowledgment acknowledgment) {
        log.info("message received at topic {} -> '{}' - {}",
                kafkaProperties.getStringTopic(), consumerRecord.key(), consumerRecord.value());
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            groupId = "KafkaConsumer",
            topics = "${spring.kafka.fileChunks:null}",
            containerFactory = "retryKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.consumer.concurrency:3}"
            )
    public void onFileChunkRecord(
            final ConsumerRecord<String, FileChunkDTO> consumerRecord, final Acknowledgment acknowledgment) throws InterruptedException {

        int simulatedInterval = new Float(Math.random()*4000).intValue();
        Thread.sleep(simulatedInterval);
        log.info("received message at topic {} {} {} {}",
                consumerRecord.key(),
                consumerRecord.value().getSequential(),
                simulatedInterval,
                consumerRecord.partition());

        acknowledgment.acknowledge();
    }

}
