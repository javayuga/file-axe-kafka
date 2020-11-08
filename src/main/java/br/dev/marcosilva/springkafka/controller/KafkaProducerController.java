package br.dev.marcosilva.springkafka.controller;

import br.dev.marcosilva.fileaxe.storage.interfaces.StorageFacade;
import br.dev.marcosilva.springkafka.dto.MetaFile;
import br.dev.marcosilva.springkafka.dto.SampleObject;
import br.dev.marcosilva.springkafka.service.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(value = "/v1/spring-kafka/producer")
@Slf4j
public class KafkaProducerController {

    private final KafkaProducer kafkaProducer;
    private final StorageFacade storageFacade;

    @Autowired
    public KafkaProducerController(KafkaProducer kafkaProducer, StorageFacade storageFacade){
        this.kafkaProducer = kafkaProducer;
        this.storageFacade = storageFacade;
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

    @PostMapping(path="send/object/{key}")
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

    @PostMapping(
            path = "send/file/{key}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> sendStringKeyAndFileContent(
            @PathVariable(name="key", required=true) String key,
            @RequestPart(value = "file") final MultipartFile file){
        try{
            storageFacade.store(file);
            kafkaProducer.sendStringKeyAndFileContainer(
                    key.concat("_").concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-yy_HH:mm:ss"))),
                    new MetaFile(file.getOriginalFilename(), file.getBytes(), (long) file.getBytes().length));

        }catch (Exception e){
            log.error("exception while sending object value", e);
            return ResponseEntity.badRequest().build();

        }
        return ResponseEntity.ok().build();
    }

}
