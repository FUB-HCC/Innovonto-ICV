package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import de.fuberlin.innovonto.utils.batchmanager.api.Submission;
import de.fuberlin.innovonto.utils.batchmanager.api.SubmissionState;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class IdeaAnnotation implements Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    public Idea getSourceIdea() {
        return sourceIdea;
    }

    public void setSourceIdea(Idea sourceIdea) {
        this.sourceIdea = sourceIdea;
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

    public List<SemanticAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<SemanticAnnotation> annotations) {
        this.annotations = annotations;
    }

    @Override
    public String getHWA() {
        return hitId + "|" + workerId + "|" + assignmentId;
    }

    @Override
    public SubmissionState getSubmissionState() {
        return submissionState;
    }

    public void setSubmissionState(SubmissionState submissionState) {
        this.submissionState = submissionState;
    }
}
