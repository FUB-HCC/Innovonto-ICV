package de.fuberlin.innovonto.utils.icvannotationappbackend.services;

import de.fuberlin.innovonto.utils.batchmanager.api.BatchService;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationBatch;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaBatchService implements BatchService<AnnotationBatch> {
    private final AnnotationBatchRepository annotationBatchRepository;

    @Autowired
    public JpaBatchService(AnnotationBatchRepository annotationBatchRepository) {
        this.annotationBatchRepository = annotationBatchRepository;
    }

    @Override
    public Optional<AnnotationBatch> findByHitIdAndWorkerIdAndAssignmentId(String hitId, String workerId, String assignmentId) {
        return annotationBatchRepository.findByHitIdAndWorkerIdAndAssignmentId(hitId, workerId, assignmentId);
    }

    @Override
    public Optional<AnnotationBatch> findByAssignmentId(String assignmentId) {
        return annotationBatchRepository.findByAssignmentId(assignmentId);
    }

    @Override
    public AnnotationBatch save(AnnotationBatch batch) {
        return annotationBatchRepository.save(batch);
    }
}
