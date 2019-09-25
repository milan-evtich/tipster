package com.milan.tipster.dao;

import com.milan.tipster.model.DiscoveryScheduler;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface DiscoverySchedulerRepository extends CrudRepository<DiscoveryScheduler, Long> {

    DiscoveryScheduler findByCode(String code);

}
