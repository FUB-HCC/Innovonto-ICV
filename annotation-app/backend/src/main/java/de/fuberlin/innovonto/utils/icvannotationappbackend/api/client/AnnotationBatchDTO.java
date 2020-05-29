package de.fuberlin.innovonto.utils.icvannotationappbackend.api.client;

import de.fuberlin.innovonto.utils.icvannotationappbackend.model.Challenge;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.Idea;

import java.util.List;
import java.util.Map;

public class AnnotationBatchDTO {
    private final int estimatedTimeInMinutes;
    private final double compensation;
    private final int batchSize;
    private final Map<String, Challenge> challenges;
    private final List<Idea> ideas;

    public AnnotationBatchDTO(Map<String, Challenge> challenges, List<Idea> ideas,
                              int estimatedTimeInMinutes, double compensation, int batchSize) {
        this.challenges = challenges;
        this.ideas = ideas;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.compensation = compensation;
        this.batchSize = batchSize;
    }

    public Map<String, Challenge> getChallenges() {
        return challenges;
    }

    public List<Idea> getIdeas() {
        return ideas;
    }

    public int getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public double getCompensation() {
        return compensation;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
