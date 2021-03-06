package de.fuberlin.innovonto.utils.icvannotationappbackend;

import de.fuberlin.innovonto.utils.batchmanager.api.BatchService;
import de.fuberlin.innovonto.utils.batchmanager.api.ProjectService;
import de.fuberlin.innovonto.utils.batchmanager.api.SubmissionService;
import de.fuberlin.innovonto.utils.batchmanager.services.BatchAllocationService;
import de.fuberlin.innovonto.utils.batchmanager.services.SubmissionResultService;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.*;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public CommandLineRunner createTestData(AnnotationProjectRepository projectRepository, ChallengeRepository challengeRepository) {
        return (args) -> {
            if (projectRepository.findById("mockproject").isEmpty()) {
                final AnnotationProject mockProject = new AnnotationProject("mockproject");
                mockProject.setCompensation(2.0d);
                mockProject.setEstimatedTimeInMinutes(10);
                mockProject.setBatchSize(2);
                //Add Ideas
                final List<Idea> ideas = new ArrayList<>();
                ideas.add(new Idea(UUID.fromString("38aa2640-6efb-49ee-afd7-fb7a786cb406"), "Analyze athletes with bionic radar to see what part of them is either helping or hurting them in things such as running", "https://innovonto-core.imp.fu-berlin.de/entities/ideaContests/mockChallenge"));
                ideas.add(new Idea(UUID.fromString("54764cfe-83f4-4a16-b9a0-2294019522dc"), "Use bionic radar to identify fouls or false starts in sporting events.", "https://innovonto-core.imp.fu-berlin.de/entities/ideaContests/mockChallenge"));
                final List<AnnotationBatch> mockBatches = new ArrayList<>();
                mockBatches.add(new AnnotationBatch(ideas));
                mockBatches.add(new AnnotationBatch(ideas));
                mockProject.setBatches(mockBatches);
                projectRepository.save(mockProject);
                //Add Challenge
                final Challenge mxBionicRadar = new Challenge("https://innovonto-core.imp.fu-berlin.de/entities/ideaContests/mockChallenge","This is a mock challenge description");
                challengeRepository.save(mxBionicRadar);
            }
        };
    }
}
