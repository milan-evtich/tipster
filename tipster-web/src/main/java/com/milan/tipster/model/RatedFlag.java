package com.milan.tipster.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class RatedFlag {

    private Boolean tipmanRated;
    private Boolean competitionRated;
    private Boolean tcRated;

}
