package de.fuberlin.innovonto.utils.icvannotationappbackend.api.client;

import de.fuberlin.innovonto.utils.common.web.MturkSesssionInformationMissingException;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.MturkAnnotationSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequestMapping("/api/mturk/")
@CrossOrigin(origins = {"http://localhost:8004", "http://localhost:9500", "https://i2m-research.imp.fu-berlin.de"})
public class MturkClientRestController {
    private static final Logger log = LoggerFactory.getLogger(MturkClientRestController.class);

    //TODO Metadata-Endpoint

    @ResponseBody
    @GetMapping(value = "/annotation/batch")
    public AnnotationBatchDTO getRatingPairsFor(@RequestParam String ratingProjectId, @RequestParam String hitId, @RequestParam String workerId, @RequestParam String assignmentId) {
        //TODO implement.
        return new AnnotationBatchDTO();
    }

    @PostMapping(value = "/annotation/submit")
    @CrossOrigin(
            origins = "*",
            allowedHeaders = "*",
            methods = {RequestMethod.GET, RequestMethod.POST})
    public MturkAnnotationSession submitRatingTask(@RequestBody MturkAnnotationSessionResultDTO submissionData) {
        if (submissionData == null || isBlank(submissionData.getHitId()) || isBlank(submissionData.getAssignmentId()) || isBlank(submissionData.getWorkerId())) {
            throw new MturkSesssionInformationMissingException("Could not find mturk session information (HWA) on the submissionData object.");
        }
        //TODO implement.
        return new MturkAnnotationSession();
    }

    //Debug View to See Mturk Submit Data:
    @ResponseBody
    @PostMapping(value = "/externalSubmit", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(methods = RequestMethod.POST)
    public String submitHITDebug(HttpServletRequest request) {
        final JSONObject result = new JSONObject();
        for (Map.Entry<String, String[]> parameter : request.getParameterMap().entrySet()) {
            result.put(parameter.getKey(), Arrays.toString(parameter.getValue()));
        }
        return result.toString();
    }
}
