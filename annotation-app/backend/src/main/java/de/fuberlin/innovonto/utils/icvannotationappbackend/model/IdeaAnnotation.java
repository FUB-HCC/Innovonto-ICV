package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class IdeaAnnotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Idea sourceIdea;

    @Column(length = 2_000)
    private String hitId;
    @Column(length = 2_000)
    private String workerId;
    @Column(length = 2_000)
    private String assignmentId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SemanticAnnotation> annotations;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus = ReviewStatus.UNREVIEWED;

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}
