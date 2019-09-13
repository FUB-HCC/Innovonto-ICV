package de.fuberlin.innovonto.icv.demo.annotator.api;

import de.fuberlin.innovonto.icv.demo.annotator.AnnotatorConfigService;
import de.fuberlin.innovonto.icv.demo.annotator.model.AnnotatorSubmitDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Controller
@RequestMapping("/api/annotator")
public class AnnotatorApiController {
    private static final Logger log = LoggerFactory.getLogger(AnnotatorApiController.class);
    private final AnnotatorConfigService annotatorConfigService;
    private RestTemplate restClient;

    private String server = "http://localhost:4000";
    private final String confidence = "0.001";
    private final String endpoint = "/api/candidates?confidence=" + confidence + "&backend=all&text=";


    public AnnotatorApiController(AnnotatorConfigService annotatorConfigService) {
        this.annotatorConfigService = annotatorConfigService;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String submitAnnotations(@RequestBody AnnotatorSubmitDTO submitData) {
        //TODO handle submit data and save in annotatorConfig
        return "{\"status\":\"ok\"}";
    }

    @GetMapping(value = "/annotate", produces = MediaType.APPLICATION_JSON_VALUE)
    public String annotate(@RequestParam(name = "text") String text) throws IOException {
        //TODO get endpoint url from annotator-config
        ResponseEntity<String> responseEntity = restClient.getForEntity(server + endpoint + text, String.class);
        return responseEntity.getBody();
    }
}
