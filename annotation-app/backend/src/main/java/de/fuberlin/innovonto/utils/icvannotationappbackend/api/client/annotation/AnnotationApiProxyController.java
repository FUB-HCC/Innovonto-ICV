package de.fuberlin.innovonto.utils.icvannotationappbackend.api.client.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = {"http://localhost:9002", "http://localhost:9500", "https://i2m-research.imp.fu-berlin.de"})
public class AnnotationApiProxyController {
    private final AnnotationService annotationService;

    @Autowired
    public AnnotationApiProxyController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    //TODO parameters are missing
    @GetMapping(value = "/annotate", produces = MediaType.APPLICATION_JSON_VALUE)
    public String annotate(@RequestParam(name = "text") String text) throws IOException {
        return annotationService.getResponse(text);
    }

}
