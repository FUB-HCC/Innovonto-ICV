package de.fuberlin.innovonto.icv.demo.annotator.api;

import de.fuberlin.innovonto.icv.demo.annotator.AnnotatorConfigService;
import de.fuberlin.innovonto.icv.demo.annotator.model.AnnotatorConfig;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.WriterDatasetRIOT;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.util.Context;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;


@Controller
@RequestMapping("/api/annotator/export")
public class ResultExportController {
    private final AnnotatorConfigService annotatorConfigService;

    @Autowired
    public ResultExportController(AnnotatorConfigService annotatorConfigService) {
        this.annotatorConfigService = annotatorConfigService;
    }

    //XML Export
    @GetMapping(value = "/annotation-results", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String getApprovedIdeasForChallenge(@RequestParam Long configId) {
        AnnotatorConfig config = annotatorConfigService.getById(configId);
        Model model = config.getInputGraph();
        //TODO add all annotations to the model.

        final StringWriter graphWriter = new StringWriter();
        RDFDataMgr.write(graphWriter, model, RDFFormat.RDFXML_PRETTY);
        return graphWriter.toString();
    }

    //Json-LD Export
    @GetMapping(value = "/annotation-results/json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getApprovedIdeasForChallengeAsJson(@RequestParam Long configId) {
        AnnotatorConfig config = annotatorConfigService.getById(configId);
        Model model = config.getInputGraph();
        //TODO add all annotations to the model.
        //TODO only use framed output, if there is an "itemType"
        return framedJsonLdOutput(model, "http://purl.org/gi2mo/ns#Idea");
    }

    private String framedJsonLdOutput(Model ideasModel, String resourceType) throws JSONException {
        final StringWriter graphWriter = new StringWriter();

        RDFDataMgr.write(graphWriter, ideasModel, RDFFormat.JSONLD_FLAT);
        final String jsonldOutput = graphWriter.toString();
        final JSONObject jenaJsonLd = new JSONObject(jsonldOutput);
        final JSONObject frameObject = new JSONObject();

        // only output the ideas using a frame: enables nesting
        frameObject.put("@type", resourceType);
        frameObject.put("@context", jenaJsonLd.getJSONObject("@context"));

        final DatasetGraph g = DatasetFactory.wrap(ideasModel).asDatasetGraph();
        final JsonLDWriteContext ctx = new JsonLDWriteContext();
        ctx.setFrame(frameObject.toString());


        System.out.println("\n--- Using frame to select resources to be output: only output gi2mo:ideas ---");


        System.out.println("Done.");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(baos, g, RDFFormat.JSONLD_FRAME_PRETTY, ctx);

        return baos.toString(StandardCharsets.UTF_8);
    }

    public static void write(OutputStream out, DatasetGraph g, RDFFormat f, Context ctx) {
        // create a WriterDatasetRIOT with the RDFFormat
        WriterDatasetRIOT w = RDFDataMgr.createDatasetWriter(f);
        PrefixMap pm = RiotLib.prefixMap(g);
        String base = null;
        w.write(out, g, pm, base, ctx);
    }

}
