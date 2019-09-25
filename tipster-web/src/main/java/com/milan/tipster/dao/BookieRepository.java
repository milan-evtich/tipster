package com.milan.tipster.dao;

import com.milan.tipster.model.Bookie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface BookieRepository extends CrudRepository<Bookie, Long> {
    Bookie findByName(String name);
}
