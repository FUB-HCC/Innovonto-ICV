package de.fuberlin.innovonto.utils.common.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * This data model (Modeling an Ontology Namespace as a bunch of static properties is taken from:
 * org.apache.jena.vocabulary.*
 */
public class GI2MO {
    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri = "http://purl.org/gi2mo/ns#";

    private static final Model m = ModelFactory.createDefaultModel();

    public static final Resource Idea = m.createResource(uri + "Idea");

    public static final Property content = m.createProperty(uri, "content");
    public static final Property hasCreator = m.createProperty(uri, "hasCreator");
    public static final Property hasIdeaContest = m.createProperty(uri, "hasIdeaContest");
    public static final Property hasSubmissionMethod = m.createProperty(uri, "hasSubmissionMethod");
    public static final Property hasContributor = m.createProperty(uri, "hasContributor");
    public static final Property hasCategory = m.createProperty(uri, "hasCategory");

    public static final Property hasStatus = m.createProperty(uri, "hasStatus");
    public static final Property isCurrentVersion = m.createProperty(uri, "isCurrentVersion");
    public static final Property versionInfo = m.createProperty(uri, "versionInfo");

    //Review
    public static final Resource MinMaxRating = m.createResource(uri + "MinMaxRating");
    public static final Resource TextualReview = m.createResource(uri + "TextualReview");
    public static final Property hasReview = m.createProperty(uri, "hasReview");

    public static final Property ratingValue = m.createProperty(uri, "ratingValue");
    public static final Property maxRatingValue = m.createProperty(uri, "maxRatingValue");
    public static final Property minRatingValue = m.createProperty(uri, "minRatingValue");

    //IdeaContest
    public static final Resource IdeaContest = m.createResource(uri + "IdeaContest");
    public static final Property startDate = m.createProperty(uri, "startDate");
    public static final Property endDate = m.createProperty(uri, "endDate");

}
