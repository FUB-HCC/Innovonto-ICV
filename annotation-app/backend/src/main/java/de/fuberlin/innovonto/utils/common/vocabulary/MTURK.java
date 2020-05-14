package de.fuberlin.innovonto.utils.common.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class MTURK {

    public static final String WORKER_PREFIX = "http://purl.org/innovonto/mturk/worker/";
    public static final String HIT_PREFIX = "http://purl.org/innovonto/mturk/hit/";
    public static final String ASSIGNMENT_PREFIX = "http://purl.org/innovonto/mturk/assignment/";
    public static final String ANNOTATION_PREFIX = "http://purl.org/innovonto/mturk/annotation/";
    public static final String SURVEY_PREFIX = "http://purl.org/innovonto/mturk/survey/";

    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri = "http://purl.org/innovonto/mturk/";


    private static final Model m = ModelFactory.createDefaultModel();

    public static final Resource HIT = m.createResource(uri + "HIT");
    public static final Resource mturkSession = m.createResource(uri + "MturkSession");
    public static final Resource ANNOTATION = m.createResource(uri + "Annotation");


    public static final Property assignmentId = m.createProperty(uri, "assignmentId");
    public static final Property hitId = m.createProperty(uri, "hitId");
    public static final Property workerId = m.createProperty(uri, "workerId");

    public static final Property hasMturkSession = m.createProperty(uri, "hasMturkSession");

    //Tracking
    public static final Resource BrainstormingSession = m.createResource(uri + "BrainstormingSession");
    public static final Property hasEvent = m.createProperty(uri, "hasEvent");

    public static final Resource TrackingEvent = m.createResource(uri + "TrackingEvent");
    public static final Property serverTimestamp = m.createProperty(uri, "serverTimestamp");
    public static final Property timerValue = m.createProperty(uri, "timerValue");

    public static final Property eventType = m.createProperty(uri, "eventType");
    public static final Property submittedIdea = m.createProperty(uri, "submittedIdea");
    public static final Property annotatorSubmit = m.createProperty(uri, "annotatorSubmit");

    //Survey Answers
    public static final Property acceptTime = m.createProperty(uri, "acceptTime");
    public static final Property submitTime = m.createProperty(uri, "submitTime");
    public static final Property survey = m.createProperty(uri, "survey");

    public static final Property hasAnswer = m.createProperty(uri, "hasAnswer");
    public static final Property hasAnnotationResult = m.createProperty(uri, "hasAnnotationResult");
    public static final Property surveyQuestion = m.createProperty(uri, "surveyQuestion");
    public static final Property surveyAnswer = m.createProperty(uri, "surveyAnswer");
}
