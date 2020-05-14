package de.fuberlin.innovonto.utils.icvannotationappbackend.management;


import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationBatch;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.Idea;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BatchSplitter {

    public List<AnnotationBatch> createBatchesFor(AnnotationProjectRequirements requirements) {
        final List<AnnotationBatch> result = new ArrayList<>((requirements.getIdeas().size() / requirements.getBatchSize()) * requirements.getGoalAnnotationsPerIdea());
        final AtomicInteger counter = new AtomicInteger();

        //TODO randomize here or not? currently I'm randomizing in the MturkClientRestController
        final Collection<List<Idea>> chunks = requirements.getIdeas().stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / requirements.getBatchSize()))
                .values();
        for (List<Idea> chunk : chunks) {
            for (int i = 0; i < requirements.getGoalAnnotationsPerIdea(); i++) {
                result.add(new AnnotationBatch(chunk));
            }
        }
        return result;
    }
}
