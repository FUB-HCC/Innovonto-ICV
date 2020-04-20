package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import javax.persistence.*;
import java.util.List;

/*
{
      "offset": 0,
      "resource_candidates": [...],
      "text": "problem",
}
*/
@Entity
public class SemanticAnnotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //TODO state: rejected if ResourceCandidate.filterBy(selected) -> empty List
    @Column(length = 2_000)
    private String text;
    @Column(name = "token_offset")
    private long offset;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ResourceCandidate> resourceCandidates;

    public SemanticAnnotation() {
    }

    public SemanticAnnotation(String text, long offset, List<ResourceCandidate> resourceCandidates) {
        this.text = text;
        this.offset = offset;
        this.resourceCandidates = resourceCandidates;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public List<ResourceCandidate> getResourceCandidates() {
        return resourceCandidates;
    }

    public void setResourceCandidates(List<ResourceCandidate> resourceCandidates) {
        this.resourceCandidates = resourceCandidates;
    }
}
