package com.milan.tipster.mapper;

import com.milan.tipster.dto.CompetitionDto;
import com.milan.tipster.dto.PredictionTipDto;
import com.milan.tipster.dto.RatingDto;
import com.milan.tipster.dto.ShortTipDto;
import com.milan.tipster.dto.TipSDto;
import com.milan.tipster.dto.TipmanCompetitionDto;
import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Rating;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.TipmanCompetitionRating;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static org.apache.commons.lang3.StringUtils.defaultString;

@Component
public class TipToPredictionOrikaMapper extends AbstractOrikaMapper<PredictionTipDto, Tip>{

    public TipToPredictionOrikaMapper(MapperFactory globalMapperFactory) {
        super(globalMapperFactory);
    }

    @PostConstruct
    public void preConfigure() {
        this.configure(super.getMapperFactory());
    }

    @Override
    public void configure(MapperFactory factory) {

        // TipmanCompetitionRating <--> TipmanCompetitionDto
        factory.classMap(TipmanCompetitionRating.class, TipmanCompetitionDto.class)
                .field("tipman.tipmanId", "tipmanId")
                .field("competition.competitionId", "competitionId")
//                .fieldMap(DESCRIPTION, DESCRIPTION).mapNulls(true).add()
                .byDefault()
                .register();


        // Rating <--> RatingDto
        factory.classMap(Rating.class, RatingDto.class)
                .byDefault()
                .register();

        // Competition <--> CompetitionDto
        factory.classMap(Competition.class, CompetitionDto.class)
                .byDefault()
                .register();

        // Competition <--> CompetitionDto
        factory.classMap(PredictionTipDto.class, ShortTipDto.class)
                .field("game.playedOn", "playedOn")
                .field("game.link", "link")
                .field("game.nameGr", "nameGr")
                .byDefault()
                .register();

        factory.classMap(Tip.class, TipSDto.class)
                .field("game.playedOn", "playedOn")
                .byDefault()
                .register();

    }


}
