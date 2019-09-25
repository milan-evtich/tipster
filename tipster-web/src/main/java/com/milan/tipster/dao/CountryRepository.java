package com.milan.tipster.dao;

import com.milan.tipster.model.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface CountryRepository extends CrudRepository<Country, Long> {

    Country findByCode(String code);

}
