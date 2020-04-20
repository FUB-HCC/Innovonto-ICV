package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface MturkAnnotationSessionRepository extends CrudRepository<MturkAnnotationSession, UUID> {
    Optional<MturkAnnotationSession> findByAssignmentId(String assignmentId);
    Iterable<MturkAnnotationSession> findAllByAnnotationProjectIdAndReviewStatus(String annotationProjectId, ReviewStatus reviewStatus);
}
