package com.lukdut.monitoring.backend.repository;

import com.lukdut.monitoring.backend.model.Command;
import org.springframework.data.repository.CrudRepository;

public interface CommandRepository extends CrudRepository<Command, Long> {
}
