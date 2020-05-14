package de.fuberlin.innovonto.utils.common.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class INOV {

    public static final String INNOVONTO_CORE_PREFIX = "https://innovonto-core.imp.fu-berlin.de/entities/";

    public static String IDEA_PREFIX = INNOVONTO_CORE_PREFIX + "ideas/";
    public static String SESSION_PREFIX = INNOVONTO_CORE_PREFIX + "sessions/";

    public static String USER_PREFIX = INNOVONTO_CORE_PREFIX + "users/";

    public static String SUBMISSION_METHOD_PREFIX = INNOVONTO_CORE_PREFIX + "submissionMethods/";
    public static String CHALLENGE_PREFIX = INNOVONTO_CORE_PREFIX + "challenges/";
    public static String IDEA_CONTEST_PREFIX = INNOVONTO_CORE_PREFIX + "ideaContests/";

    //Session
    public static String TRACKING_EVENT_PREFIX = INNOVONTO_CORE_PREFIX + "trackingEvents/";
    public static String EVENT_TYPE_PREFIX = INNOVONTO_CORE_PREFIX + "eventTypes/";
    public static String SURVEY_PREFIX = INNOVONTO_CORE_PREFIX + "surveys/";

    //ICV
    public static String ANNOTATION_CANDIDATE_PREFIX = INNOVONTO_CORE_PREFIX + "annotationCandidates/";
    public static String RESOURCE_CANDIDATE_PREFIX = INNOVONTO_CORE_PREFIX + "resourceCandidates/";
    public static String INSPIRATION_PREFIX = INNOVONTO_CORE_PREFIX + "inspirations/";

    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri = "http://purl.org/innovonto/types/#";

    private static final Model m = ModelFactory.createDefaultModel();

    public static final Resource Challenge = m.createResource(uri + "Challenge");
    public static final Resource ResearchDescription = m.createResource(uri + "ResearchDescription");
    public static final Resource TextualRefinement = m.createResource(uri + "TextualRefinement");

    public static final Property icon = m.createProperty(uri, "icon");
    public static final Property hasChallenge = m.createProperty(uri, "hasChallenge");
    public static final Property ideaType = m.createProperty(uri,"ideaType");
    public static final Property assignedTo = m.createProperty(uri, "assignedTo");
    public static final Property hasTextualRefinement = m.createProperty(uri, "hasTextualRefinement");
    public static final Property inspiredBy = m.createProperty(uri, "inspiredBy");

    //Block ICV:
    public static final String CONCEPT_PREFIX = INNOVONTO_CORE_PREFIX + "concepts/";
    public static final Property hasAnnotation = m.createProperty(uri, "hasAnnotation");

    //Term Handling
    public static final Property text = m.createProperty(uri, "text");
    public static final Property offset = m.createProperty(uri, "offset");

    //Block Annotation Candidate
    public static final Resource AnnotationCandidate = m.createResource(uri + "AnnotationCandidate");
    public static final Property annotationState = m.createProperty(uri, "annotationState");
    public static final Property hasResourceCandidate = m.createProperty(uri, "hasResourceCandidate");

    //Block Resource Candidate
    public static final Resource ResourceCandidate = m.createResource(uri + "ResourceCandidate");
    public static final Property hasLinkedResource = m.createProperty(uri, "hasLinkedResource");
    public static final Property source = m.createProperty(uri, "source");
    public static final Property confidence = m.createProperty(uri, "confidence");
    public static final Property selected = m.createProperty(uri, "selected");

    //IdeaContest
    public static final Property hasResearchDescription = m.createProperty(uri, "hasResearchDescription");

    //Session
    public static final Resource BrainstormingSession = m.createProperty(uri,"BrainstormingSession");
    public static final Property hasTrackingEvent = m.createProperty(uri, "hasTrackingEvent");
    public static final Property started = m.createProperty(uri, "started");
    public static final Property finished = m.createProperty(uri, "finished");

    public static final Resource TrackingEvent = m.createProperty(uri,"TrackingEvent");

    public static final Property serverTimestamp = m.createProperty(uri, "serverTimestamp");
    public static final Property timerValue = m.createProperty(uri, "timerValue");
    //Idea Submission Tracking Eventas
    public static final Property hasTargetIdea = m.createProperty(uri, "hasTargetIdea");


    public static final Property numberInSession = m.createProperty(uri, "numberInSession");
    public static final Property timeSinceLastIdea = m.createProperty(uri, "timeSinceLastIdea");
    public static final Property timerValueInSession = m.createProperty(uri, "timerValueInSession");
    public static final Property formattedTimeSinceLastIdea = m.createProperty(uri, "formattedTimeSinceLastIdea");

    //User Metadata
    public static final Resource Ideator = m.createResource(uri + "Ideator");

    public static final Property hasSubmittedIdeasForIdeaContest = m.createProperty(uri, "hasSubmittedIdeasForIdeaContest");
    public static final Property hasSubmittedIdeasForChallenge = m.createProperty(uri, "hasSubmittedIdeasForChallenge");
    public static final Property isCreatorOf = m.createProperty(uri, "isCreatorOf");
    public static final Property hasBrainstormingSession = m.createProperty(uri,"hasBrainstormingSession");

    public static final Property age = m.createProperty(uri, "age");
    public static final Property gender = m.createProperty(uri, "gender");
    public static final Property nationality = m.createProperty(uri, "nationality");
    public static final Property occupationalGroup = m.createProperty(uri, "occupationalGroup");
    public static final Property occupation = m.createProperty(uri, "occupation");
    public static final Property highestDegree = m.createProperty(uri, "highestDegree");

    //inspirations
    public static final Resource StaticInspiration = m.createResource(uri + "StaticInspiration");
    public static final Resource ImageInspiration = m.createResource(uri + "ImageInspiration");
    public static final Resource TextInspiration = m.createResource(uri + "TextInspiration");
    public static final Resource Exemplar = m.createResource(uri + "Exemplar");

    public static final Property hasInspiration = m.createProperty(uri, "hasInspiration");

    //Survey Answers
    public static final Property acceptTime = m.createProperty(uri, "acceptTime");
    public static final Property submitTime = m.createProperty(uri, "submitTime");
    public static final Property survey = m.createProperty(uri, "survey");

    public static final Property hasAnswer = m.createProperty(uri, "hasAnswer");
    public static final Property surveyQuestion = m.createProperty(uri, "surveyQuestion");
    public static final Property surveyAnswer = m.createProperty(uri, "surveyAnswer");

    //TODO categories: aufräumen. Wird benutzt in IwmElaboratorIdeaConverter
    public static final Property hasSubCategory = m.createProperty(uri, "hasSubCategory");
}
