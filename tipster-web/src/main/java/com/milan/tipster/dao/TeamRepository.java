package com.milan.tipster.dao;

import com.milan.tipster.model.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface TeamRepository extends CrudRepository<Team, Long> {

    Team findByNameGr(String nameGr);

    Team findByCode(String code);

}
