package br.dev.marcosilva.springkafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MetaFile {
    private String name;
    private Long size;

}
