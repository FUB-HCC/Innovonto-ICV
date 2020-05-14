package de.fuberlin.innovonto.utils.icvannotationappbackend.management;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.Challenge;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.Idea;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationProjectRequirements {
    private String id;
    private List<Challenge> challenges;
    private List<Idea> ideas;
    private int goalAnnotationsPerIdea;
    private int batchSize;
    private int estimatedTimeInMinutes;
    private double compensation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public List<Idea> getIdeas() {
        return ideas;
    }

    public void setIdeas(List<Idea> ideas) {
        this.ideas = ideas;
    }

    public int getGoalAnnotationsPerIdea() {
        return goalAnnotationsPerIdea;
    }

    public void setGoalAnnotationsPerIdea(int goalAnnotationsPerIdea) {
        this.goalAnnotationsPerIdea = goalAnnotationsPerIdea;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public void setEstimatedTimeInMinutes(int estimatedTimeInMinutes) {
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    }

    public double getCompensation() {
        return compensation;
    }

    public void setCompensation(double compensation) {
        this.compensation = compensation;
    }
}
