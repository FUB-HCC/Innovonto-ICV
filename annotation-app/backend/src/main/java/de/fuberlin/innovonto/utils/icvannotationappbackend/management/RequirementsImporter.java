package de.fuberlin.innovonto.utils.icvannotationappbackend.management;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequirementsImporter {
    private final IdeaRepository ideaRepository;
    private final ChallengeRepository challengeRepository;
    private final AnnotationProjectRepository annotationProjectRepository;
    private final BatchSplitter batchSplitter;

    @Autowired
    public RequirementsImporter(IdeaRepository ideaRepository, ChallengeRepository challengeRepository, AnnotationProjectRepository annotationProjectRepository, BatchSplitter batchSplitter) {
        this.ideaRepository = ideaRepository;
        this.challengeRepository = challengeRepository;
        this.annotationProjectRepository = annotationProjectRepository;
        this.batchSplitter = batchSplitter;
    }

    public AnnotationProjectRequirements importRequirementsFromJson(String jsonInput) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonInput, AnnotationProjectRequirements.class);
    }

    public void validateRequirements(AnnotationProjectRequirements requirements) throws ValidationException {
        //Step 1: Check if already existing id
        if (annotationProjectRepository.findById(requirements.getId()).isPresent()) {
            throw new ValidationException("Validation Failed: Already Existing Project with id: " + requirements.getId());
        }
        //Step 2: Check That all challenges are present
        final List<String> challengeIdsInIdeas = requirements.getIdeas().stream().map(Idea::getHasIdeaContest).collect(Collectors.toList());
        final List<String> challengeIdsInRequirements = requirements.getChallenges().stream().map(Challenge::getId).collect(Collectors.toList());
        for (String challengeId : challengeIdsInIdeas) {
            if (!challengeIdsInRequirements.contains(challengeId)) {
                if (challengeRepository.findById(challengeId).isEmpty()) {
                    throw new ValidationException("Unknown challenge: " + challengeId + " not found in either the requirements.json nor the database");
                }
            }
        }

        //Step 4: Check that resulting batches are balanced (TODO long-term: generify)
        if (!((requirements.getIdeas().size() * requirements.getGoalAnnotationsPerIdea()) % requirements.getBatchSize() == 0)) {
            throw new ValidationException("Estimated Ratings: " + (requirements.getIdeas().size() * requirements.getGoalAnnotationsPerIdea()) + " is not evenly divisible by: " + (requirements.getBatchSize()));
        }
    }

    //TODO FIXME: this will fail on re-upload of a challenge.
    public AnnotationProject saveRequirementsAsProject(AnnotationProjectRequirements requirements) {
        final AnnotationProject result = new AnnotationProject(requirements.getId());
        result.setBatchSize(requirements.getBatchSize());
        result.setEstimatedTimeInMinutes(requirements.getEstimatedTimeInMinutes());
        result.setCompensation(requirements.getCompensation());
        //save challenges
        for (Challenge challenge : requirements.getChallenges()) {
            challengeRepository.save(challenge);
        }

        //save ideas
        for (Idea idea : requirements.getIdeas()) {
            ideaRepository.save(idea);
        }
        List<AnnotationBatch> splittedBatches = batchSplitter.createBatchesFor(requirements);
        result.setBatches(splittedBatches);
        annotationProjectRepository.save(result);
        return result;
    }

}
