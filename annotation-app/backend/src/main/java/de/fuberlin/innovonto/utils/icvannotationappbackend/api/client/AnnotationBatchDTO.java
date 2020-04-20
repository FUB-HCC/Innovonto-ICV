package de.fuberlin.innovonto.utils.icvannotationappbackend.api.client;

import de.fuberlin.innovonto.utils.icvannotationappbackend.model.SemanticAnnotation;

import java.util.List;

public class AnnotationBatchDTO {
    //A List of Ideas that have to be annotated.
    private String id;
    private String content;
    private List<SemanticAnnotation> annotations;
}
