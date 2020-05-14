package de.fuberlin.innovonto.utils.common.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * This namespace is used for legacy interactions with the ontoidea platform
 */
public class OID {
    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri = "http://purl.org/innovonto/legacy/types#";
    public static final String ANNOTATION_PREFIX = "http://purl.org/innovonto/i2m-legacy/cscw/annotator-submit/";

    private static final Model m = ModelFactory.createDefaultModel();

    public static final String STATUS_PREFIX = "http://purl.org/innovonto/i2m-legacy/status/";
    public static final Resource IdeaStatus = m.createResource(uri + "IdeaStatus");

    //TODO CeRRI Ideas Stuff
    //TODO maybe group phase stuff? filtered, enhanced, contentGerman, userRole
    public static final Property legacyStatus = m.createProperty(uri, "status");

    //TODO Similarity Ratings
    public static final String MX_MASTER_PREFIX = "http://purl.org/innovonto/mx-master/similarities/";
    public static final String RATING_PREFIX = MX_MASTER_PREFIX + "ratings/";
    public static final String RATING_PROJECT_PREFIX = MX_MASTER_PREFIX + "ratingProjects/";
    public static final String MTURK_SESSION_PREFIX = MX_MASTER_PREFIX + "mturkSessions/";

    public static final Resource SimilarityRating = m.createResource(uri + "SimilarityRating");
    public static final Resource RatingProject = m.createResource(uri + "RatingProject");

    public static final Property hasRatingProject = m.createProperty(uri, "hasRatingProject");
    public static final Property hasAnnotationProject = m.createProperty(uri, "hasAnnotationProject");
    public static final Property hasSimilarityRating = m.createProperty(uri, "hasSimilarityRating");

    public static final Property hasLeftIdea = m.createProperty(uri, "hasLeftIdea");
    public static final Property hasRightIdea = m.createProperty(uri, "hasRightIdea");

    //TODO move to survey-data format
    public static final Property fulltextFeedback = m.createProperty(uri, "fulltextFeedback");
    public static final Property clarityRating = m.createProperty(uri, "clarityRating");

}
