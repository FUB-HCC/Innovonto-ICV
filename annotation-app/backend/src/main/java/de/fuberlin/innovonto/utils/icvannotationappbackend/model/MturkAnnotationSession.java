package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class MturkAnnotationSession {
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
    private String fulltextFeedback;
    private int clarityRating;

    private boolean passedAttentionCheck = false;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus = ReviewStatus.UNREVIEWED;

    @OneToMany(cascade = CascadeType.ALL)
    private List<IdeaAnnotation> annotatedIdeas;

    //Hibernate
    public MturkAnnotationSession() {
    }

    public UUID getId() {
        return id;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
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
