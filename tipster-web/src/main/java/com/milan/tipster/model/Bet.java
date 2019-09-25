package com.milan.tipster.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long betId;

    private String pic; // 1X2
    private Long stake;
    private Double odd;
    private String status;
    private Boolean dnb;

}
