package de.fuberlin.innovonto.utils.common;

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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class JsonLDUtils {

    private JsonLDUtils() {
    }

    public static String framedJsonLdOutput(Model inputModel, String frameByResourceType) throws JSONException {
        final StringWriter graphWriter = new StringWriter();
        RDFDataMgr.write(graphWriter, inputModel, RDFFormat.JSONLD_FLAT);

        final String jsonldOutput = graphWriter.toString();
        final JSONObject jenaJsonLd = new JSONObject(jsonldOutput);
        final JSONObject frameObject = new JSONObject();

        // only output the ideas using a frame: enables nesting
        frameObject.put("@type", frameByResourceType);
        frameObject.put("@context", jenaJsonLd.getJSONObject("@context"));

        final DatasetGraph g = DatasetFactory.wrap(inputModel).asDatasetGraph();
        final JsonLDWriteContext ctx = new JsonLDWriteContext();
        ctx.setFrame(frameObject.toString());


        System.out.println("\n--- Using frame to select resources to be output: only output: " + frameByResourceType);


        System.out.println("Done.");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //TODO this throws a NPE for empty models?
        /*
        Caused by: java.lang.NullPointerException: null
            at com.github.jsonldjava.core.JsonLdApi.filterNodes(JsonLdApi.java:1699) ~[jsonld-java-0.12.5.jar:na]
            at com.github.jsonldjava.core.JsonLdApi.frame(JsonLdApi.java:1404) ~[jsonld-java-0.12.5.jar:na]
            at com.github.jsonldjava.core.JsonLdApi.frame(JsonLdApi.java:1355) ~[jsonld-java-0.12.5.jar:na]
            at com.github.jsonldjava.core.JsonLdProcessor.frame(JsonLdProcessor.java:324) ~[jsonld-java-0.12.5.jar:na]
            at org.apache.jena.riot.writer.JsonLDWriter.toJsonLDJavaAPI(JsonLDWriter.java:224) ~[jena-arq-3.14.0.jar:3.14.0]
            at org.apache.jena.riot.writer.JsonLDWriter.serialize(JsonLDWriter.java:179) ~[jena-arq-3.14.0.jar:3.14.0]
            at org.apache.jena.riot.writer.JsonLDWriter.write(JsonLDWriter.java:140) ~[jena-arq-3.14.0.jar:3.14.0]
            at org.apache.jena.riot.writer.JsonLDWriter.write(JsonLDWriter.java:146) ~[jena-arq-3.14.0.jar:3.14.0]
            at de.fuberlin.innovonto.utils.common.JsonLDUtils.write(JsonLDUtils.java:63) ~[main/:na]
            at de.fuberlin.innovonto.utils.common.JsonLDUtils.framedJsonLdOutput(JsonLDUtils.java:46) ~[main/:na]
         */
        write(baos, g, RDFFormat.JSONLD_FRAME_PRETTY, ctx);

        return baos.toString(StandardCharsets.UTF_8);
    }


    public static void write(OutputStream out, DatasetGraph g, RDFFormat f, Context ctx) {
        // create a WriterDatasetRIOT with the RDFFormat
        //TODO createDatasetWriter is deprecated
        //RDFWriter.create().format(serialization).source(datasetGraph).build()}
        /*RDFWriter build = RDFWriter.create()
                .format(f)
                .source(g)
                .build();*/
        WriterDatasetRIOT w = RDFDataMgr.createDatasetWriter(f);
        PrefixMap pm = RiotLib.prefixMap(g);
        String base = null;
        w.write(out, g, pm, base, ctx);
    }

}
