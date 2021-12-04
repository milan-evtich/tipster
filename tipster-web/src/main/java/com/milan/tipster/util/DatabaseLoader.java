package com.milan.tipster.util;

import com.milan.tipster.config.DictionaryProperties;
import com.milan.tipster.dao.*;
import com.milan.tipster.model.*;
import com.milan.tipster.model.enums.ESeason;
import com.milan.tipster.service.FetchingService;
import com.milan.tipster.service.GameDiscoveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DatabaseLoader implements CommandLineRunner {

    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private SportRepository sportRepository;
    @Autowired
    private BookieRepository bookieRepository;
    @Autowired
    private AssociationRepository associationRepository;
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private DiscoverySchedulerRepository discoverySchedulerRepository;
    @Autowired
    private FetchingService fetchingService;
    @Autowired
    private DictionaryProperties dictionaryProperties;
    @Autowired
    private YamlConfigMaker yamlConfigMaker;

    // services shouldn't be here probably
    @Autowired
    private GameDiscoveryService gameDiscoveryService;


    @Override
    public void run(String... strings) throws Exception {
        createDefaultManagers(false);
        makeAuthentication(true);

        SecurityContextHolder.clearContext();

        /////////////////////////////////
        // DICTIONARIES INITIALIZATION //

        // NOT WORKING SINCE 10.11.2021
//
//        log.info("==== COUNTRIES INITIALIZATION ====");
//        if (dictionaryProperties.getCountryImport() == true && !dictionaryProperties.getCountries().isEmpty()) {
//            for (Country country : dictionaryProperties.getCountries()) {
//                this.countryRepository.save(country);
//                log.info("Country= name:{}, code:{}", country.getName(), country.getCode());
//            }
//        } else {
//            log.info("Country import option turned off in configuration");
//        }
//
//        log.info("==== SPORTS INITIALIZATION ====");
//        if (dictionaryProperties.getSportImport() == true && !dictionaryProperties.getSports().isEmpty()) {
//            for (Sport sport : dictionaryProperties.getSports()) {
//                this.sportRepository.save(sport);
//                log.info("Sport= name:{}, code:{}", sport.getName(), sport.getCode());
//            }
//        } else {
//            log.info("Sport import option turned off in configuration");
//        }
//
//        log.info("==== BOOKIES INITIALIZATION ====");
//        if (dictionaryProperties.getBookieImport() == true && !dictionaryProperties.getBookies().isEmpty()) {
//            for (Bookie bookie : dictionaryProperties.getBookies()) {
//                this.bookieRepository.save(bookie);
//                log.info("Bookie= name:{}, code:{}", bookie.getName(), bookie.getCode());
//            }
//        } else {
//            log.info("Bookie import option turned off in configuration");
//        }
//
//        log.info("==== ASSOCIATIONS INITIALIZATION ====");
//        if (dictionaryProperties.getAssociationImport() == true && !dictionaryProperties.getAssociations().isEmpty()) {
//            for (Association association : dictionaryProperties.getAssociations()) {
//                this.associationRepository.save(association);
//                log.info("Association= name:{}, code:{}", association.getName(), association.getCode());
//            }
//        } else {
//            log.info("Association import option turned off in configuration");
//        }
//
//        log.info("==== COMPETITIONS AND TEAMS INITIALIZATION ====");
//        if (dictionaryProperties.getCompetitionImport() == true && !dictionaryProperties.getCompetitions().isEmpty()) {
//            for (Competition competition : dictionaryProperties.getCompetitions()) {
//                competition.setSeason(ESeason._2018_2019);
//                List<Team> teams = new ArrayList<>();
//                log.info(competition.getTeams().toString());
///*
//                // TEAMS should get the teams from config-competition-and-teams.yml file
//                if (competition.getType() == ECompetitionType.LEAGUE) {
//                    teams = fetchingService.fetchTeams("/leagues/" +  competition.getCode()  + "/");
//                    for (Team team : teams) {
//                        team.getCompetitions().add(competition);
//                        competition.getTeams().add(team);
//                    }
//                }
//*/
//                this.competitionRepository.save(competition);
//                log.info("Competition= name:{},season:{}, nameGr: {}, code:{}, type:{}", competition.getName(), competition.getSeason(),
//                        competition.getNameGr(), competition.getCode(), competition.getType().name());
//                if (!teams.isEmpty()) {
//                    log.info("Teams: {}", teams);
//                }
//            }
//            yamlConfigMaker.writeConfig("config-competitions.yaml", dictionaryProperties.getCompetitions());
//        } else {
//            log.info("Competition and team import option turned off in configuration");
//        }
//
//        log.info("==== DISCOVERY SCHEDULER ====");
//        if (dictionaryProperties.getDiscoverySchedulerImport() == true) {
//            DiscoveryScheduler defaultDiscoveryScheduler = dictionaryProperties.getDefaultDiscoveryScheduler();
//            if (defaultDiscoveryScheduler != null) {
//                log.info("Default discovery scheduler: {}", defaultDiscoveryScheduler);
//                discoverySchedulerRepository.save(defaultDiscoveryScheduler);
//            }
//        } else {
//            log.info("Default discovery scheduler import option turned off in configuration");
//        }

//        log.info("==== GAMES DISCOVERY ====");
//        gameDiscoveryService.discoverAllGames();

        //                              //
        //////////////////////////////////

        //        this.fetchingService.logLinks("/");


    }

    private void makeAuthentication(boolean should) {
        if (should) {
            // AUTHENTICATION
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("Milan", "doesn't matter",
                            AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("Anastasia", "doesn't matter",
                            AuthorityUtils.createAuthorityList("ROLE_MANAGER")));
        }
    }

    private void createDefaultManagers(boolean should) {
        if (should) {
            // MANAGERS
            Manager milan = this.managerRepository.save(new Manager("Milan", "123",
                    "ROLE_MANAGER"));
            Manager anastasia = this.managerRepository.save(new Manager("Anastasia", "123",
                    "ROLE_MANAGER"));
        }
    }
}