package de.fuberlin.innovonto.icv.demo.annotator.controller;

import de.fuberlin.innovonto.icv.demo.annotator.AnnotatorConfigService;
import de.fuberlin.innovonto.icv.demo.annotator.model.AnnotatableResource;
import de.fuberlin.innovonto.icv.demo.annotator.model.AnnotatorConfig;
import de.fuberlin.innovonto.icv.demo.annotator.model.ConfigurationDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AnnotatorController {
    private static final Logger log = LoggerFactory.getLogger(AnnotatorController.class);
    private final AnnotatorConfigService annotatorConfigService;


    @Autowired
    public AnnotatorController(AnnotatorConfigService annotatorConfigService) {
        this.annotatorConfigService = annotatorConfigService;
    }

    @GetMapping("/")
    public String annotatorStartupScreen(@RequestParam(required = false) Long configId) {
        if(configId != null) {
            //TODO prepoulate fields.
        }
        return "choose-file";
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String buildAnnotatorConfig(@RequestPart MultipartFile inputGraph,
                                       @RequestPart String annotateProperty,
                                       @RequestPart(required = false) String itemType) throws IOException {
        //TODO error handling
        // Step 2: filter unknown filetypes:
        final String contentType = inputGraph.getContentType();
        String lang;
        if (contentType.equals("application/json")) {
            lang = "JSON-LD";
        } else if (contentType.equals("text/xml")) {
            lang = "RDF/XML";
        } else {
            throw new IllegalArgumentException("Unrecognized content type: " + contentType);
        }

        // Step 2: get fileContent as model:
        log.info("Reading File: " + inputGraph.getOriginalFilename());
        //TODO give importer a stringOutput
        final Model model = readFileDataToModel(inputGraph.getInputStream(), lang);
        log.info("Created RDF model from file. Finding entities and their properties to be annotated.");
        Property annotatePropertyResource = model.createProperty(annotateProperty);
        List<AnnotatableResource> resources;
        //TODO implement offset and limit
        if (StringUtils.isNotBlank(itemType)) {
            resources = findAnnotatablesByItemType(itemType, annotatePropertyResource, model);
        } else {
            resources = findAnnotatablesByProperty(model, annotatePropertyResource);
        }

        log.info("Found " + resources.size() + " annotatable entities");
        //Generate ID
        final AnnotatorConfig toSave = new AnnotatorConfig();
        toSave.setInputGraph(model);
        toSave.setAnnotatableResourceList(resources);
        final AnnotatorConfig result = annotatorConfigService.save(toSave);
        //Redirect to configuration site
        return "redirect:/configure?configId=" + result.getId();
    }

    //TODO implement.
    private List<AnnotatableResource> findAnnotatablesByProperty(Model model, Property annotatePropertyResource) {
        throw new RuntimeException("not implemented");
    }

    private List<AnnotatableResource> findAnnotatablesByItemType(String itemType, Property annotatePropertyResource, Model model) {
        final List<AnnotatableResource> result = new ArrayList<>();
        final Resource itemTypeResource = model.createResource(itemType);
        //Step 1: Get all things that are an $itemType:
        final StmtIterator allItemsByType = model.listStatements(null, RDF.type, itemTypeResource);
        int count = 0;
        while (allItemsByType.hasNext()) {
            Statement statement = allItemsByType.nextStatement();
            Resource currentEntity = statement.getSubject();
            //TODO this assumes that the property is always just one.
            Statement property = model.getProperty(currentEntity, annotatePropertyResource);
            result.add(new AnnotatableResource(property.getSubject().toString(), property.getObject().asLiteral().getString()));
        }
        log.info("Found " + count + " annotatable items");
        return result;
    }

    private static Model readFileDataToModel(InputStream inputStream, String lang) {
        final Model model = ModelFactory.createDefaultModel();
        model.read(inputStream, null, lang);
        return model;
    }

    @GetMapping("/configure")
    public String getConfigurationPage(@RequestParam Long configId) {
        AnnotatorConfig config = annotatorConfigService.getById(configId);
        //TODO set id in frontend via model
        return "choose-configuration";
    }

    @PostMapping("/configure")
    public String addConfiguration(@RequestBody ConfigurationDTO configurationDTO) {
        //TODO set json fields in app.
        return "app";
    }
}
