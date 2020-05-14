package de.fuberlin.innovonto.utils.icvannotationappbackend.api.client;

import de.fuberlin.innovonto.utils.icvannotationappbackend.model.Challenge;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.Idea;

import java.util.List;
import java.util.Map;

public class AnnotationBatchDTO {
    private Map<String, Challenge> challenges;
    private List<Idea> ideas;

    public AnnotationBatchDTO(Map<String, Challenge> challenges, List<Idea> ideas) {
        this.challenges = challenges;
        this.ideas = ideas;
    }

    public Map<String, Challenge> getChallenges() {
        return challenges;
    }

    public List<Idea> getIdeas() {
        return ideas;
    }
}
