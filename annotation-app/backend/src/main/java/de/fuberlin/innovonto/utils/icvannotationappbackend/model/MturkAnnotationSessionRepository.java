package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import de.fuberlin.innovonto.utils.batchmanager.api.SubmissionState;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface MturkAnnotationSessionRepository extends CrudRepository<MturkAnnotationSession, UUID> {
    Optional<MturkAnnotationSession> findByAssignmentId(String assignmentId);
    Iterable<MturkAnnotationSession> findAllByProjectIdAndSubmissionState(String annotationProjectId, SubmissionState submissionState);
    Optional<MturkAnnotationSession> findByHitIdAndWorkerIdAndAssignmentId(String hitId, String workerId, String assignmentId);
}
