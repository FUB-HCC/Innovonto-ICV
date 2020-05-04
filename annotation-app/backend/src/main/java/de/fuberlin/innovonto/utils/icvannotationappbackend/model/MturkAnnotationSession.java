package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import de.fuberlin.innovonto.utils.batchmanager.api.Submission;
import de.fuberlin.innovonto.utils.batchmanager.api.SubmissionState;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class MturkAnnotationSession implements Submission {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "de.fuberlin.innovonto.utils.common.FallbackUUIDGenerator"
    )
    private UUID id;
    //Metadata
    //TODO accepted
    private LocalDateTime submitted;
    private LocalDateTime reviewed;

    //General:
    @NotBlank
    private String hitId;
    @NotBlank
    private String workerId;
    @NotBlank
    private String assignmentId;
    @NotBlank
    private String annotationProjectId;

    //Survey
    //TODO survey.

    @Enumerated(EnumType.STRING)
    private SubmissionState submissionState = SubmissionState.UNREVIEWED;

    @OneToMany(cascade = CascadeType.ALL)
    private List<IdeaAnnotation> annotatedIdeas;

    //Hibernate
    public MturkAnnotationSession() {
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String getHitId() {
        return hitId;
    }

    @Override
    public void setHitId(String hitId) {
        this.hitId = hitId;
    }

    @Override
    public String getWorkerId() {
        return workerId;
    }

    @Override
    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    @Override
    public String getAssignmentId() {
        return assignmentId;
    }

    @Override
    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    @Override
    public String getProjectId() {
        return annotationProjectId;
    }

    @Override
    public String getHWA() {
        return hitId + "|" + workerId + "|" + assignmentId;
    }

    public SubmissionState getSubmissionState() {
        return submissionState;
    }

    public void setSubmissionState(SubmissionState submissionState) {
        this.submissionState = submissionState;
    }

    public void setReviewed(LocalDateTime reviewed) {
        this.reviewed = reviewed;
    }

    public List<IdeaAnnotation> getAnnotatedIdeas() {
        return annotatedIdeas;
    }

    public void setAnnotatedIdeas(List<IdeaAnnotation> annotatedIdeas) {
        this.annotatedIdeas = annotatedIdeas;
    }
}
