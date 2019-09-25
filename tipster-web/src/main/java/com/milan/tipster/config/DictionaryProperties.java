package com.milan.tipster.config;

import com.milan.tipster.model.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "dictionary")
public class DictionaryProperties {
    // countries
    private Boolean countryImport;
    private List<Country> countries = new ArrayList<>();

    // sports
    private Boolean sportImport;
    private List<Sport> sports = new ArrayList<>();

    // associations
    private Boolean associationImport;
    private List<Association> associations = new ArrayList<>();

    // competitions and teams
    private Boolean competitionImport;
    private List<Competition> competitions = new ArrayList<>();

    // discovery scheduler
    private Boolean discoverySchedulerImport;
    private DiscoveryScheduler defaultDiscoveryScheduler;

    // bookies
    private Boolean bookieImport;
    private List<Bookie> bookies = new ArrayList<>();
}
