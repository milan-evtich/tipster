package com.milan.tipster.service.impl;

import com.milan.tipster.dao.DiscoverySchedulerRepository;
import com.milan.tipster.model.DiscoveryScheduler;
import com.milan.tipster.service.GameDiscoveryService;
import com.milan.tipster.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class GameDiscoveryServiceImpl implements GameDiscoveryService {

    @Autowired
    private DiscoverySchedulerRepository discoverySchedulerRepository;

    @Override
    public void discoverAllGames() {

        DiscoveryScheduler discoveryScheduler = discoverySchedulerRepository.findByCode(Constants.DEFAULT_GAME_DISCOVERY_SCHEDULER);
        if (discoveryScheduler.getActive()) {
            LocalDate today = LocalDate.now();
            while (!discoveryScheduler.getLastMatchDay().isEqual(today)) {
                log.info("TEST_COUNT");
                fetchMatchDay(discoveryScheduler);
            }
        }
    }

    private void fetchMatchDay(DiscoveryScheduler discoveryScheduler) {
        // TODO
        discoveryScheduler.setLastMatchDay(discoveryScheduler.getLastMatchDay().plusDays(1L));
    }

}
