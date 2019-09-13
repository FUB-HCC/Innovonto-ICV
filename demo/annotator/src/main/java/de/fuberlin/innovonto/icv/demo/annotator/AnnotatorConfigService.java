package de.fuberlin.innovonto.icv.demo.annotator;

import de.fuberlin.innovonto.icv.demo.annotator.model.AnnotatorConfig;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnnotatorConfigService {
    private long nextId = 0;
    private Map<Long, AnnotatorConfig> configCache = new HashMap<>();

    public AnnotatorConfig save(AnnotatorConfig input) {
        long currentId = ++nextId;
        input.setId(currentId);
        configCache.put(currentId, input);
        return input;
    }

    public AnnotatorConfig getById(long id) {
        return configCache.get(id);
    }
}
