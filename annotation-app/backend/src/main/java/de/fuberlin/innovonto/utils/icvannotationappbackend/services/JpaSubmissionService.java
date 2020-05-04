package de.fuberlin.innovonto.utils.icvannotationappbackend.services;

import de.fuberlin.innovonto.utils.batchmanager.api.SubmissionService;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.MturkAnnotationSession;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.MturkAnnotationSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaSubmissionService implements SubmissionService<MturkAnnotationSession> {
    private final MturkAnnotationSessionRepository mturkAnnotationSessionRepository;

    @Autowired
    public JpaSubmissionService(MturkAnnotationSessionRepository mturkAnnotationSessionRepository) {
        this.mturkAnnotationSessionRepository = mturkAnnotationSessionRepository;
    }

    @Override
    public Optional<MturkAnnotationSession> findByHitIdAndWorkerIdAndAssignmentId(String hitId, String workerId, String assignmentId) {
        return mturkAnnotationSessionRepository.findByHitIdAndWorkerIdAndAssignmentId(hitId, workerId, assignmentId);
    }

    @Override
    public MturkAnnotationSession save(MturkAnnotationSession submission) {
        return mturkAnnotationSessionRepository.save(submission);
    }
}
