package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import de.fuberlin.innovonto.utils.batchmanager.api.Batch;
import de.fuberlin.innovonto.utils.batchmanager.api.BatchState;
import de.fuberlin.innovonto.utils.batchmanager.api.Project;
import de.fuberlin.innovonto.utils.batchmanager.api.Submission;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AnnotationProject implements Project {
    @Id
    private String id;

    private LocalDateTime created;

    //TODO include metadata.

    @OneToMany(cascade = CascadeType.ALL)
    private List<AnnotationBatch> batches;

    @OneToMany(cascade = CascadeType.ALL)
    private List<MturkAnnotationSession> submissions;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<Batch> getBatches() {
        return new ArrayList<>(batches);
    }

    public void setBatches(List<AnnotationBatch> batches) {
        this.batches = batches;
    }

    public List<MturkAnnotationSession> getSubmissions() {
        return submissions;
    }

    public void addSubmission(Submission submission) {
        if (submissions == null) {
            submissions = new ArrayList<>();
        }
        if (submission instanceof MturkAnnotationSession) {
            submissions.add((MturkAnnotationSession) submission);
        } else {
            throw new IllegalArgumentException("Wrong subclass of submission: " + submission);
        }
    }

    @Override
    public long getNumberOfBatches() {
        return this.batches.size();
    }

    @Override
    public long getNumberOfBatchesInProgress() {
        return batches.stream().filter((b) -> b.getBatchState().equals(BatchState.ALLOCATED)).count();
    }

    @Override
    public long getNumberOfBatchesSubmitted() {
        return batches.stream().filter((b) -> b.getBatchState().equals(BatchState.SUBMITTED)).count();
    }
}
