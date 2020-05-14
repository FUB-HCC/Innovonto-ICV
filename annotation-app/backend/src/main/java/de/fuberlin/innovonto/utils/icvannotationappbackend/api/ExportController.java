package de.fuberlin.innovonto.utils.icvannotationappbackend.api;

import de.fuberlin.innovonto.utils.common.vocabulary.MTURK;
import de.fuberlin.innovonto.utils.icvannotationappbackend.services.AnnotationExportService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.StringWriter;

import static de.fuberlin.innovonto.utils.common.JsonLDUtils.framedJsonLdOutput;

@RestController
@RequestMapping("/api/management/export")
@CrossOrigin(origins = {"http://localhost:8004", "http://localhost:9500", "https://i2m-research.imp.fu-berlin.de"})
public class ExportController {
    private final AnnotationExportService annotationExportService;

    @Autowired
    public ExportController(AnnotationExportService annotationExportService) {
        this.annotationExportService = annotationExportService;
    }

    @GetMapping(value = "/usableAnnotationsForProject/xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String getUsableAnnotationsForProjectAsXml(@RequestParam(value = "annotationProjectId") String annotationProjectId,
                                                      @RequestParam(value = "includeIdeaDetails", required = false) Boolean includeIdeaDetails) {
        if (includeIdeaDetails == null) {
            includeIdeaDetails = false;
        }
        final Model ratingsModel = annotationExportService.exportUsableAnnotations(annotationProjectId, includeIdeaDetails);
        final StringWriter graphWriter = new StringWriter();
        RDFDataMgr.write(graphWriter, ratingsModel, RDFFormat.RDFXML_PRETTY);
        return graphWriter.toString();
    }

    @GetMapping(value = "/usableAnnotationsForProject", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUsableAnnotationsForProjectAsJson(@RequestParam(value = "annotationProjectId") String annotationProjectId,
                                                       @RequestParam(value = "includeIdeaDetails", required = false) Boolean includeIdeaDetails) {
        if (includeIdeaDetails == null) {
            includeIdeaDetails = false;
        }
        final Model ratingsModel = annotationExportService.exportUsableAnnotations(annotationProjectId, includeIdeaDetails);
        return framedJsonLdOutput(ratingsModel, MTURK.mturkSession.toString());
    }

}
