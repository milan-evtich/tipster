package com.milan.tipster.dto;

import com.milan.tipster.model.enums.ECompetitionType;
import lombok.Data;

@Data
public class CompetitionDto {

//    private String code;
    private String nameGr;
    private Long rank;
    private RatingDto rating;
    private Long competitionId;
//    private ECompetitionType type;
//    private CountryDto country;

}
