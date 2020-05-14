package de.fuberlin.innovonto.utils.icvannotationappbackend;

import de.fuberlin.innovonto.utils.batchmanager.api.BatchService;
import de.fuberlin.innovonto.utils.batchmanager.api.ProjectService;
import de.fuberlin.innovonto.utils.batchmanager.api.SubmissionService;
import de.fuberlin.innovonto.utils.batchmanager.services.BatchAllocationService;
import de.fuberlin.innovonto.utils.batchmanager.services.SubmissionResultService;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationBatch;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationProject;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationProjectRepository;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.MturkAnnotationSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IcvAnnotationAppBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IcvAnnotationAppBackendApplication.class, args);
    }

    @Bean
    @Autowired
    public BatchAllocationService getBatchAllocationService(ProjectService<AnnotationProject> projectService, BatchService<AnnotationBatch> batchService) {
        return new BatchAllocationService(projectService, batchService);
    }

    @Bean
    @Autowired
    public SubmissionResultService getSubmissionResultService(ProjectService<AnnotationProject> projectService,
                                                              BatchService<AnnotationBatch> batchBatchService,
                                                              SubmissionService<MturkAnnotationSession> submissionService) {
        return new SubmissionResultService(submissionService, projectService, batchBatchService);
    }

    @Bean
    @Autowired
    public CommandLineRunner createTestData(AnnotationProjectRepository projectRepository) {
        return (args) -> {
            if (projectRepository.findById("mockproject").isEmpty()) {
                AnnotationProject mockProject = new AnnotationProject("mockproject");
                mockProject.setCompensation(2.0d);
                mockProject.setEstimatedTimeInMinutes(10);
                mockProject.setBatchSize(1);
                projectRepository.save(mockProject);
            }
        };
    }
}
