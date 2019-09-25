package com.milan.tipster.dao;

import com.milan.tipster.model.Association;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface AssociationRepository extends CrudRepository<Association, Long> {
}
