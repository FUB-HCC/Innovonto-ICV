package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import de.fuberlin.innovonto.utils.batchmanager.api.SubmissionState;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class IdeaAnnotation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "de.fuberlin.innovonto.utils.common.FallbackUUIDGenerator"
    )
    private UUID id;

    @ManyToOne
    private Idea sourceIdea;

    @Column(length = 2_000)
    private String hitId;
    @Column(length = 2_000)
    private String workerId;
    @Column(length = 2_000)
    private String assignmentId;
    @Column(length = 2_000)
    private String projectId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SemanticAnnotation> annotations;

    @Enumerated(EnumType.STRING)
    private SubmissionState submissionState;

    public UUID getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public Idea getSourceIdea() {
        return sourceIdea;
    }

    public void setSourceIdea(Idea sourceIdea) {
        this.sourceIdea = sourceIdea;
    }

    public String getHitId() {
        return hitId;
    }

    public void setHitId(String hitId) {
        this.hitId = hitId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public List<SemanticAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<SemanticAnnotation> annotations) {
        this.annotations = annotations;
    }

    public String getHWA() {
        return hitId + "|" + workerId + "|" + assignmentId;
    }

    public SubmissionState getSubmissionState() {
        return submissionState;
    }

    public void setSubmissionState(SubmissionState submissionState) {
        this.submissionState = submissionState;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
