package br.dev.marcosilva.springkafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class SampleObject {
    private String account;
    private BigDecimal value;
    private Boolean paid;

}
