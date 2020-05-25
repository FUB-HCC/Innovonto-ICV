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
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//For more open API stuff see
//https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations#OpenAPIDefinition
//https://springdoc.org/faq.html#how-can-i-configure-swagger-ui
@OpenAPIDefinition(info = @Info(
        title = "Idea Annotation App Backend",
        version = "0.0.1-SNAPSHOT"),
        servers = {
                @Server(
                        description = "Development",
                        url = "http://localhost:9002"
                ),
                @Server(
                        description = "Live",
                        url = "https://i2m-research.imp.fu-berlin.de"
                )
        })
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
