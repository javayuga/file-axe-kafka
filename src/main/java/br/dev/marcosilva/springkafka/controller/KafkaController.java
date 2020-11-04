package br.dev.marcosilva.springkafka.controller;

import br.dev.marcosilva.springkafka.dto.SampleObject;
import br.dev.marcosilva.springkafka.service.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/spring-kafka")
@Slf4j
public class KafkaController {
    public KafkaProducer kafkaProducer;

    public KafkaController(KafkaProducer kafkaProducer){
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping(path="send/string/{key}/{value}")
    public ResponseEntity<Void> sendStringKeyAndValue(
            @PathVariable(name="key", required=true) String key,
            @PathVariable(name="value", required=true) String value){

        try{
            kafkaProducer.sendStringKeyAndValue(key, value);

        }catch (Exception e){
            return ResponseEntity.badRequest().build();

        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(path="send/string/{key}")
    public ResponseEntity<Void> sendStringKeyAndObjectValue(
            @PathVariable(name="key", required=true) String key,
            @Valid @RequestBody(required = true) SampleObject object){
        try{
            kafkaProducer.sendStringKeyAndObjectValue(key, object);

        }catch (Exception e){
            log.error("exception while sending object value", e);
            return ResponseEntity.badRequest().build();

        }
        return ResponseEntity.ok().build();
    }

}
