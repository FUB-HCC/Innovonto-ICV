package de.fuberlin.innovonto.utils.icvannotationappbackend.api;

import de.fuberlin.innovonto.utils.icvannotationappbackend.management.AnnotationRequirements;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.*;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/management/")
@CrossOrigin(origins = {"http://localhost:8004", "http://localhost:9500", "https://i2m-research.imp.fu-berlin.de"})
public class ManagementRestController {
    private final AnnotationProjectRepository annotationProjectRepository;
    private final BatchRepository batchRepository;
    private final MturkAnnotationSessionRepository mturkAnnotationSessionRepository;

    @Autowired
    public ManagementRestController(AnnotationProjectRepository annotationProjectRepository, BatchRepository batchRepository, MturkAnnotationSessionRepository mturkAnnotationSessionRepository) {
        this.annotationProjectRepository = annotationProjectRepository;
        this.batchRepository = batchRepository;
        this.mturkAnnotationSessionRepository = mturkAnnotationSessionRepository;
    }

    //Upload Requirements
    @PostMapping("/requirements/")
    @CrossOrigin(
            origins = "*",
            allowedHeaders = "*",
            methods = {RequestMethod.GET, RequestMethod.POST})
    public AnnotationProject uploadRequirements(@RequestBody AnnotationRequirements requirements) {
        //TODO implement.
        return new AnnotationProject();
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
    public Optional<MturkAnnotationSession> getByAssignmentId(@RequestParam String assignmentId) {
        return mturkAnnotationSessionRepository.findByAssignmentId(assignmentId);
    }


    @GetMapping("/mturkAnnotationSessions/{assignmentId}/set-usable")
    public MturkAnnotationSession setUsable(@PathVariable String assignmentId) throws NotFoundException {
        //TODO what to do if there are multiple sessions for an assignmentId?
        Optional<MturkAnnotationSession> byId = mturkAnnotationSessionRepository.findByAssignmentId(assignmentId);
        if (byId.isEmpty()) {
            throw new NotFoundException("Could not find session with assignment: " + assignmentId);
        } else {
            final MturkAnnotationSession session = byId.get();
            session.setReviewStatus(ReviewStatus.USABLE);
            session.setReviewed(LocalDateTime.now());
            for (IdeaAnnotation annotation : session.getAnnotatedIdeas()) {
                annotation.setReviewStatus(ReviewStatus.USABLE);
            }
            return mturkAnnotationSessionRepository.save(session);
        }
    }

    @GetMapping("/mturkRatingSessions/{assignmentId}/set-unusable")
    public MturkAnnotationSession setUnusable(@PathVariable String assignmentId) throws NotFoundException {
        Optional<MturkAnnotationSession> byId = mturkAnnotationSessionRepository.findByAssignmentId(assignmentId);
        //TODO what to do if there are multiple sessions for an assignmentId?
        if (byId.isEmpty()) {
            throw new NotFoundException("Could not find session with id: " + assignmentId);
        } else {
            final MturkAnnotationSession session = byId.get();
            Optional<Batch> byResultId = batchRepository.findByResultsAnnotationSessionId(session.getId());
            if (byResultId.isEmpty()) {
                throw new NotFoundException("Could not find source batch for session with id: " + session.getId());
            } else {
                //TODO reset-assignmentId
                session.setReviewStatus(ReviewStatus.UNUSABLE);
                session.setReviewed(LocalDateTime.now());
                for (IdeaAnnotation annotation : session.getAnnotatedIdeas()) {
                    annotation.setReviewStatus(ReviewStatus.UNUSABLE);
                }

                final Batch sourceBatch = byResultId.get();
                sourceBatch.setSubmitted(null);
                sourceBatch.setBatchState(BatchState.UNALLOCATED);
                sourceBatch.setHitId(null);
                sourceBatch.setWorkerId(null);
                sourceBatch.setAssignmentId(null);
                sourceBatch.setResultsAnnotationSessionId(null);
                batchRepository.save(sourceBatch);

                return mturkAnnotationSessionRepository.save(session);
            }
        }
    }

}
