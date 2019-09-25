package com.milan.tipster.dao;

import com.milan.tipster.model.Source;
import org.springframework.data.repository.CrudRepository;

public interface SourceRepository extends CrudRepository<Source, Long> {
    Source findByName(String name);
}
