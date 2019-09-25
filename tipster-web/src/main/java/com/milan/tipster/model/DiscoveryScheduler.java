package com.milan.tipster.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
public class DiscoveryScheduler {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long discoverySchedulerId;
    private String code;
    private String name;
    private Boolean active;
    private LocalDate lastMatchDay;
}
