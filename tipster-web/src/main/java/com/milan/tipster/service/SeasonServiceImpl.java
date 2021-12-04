package com.milan.tipster.service;

import com.milan.tipster.model.enums.ESeason;
import com.milan.tipster.util.Utils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class SeasonServiceImpl implements SeasonService {
    @Override
    public ESeason determineSeason(LocalDateTime gamePlayedOn) {
        if (gamePlayedOn.isAfter(Utils.convertStringToLocalDateTime("01-07-21-00-00-00", null))
                && gamePlayedOn.isBefore(Utils.convertStringToLocalDateTime("30-06-22-00-00-00", null))
        ) {
            return ESeason._2021_2022;
        }
        if (gamePlayedOn.isAfter(Utils.convertStringToLocalDateTime("01-07-20-00-00-00", null))
                && gamePlayedOn.isBefore(Utils.convertStringToLocalDateTime("30-06-21-00-00-00", null))
        ) {
            return ESeason._2020_2021;
        }
        if (gamePlayedOn.isAfter(Utils.convertStringToLocalDateTime("01-07-19-00-00-00", null))
                && gamePlayedOn.isBefore(Utils.convertStringToLocalDateTime("30-06-20-00-00-00", null))
        ) {
            return ESeason._2019_2020;
        } else if (gamePlayedOn.isAfter(Utils.convertStringToLocalDateTime("01-07-18-00-00-00", null))
                && gamePlayedOn.isBefore(Utils.convertStringToLocalDateTime("30-06-19-00-00-00", null))) {
            return ESeason._2018_2019;
        } else {
            // default
            return ESeason._2021_2022;
        }
    }

    @Override
    public ESeason findSeasonByYear(String year) {
        Objects.requireNonNull(year, "Year!");
    //     format: 2019/2020
        if (year.contains("/")) {
            String[] yearParts = year.split("/");
            year = yearParts[1];
        }
        return ESeason.get(year);
    }
}
