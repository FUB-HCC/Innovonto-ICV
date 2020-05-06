package de.fuberlin.innovonto.utils.icvannotationappbackend.api.client;

import de.fuberlin.innovonto.utils.batchmanager.api.Batch;
import de.fuberlin.innovonto.utils.batchmanager.api.Submission;
import de.fuberlin.innovonto.utils.batchmanager.services.BatchAllocationService;
import de.fuberlin.innovonto.utils.batchmanager.services.SubmissionResultService;
import de.fuberlin.innovonto.utils.common.web.MturkSesssionInformationMissingException;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationBatch;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationProject;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.MturkAnnotationSession;
import de.fuberlin.innovonto.utils.icvannotationappbackend.services.JpaProjectService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequestMapping("/api/mturk/")
@CrossOrigin(origins = {"http://localhost:8004", "http://localhost:9500", "https://i2m-research.imp.fu-berlin.de"})
public class MturkClientRestController {
    private static final Logger log = LoggerFactory.getLogger(MturkClientRestController.class);

    private final BatchAllocationService batchAllocationService;
    private final SubmissionResultService submissionResultService;
    private final JpaProjectService projectService;

    @Autowired
    public MturkClientRestController(BatchAllocationService batchAllocationService, SubmissionResultService submissionResultService, JpaProjectService projectService) {
        this.batchAllocationService = batchAllocationService;
        this.submissionResultService = submissionResultService;
        this.projectService = projectService;
    }

    @ResponseBody
    @GetMapping(value = "/projectMetadata")
    public AnnotationProject getProjectMetadata(@RequestParam String projectId) {
        Optional<AnnotationProject> byId = projectService.findById(projectId);
        if (byId.isEmpty()) {
            throw new IllegalArgumentException("No project for id: " + projectId);
        } else {
            return byId.get();
        }
    }

    @ResponseBody
    @GetMapping(value = "/annotation/batch")
    public AnnotationBatchDTO getAnnotationBatchFor(@RequestParam String ratingProjectId, @RequestParam String hitId, @RequestParam String workerId, @RequestParam String assignmentId) {
        if (isBlank(hitId) || isBlank(workerId) || isBlank(assignmentId)) {
            throw new MturkSesssionInformationMissingException("Could not find mturk session information (HWA) on the result object.");
        }

        final Batch batchForCurrentAssignment = batchAllocationService.allocateBatchFor(ratingProjectId, hitId, workerId, assignmentId);
        if (batchForCurrentAssignment instanceof AnnotationBatch) {
            //Build DTO from annotation Batch?
            return new AnnotationBatchDTO();
        } else {
            throw new IllegalStateException("Tried to allocate a batch for a project, where the project does not match the allocation type");
        }
    }

    @PostMapping(value = "/annotation/submit")
    @CrossOrigin(
            origins = "*",
            allowedHeaders = "*",
            methods = {RequestMethod.GET, RequestMethod.POST})
    public Submission submitRatingTask(@RequestBody MturkAnnotationSessionResultDTO submissionData) {
        if (submissionData == null || isBlank(submissionData.getHitId()) || isBlank(submissionData.getAssignmentId()) || isBlank(submissionData.getWorkerId())) {
            throw new MturkSesssionInformationMissingException("Could not find mturk session information (HWA) on the submissionData object.");
        }
        //TODO build a submission from the existing
        Submission savedSubmission = submissionResultService.updateProjectAndBatchAndSave(null);
        //TODO update things?
        return savedSubmission;
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
