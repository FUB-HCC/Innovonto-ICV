package de.fuberlin.innovonto.utils.icvannotationappbackend.api.client;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class MturkAnnotationSessionResultDTO {

    //MTURK:
    @NotBlank
    private String projectId;
    @NotBlank
    private String hitId;
    @NotBlank
    private String workerId;
    @NotBlank
    private String assignmentId;

    //Survey
    private String fulltextFeedback;
    private int clarityRating;

    private boolean passedAttentionCheck = false;

    //Results
    private List<AnnotatedIdeaDTO> annotatedIdeas;

    private List<TrackingEventDTO> trackingEvents;

    public MturkAnnotationSessionResultDTO() {
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public String getFulltextFeedback() {
        return fulltextFeedback;
    }

    public void setFulltextFeedback(String fulltextFeedback) {
        this.fulltextFeedback = fulltextFeedback;
    }

    public int getClarityRating() {
        return clarityRating;
    }

    public void setClarityRating(int clarityRating) {
        this.clarityRating = clarityRating;
    }

    public boolean isPassedAttentionCheck() {
        return passedAttentionCheck;
    }

    public void setPassedAttentionCheck(boolean passedAttentionCheck) {
        this.passedAttentionCheck = passedAttentionCheck;
    }

    public List<AnnotatedIdeaDTO> getAnnotatedIdeas() {
        return annotatedIdeas;
    }

    public void setAnnotatedIdeas(List<AnnotatedIdeaDTO> annotatedIdeas) {
        this.annotatedIdeas = annotatedIdeas;
    }

    public List<TrackingEventDTO> getTrackingEvents() {
        return trackingEvents;
    }

    public void setTrackingEvents(List<TrackingEventDTO> trackingEvents) {
        this.trackingEvents = trackingEvents;
    }
}
