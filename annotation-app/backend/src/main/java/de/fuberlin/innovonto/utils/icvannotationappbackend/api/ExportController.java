package de.fuberlin.innovonto.utils.icvannotationappbackend.api;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/management/export")
@CrossOrigin(origins = {"http://localhost:8004", "http://localhost:9500", "https://i2m-research.imp.fu-berlin.de"})
public class ExportController {

    @GetMapping(value = "/usableAnnotationsForProject/xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String getUsableRatingsForProjectAsXml(@RequestParam(value = "annotationProjectId") String annotationProjectId) {
        //TODO implement
        return "not implemented";
    }

    @GetMapping(value = "/usableRatingsForProject", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUsableRatingsForProjectAsJson(@RequestParam(value = "annotationProjectId") String ratingProjectId) {
        //TODO implement
        return "not implemented";
    }

}
