package com.milan.tipster.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Source {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sourceId;

    private String name;
    private String url;
    private Double score;
    private Long rank;
}
