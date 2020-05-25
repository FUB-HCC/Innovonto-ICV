package de.fuberlin.innovonto.utils.icvannotationappbackend.api.client.annotation;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProxyAnnotationService implements AnnotationService {
    private static final Logger log = LoggerFactory.getLogger(ProxyAnnotationService.class);

    @Value("${innovonto.icv.backend.server}")
    private String server = "http://localhost:4000";

    private final String confidence = "0.001";
    private final String endpoint = "/api/candidates?confidence=" + confidence + "&backend=all&text=";

    private final LoadingCache<String, String> annotationCache;
    private final RestTemplate restClient;


    @Autowired
    public ProxyAnnotationService() {
        //TODO pre-warm cache?
        this.restClient = new RestTemplate();
        this.annotationCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .build(key -> {
                    ResponseEntity<String> responseEntity = restClient.getForEntity(server + endpoint + key, String.class);
                    return responseEntity.getBody();
                });
    }

    @Override
    public String getResponse(String input) {
        //TODO caching based on md5
        log.info("Getting Response from: " + server + endpoint + input);
        String response = annotationCache.get(input);
        log.info("Got Response: " + StringUtils.truncate(response,100));
        return response;
    }
}
