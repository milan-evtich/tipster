package com.milan.tipster.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum ESeason {

    _2018_2019("2019"),
    _2019_2020("2020"),
    _2020_2021("2021"),
    _2021_2022("2022"),
    OTHER("other");

    //Lookup table
    private static final Map<String, ESeason> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static
    {
        for(ESeason season : ESeason.values())
        {
            lookup.put(season.getYear(), season);
        }
    }

    public static ESeason get(String year)
    {
        return lookup.get(year);
    }

    private final String year;

    ESeason(String year) {
        this.year = year;
    }

    public String getYear() {
        return this.year;
    }

}
