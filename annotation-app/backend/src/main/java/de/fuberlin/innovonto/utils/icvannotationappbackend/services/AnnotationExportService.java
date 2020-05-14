package de.fuberlin.innovonto.utils.icvannotationappbackend.services;

import de.fuberlin.innovonto.utils.batchmanager.api.SubmissionState;
import de.fuberlin.innovonto.utils.common.vocabulary.GI2MO;
import de.fuberlin.innovonto.utils.common.vocabulary.INOV;
import de.fuberlin.innovonto.utils.common.vocabulary.MTURK;
import de.fuberlin.innovonto.utils.common.vocabulary.OID;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.*;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static de.fuberlin.innovonto.utils.common.vocabulary.INOV.ANNOTATION_CANDIDATE_PREFIX;

@Service
public class AnnotationExportService {
    private static final Logger log = LoggerFactory.getLogger(AnnotationExportService.class);

    private final MturkAnnotationSessionRepository mturkAnnotationSessionRepository;
    private final IdeaRepository ideaRepository;

    @Autowired
    public AnnotationExportService(MturkAnnotationSessionRepository mturkAnnotationSessionRepository, IdeaRepository ideaRepository) {
        this.mturkAnnotationSessionRepository = mturkAnnotationSessionRepository;
        this.ideaRepository = ideaRepository;
    }


    public Model exportUsableAnnotations(String projectId, boolean includeIdeaDetails) {
        final Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("gi2mo", GI2MO.uri);
        model.setNsPrefix("dcterms", DCTerms.getURI());
        model.setNsPrefix("inov", INOV.uri);
        model.setNsPrefix("mturk", MTURK.uri);
        model.setNsPrefix("oid", OID.uri);

        Iterable<MturkAnnotationSession> usableSessions = mturkAnnotationSessionRepository.findAllByProjectIdAndSubmissionState(projectId, SubmissionState.USABLE);
        final Set<UUID> localIdeaIds = new HashSet<>();
        for (MturkAnnotationSession session : usableSessions) {
            //TODO this leads to duplicates in the framed output:
            //Block Session
            final Resource outputSession = model.createResource(OID.MTURK_SESSION_PREFIX + session.getId());
            outputSession.addProperty(RDF.type, MTURK.mturkSession);
            outputSession.addProperty(MTURK.hitId, MTURK.HIT_PREFIX + session.getHitId());
            outputSession.addProperty(MTURK.workerId, MTURK.WORKER_PREFIX + session.getWorkerId());
            outputSession.addProperty(MTURK.assignmentId, MTURK.ASSIGNMENT_PREFIX + session.getAssignmentId());
            outputSession.addProperty(OID.hasRatingProject, OID.RATING_PROJECT_PREFIX + session.getProjectId());

            outputSession.addProperty(DCTerms.created, session.getSubmitted().format(DateTimeFormatter.ISO_DATE_TIME), XSDDatatype.XSDdateTime);

            outputSession.addProperty(OID.fulltextFeedback, session.getFulltextFeedback());
            outputSession.addLiteral(OID.clarityRating, session.getClarityRating());

            //Block Annotation
            for (IdeaAnnotation annotatedIdea : session.getAnnotatedIdeas()) {
                if (annotatedIdea.getSubmissionState().equals(SubmissionState.USABLE)) {
                    final UUID localId = annotatedIdea.getSourceIdea().getId();
                    localIdeaIds.add(localId);
                    final Resource outputIdea = model.createResource(INOV.IDEA_PREFIX + localId);
                    outputIdea.addProperty(RDF.type, GI2MO.Idea);
                    for (SemanticAnnotation annotation : annotatedIdea.getAnnotations()) {
                        final Resource outputAnnotation = model.createResource(ANNOTATION_CANDIDATE_PREFIX + annotation.getId());
                        outputAnnotation.addProperty(RDF.type, INOV.AnnotationCandidate);
                        outputAnnotation.addProperty(MTURK.workerId, MTURK.WORKER_PREFIX + session.getWorkerId());
                        outputAnnotation.addProperty(MTURK.assignmentId, MTURK.ASSIGNMENT_PREFIX + session.getAssignmentId());
                        outputAnnotation.addProperty(OID.hasRatingProject, OID.RATING_PROJECT_PREFIX + session.getProjectId());
                        outputAnnotation.addProperty(DCTerms.created, session.getSubmitted().format(DateTimeFormatter.ISO_DATE_TIME), XSDDatatype.XSDdateTime);
                        if (annotation.getResourceCandidates().isEmpty() || annotation.getResourceCandidates().stream().noneMatch(ResourceCandidate::isSelected)) {
                            outputAnnotation.addProperty(INOV.annotationState, "rejected");
                        } else {
                            outputAnnotation.addProperty(INOV.annotationState, "approved");
                        }

                        for (ResourceCandidate resourceCandidate : annotation.getResourceCandidates()) {
                            final Resource outputConcept = model.createResource(INOV.RESOURCE_CANDIDATE_PREFIX + resourceCandidate.getId());
                            outputConcept.addProperty(RDF.type, INOV.ResourceCandidate);
                            outputConcept.addProperty(INOV.text, resourceCandidate.getText());
                            outputConcept.addProperty(INOV.hasLinkedResource, model.createResource(resourceCandidate.getResource()));
                            outputConcept.addProperty(INOV.offset, model.createTypedLiteral(resourceCandidate.getOffset()));
                            outputConcept.addProperty(INOV.selected, model.createTypedLiteral(resourceCandidate.isSelected()));
                            outputConcept.addProperty(INOV.source, resourceCandidate.getSource());
                            outputConcept.addProperty(INOV.confidence, model.createTypedLiteral(resourceCandidate.getConfidence()));
                            outputAnnotation.addProperty(INOV.hasResourceCandidate, outputConcept);
                        }

                        outputIdea.addProperty(INOV.hasAnnotation, outputAnnotation);
                    }
                    outputSession.addProperty(INOV.hasTargetIdea, outputIdea);
                } else {
                    log.info("Skipping Unusable Annotation: " + annotatedIdea);
                }

            }

            //Block Ideas
            //TODO not included in framed output?
            if (includeIdeaDetails) {
                for (Idea idea : ideaRepository.findAllById(localIdeaIds)) {
                    final Resource outputIdea = model.createResource(INOV.IDEA_PREFIX + idea.getId());
                    outputIdea.addProperty(RDF.type, GI2MO.Idea);
                    outputIdea.addProperty(GI2MO.content, idea.getContent());
                    outputIdea.addProperty(GI2MO.hasIdeaContest, idea.getHasIdeaContest());
                }
            }
        }
        return model;
    }
}
