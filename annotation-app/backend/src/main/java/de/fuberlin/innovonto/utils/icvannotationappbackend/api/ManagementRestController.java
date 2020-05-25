package de.fuberlin.innovonto.utils.icvannotationappbackend.api;

import de.fuberlin.innovonto.utils.batchmanager.api.BatchState;
import de.fuberlin.innovonto.utils.batchmanager.api.SubmissionState;
import de.fuberlin.innovonto.utils.icvannotationappbackend.management.AnnotationProjectRequirements;
import de.fuberlin.innovonto.utils.icvannotationappbackend.management.RequirementsImporter;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.*;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/management/")
@CrossOrigin(origins = {"http://localhost:9002", "http://localhost:9500", "https://i2m-research.imp.fu-berlin.de"})
public class ManagementRestController {
    private final AnnotationProjectRepository annotationProjectRepository;
    private final AnnotationBatchRepository batchRepository;
    private final MturkAnnotationSessionRepository mturkAnnotationSessionRepository;
    private final RequirementsImporter requirementsImporter;

    @Autowired
    public ManagementRestController(AnnotationProjectRepository annotationProjectRepository, AnnotationBatchRepository batchRepository, MturkAnnotationSessionRepository mturkAnnotationSessionRepository, RequirementsImporter requirementsImporter) {
        this.annotationProjectRepository = annotationProjectRepository;
        this.batchRepository = batchRepository;
        this.mturkAnnotationSessionRepository = mturkAnnotationSessionRepository;
        this.requirementsImporter = requirementsImporter;
    }

    //Upload Requirements
    @PostMapping("/requirements/")
    @CrossOrigin(
            origins = "*",
            allowedHeaders = "*",
            methods = {RequestMethod.GET, RequestMethod.POST})
    public AnnotationProject uploadRequirements(@RequestBody AnnotationProjectRequirements requirements) {
        requirementsImporter.validateRequirements(requirements);
        return requirementsImporter.saveRequirementsAsProject(requirements);
    }

    //See all RatingProjects
    @GetMapping("/annotationProjects/")
    public Iterable<AnnotationProject> getAllRatingProjects() {
        return annotationProjectRepository.findAll();
    }

    //See all RatingProjects
    @GetMapping("/annotationProjects/{id}")
    public Optional<AnnotationProject> getRatingProjectById(@PathVariable String id) {
        return annotationProjectRepository.findById(id);
    }

    //TODO add Display Object for MturkRating Session, to make reviewing a session easier.
    @GetMapping("/mturkAnnotationSessions/byAssignment")
    public Iterable<MturkAnnotationSession> getByAssignmentId(@RequestParam String assignmentId) {
        return mturkAnnotationSessionRepository.findAllByAssignmentIdOrderBySubmittedDesc(assignmentId);
    }


    @GetMapping("/mturkAnnotationSessions/{sessionId}/set-usable")
    public MturkAnnotationSession setUsable(@PathVariable String sessionId) throws NotFoundException {
        Optional<MturkAnnotationSession> byId = mturkAnnotationSessionRepository.findById(UUID.fromString(sessionId));
        if (byId.isEmpty()) {
            throw new NotFoundException("Could not find session with sessionId: " + sessionId);
        } else {
            final MturkAnnotationSession session = byId.get();
            session.setSubmissionState(SubmissionState.USABLE);
            session.setReviewed(LocalDateTime.now());
            for (IdeaAnnotation annotation : session.getAnnotatedIdeas()) {
                annotation.setSubmissionState(SubmissionState.USABLE);
            }
            return mturkAnnotationSessionRepository.save(session);
        }
    }

    @GetMapping("/mturkAnnotationSessions/{sessionId}/set-unusable")
    public MturkAnnotationSession setUnusable(@PathVariable String sessionId) throws NotFoundException {
        Optional<MturkAnnotationSession> byId = mturkAnnotationSessionRepository.findById(UUID.fromString(sessionId));
        if (byId.isEmpty()) {
            throw new NotFoundException("Could not find session with id: " + sessionId);
        } else {
            final MturkAnnotationSession session = byId.get();
            Optional<AnnotationBatch> byResultId = batchRepository.findByAssignmentId(session.getAssignmentId());
            if (byResultId.isEmpty()) {
                throw new NotFoundException("Could not find source batch for session with id: " + session.getId());
            } else {
                session.setSubmissionState(SubmissionState.UNUSABLE);
                session.setReviewed(LocalDateTime.now());
                for (IdeaAnnotation annotation : session.getAnnotatedIdeas()) {
                    annotation.setSubmissionState(SubmissionState.UNUSABLE);
                }

                final AnnotationBatch sourceBatch = byResultId.get();
                sourceBatch.setSubmitted(null);
                sourceBatch.setBatchState(BatchState.UNALLOCATED);
                sourceBatch.setHitId(null);
                sourceBatch.setWorkerId(null);
                sourceBatch.setAssignmentId(null);
                batchRepository.save(sourceBatch);

                return mturkAnnotationSessionRepository.save(session);
            }
        }
    }

}
