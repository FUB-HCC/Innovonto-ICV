package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import de.fuberlin.innovonto.utils.batchmanager.api.Batch;
import de.fuberlin.innovonto.utils.batchmanager.api.BatchState;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class AnnotationBatch implements Batch {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "de.fuberlin.innovonto.utils.common.FallbackUUIDGenerator"
    )
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime lastPublished;
    private LocalDateTime submitted;

    @Enumerated(EnumType.STRING)
    private BatchState batchState = BatchState.UNALLOCATED;

    private String hitId;
    private String workerId;
    private String assignmentId;

    @ElementCollection
    private List<UUID> submissionIds;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Idea> ideas;

    //hibernate
    public AnnotationBatch() {
        this.created = LocalDateTime.now();
    }

    @Override
    public UUID getId() {
        return id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastPublished() {
        return lastPublished;
    }

    public void setLastPublished(LocalDateTime lastPublished) {
        this.lastPublished = lastPublished;
    }

    public LocalDateTime getSubmitted() {
        return submitted;
    }

    public void setSubmitted(LocalDateTime submitted) {
        this.submitted = submitted;
    }

    public BatchState getBatchState() {
        return batchState;
    }

    public void setBatchState(BatchState batchState) {
        this.batchState = batchState;
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

    public List<Idea> getIdeas() {
        return ideas;
    }

    public void setIdeas(List<Idea> ideas) {
        this.ideas = ideas;
    }

    public String getHWA() {
        return hitId + "|" + workerId + "|" + assignmentId;
    }

    @Override
    public void addSubmissionId(UUID submissionId) {
        if (this.submissionIds == null) {
            this.submissionIds = new ArrayList<>();
        }
        this.submissionIds.add(submissionId);
    }

    @Override
    public List<UUID> getSubmissionIds() {
        return submissionIds;
    }
}
