package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnnotationBatchRepository extends CrudRepository<AnnotationBatch, UUID> {
    Optional<AnnotationBatch> findByAssignmentId(String assignmentId);
    Optional<AnnotationBatch> findByHitIdAndWorkerIdAndAssignmentId(String hitId, String workerId, String assignmentId);

    //TODO find by result?
}
