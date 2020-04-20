package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface BatchRepository  extends CrudRepository<Batch, UUID> {
    Optional<Batch> findByAssignmentId(String assignmentId);
}
