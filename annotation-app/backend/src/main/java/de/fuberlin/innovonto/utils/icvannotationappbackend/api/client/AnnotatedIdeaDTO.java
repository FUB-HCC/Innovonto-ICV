package de.fuberlin.innovonto.utils.icvannotationappbackend.api.client;

import de.fuberlin.innovonto.utils.icvannotationappbackend.model.SemanticAnnotation;

import java.util.List;
import java.util.UUID;

public class AnnotatedIdeaDTO {
    private UUID id;
    private String content;
    private List<SemanticAnnotation> annotations;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<SemanticAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<SemanticAnnotation> annotations) {
        this.annotations = annotations;
    }
}
