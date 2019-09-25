package com.milan.tipster.dao;

import com.milan.tipster.model.Sport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface SportRepository extends CrudRepository<Sport, Long> {
}
