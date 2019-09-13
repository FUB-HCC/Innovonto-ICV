package de.fuberlin.innovonto.icv.demo.annotator.model;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public class AnnotatorConfig {
    private long id;

    private Model inputGraph;
    private List<AnnotatableResource> annotatableResourceList;

    private int offset = 0;


    private String conceptRepresentation;
    private String sortBy;
    private String autoAnnotation;

    private String annotationEndpoint;
    private String redirectAfterProcess;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Model getInputGraph() {
        return inputGraph;
    }

    public void setInputGraph(Model inputGraph) {
        this.inputGraph = inputGraph;
    }

    public List<AnnotatableResource> getAnnotatableResourceList() {
        return annotatableResourceList;
    }

    public void setAnnotatableResourceList(List<AnnotatableResource> annotatableResourceList) {
        this.annotatableResourceList = annotatableResourceList;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getAnnotationEndpoint() {
        return annotationEndpoint;
    }

    public void setAnnotationEndpoint(String annotationEndpoint) {
        this.annotationEndpoint = annotationEndpoint;
    }

    public String getConceptRepresentation() {
        return conceptRepresentation;
    }

    public void setConceptRepresentation(String conceptRepresentation) {
        this.conceptRepresentation = conceptRepresentation;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getAutoAnnotation() {
        return autoAnnotation;
    }

    public void setAutoAnnotation(String autoAnnotation) {
        this.autoAnnotation = autoAnnotation;
    }

    public String getRedirectAfterProcess() {
        return redirectAfterProcess;
    }

    public void setRedirectAfterProcess(String redirectAfterProcess) {
        this.redirectAfterProcess = redirectAfterProcess;
    }
}
